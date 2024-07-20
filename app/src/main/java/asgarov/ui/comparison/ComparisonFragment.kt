package asgarov.ui.comparison


import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import asgarov.elchin.econvis.R
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.Year
import asgarov.elchin.econvis.databinding.FragmentComparisonBinding
import asgarov.elchin.econvis.utils.NetworkUtils
import asgarov.elchin.econvis.utils.RegionExpandableListAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class ComparisonFragment : Fragment() {
    private lateinit var binding: FragmentComparisonBinding
    private val viewModel: ComparisonViewModel by viewModels()
    private val chartData = mutableListOf<Pair<String, Float>>()


    private val selectedCountryIds = mutableListOf<Long>()
    private val selectedIndicatorIds = mutableListOf<Long>()
    private val selectedYearIds = mutableListOf<Long>()
    private lateinit var indicators: List<Indicator>
    private lateinit var years: List<Year>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComparisonBinding.inflate(inflater, container, false)

        viewModel.fetchCountries()
        viewModel.fetchIndicators()
        viewModel.fetchYears()

        setupObservers()
        setupButtons()
        setupChartSwitch()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.countriesByRegion.observe(viewLifecycleOwner, Observer { groupedCountries ->
            val regionList = groupedCountries.keys.toList()
            val countryMap = groupedCountries.mapValues { entry -> entry.value }

            binding.selectCountriesButton.setOnClickListener {
                showRegionSelectDialog("Select Countries", regionList, countryMap)
            }
        })

        viewModel.indicators.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { indicatorsList ->
                indicators = indicatorsList
                Log.d("ComparisonFragment", "Indicators: $indicators")
                val indicatorNames = indicators.map { it.name }.toTypedArray()
                val indicatorIds = indicators.map { it.id }.toLongArray()

                binding.selectIndicatorsButton.setOnClickListener {
                    showSingleSelectDialog("Select Indicator", indicatorNames, indicatorIds, selectedIndicatorIds)
                }
            }.onFailure { throwable ->
                Log.e("ComparisonFragment", "Error fetching indicators", throwable)
            }
        })

        viewModel.years.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { yearsList ->
                years = yearsList
                Log.d("ComparisonFragment", "Years: $years")
                val yearValues = years.map { it.year.toString() }.toTypedArray()
                val yearIds = years.map { it.id }.toLongArray()

                binding.selectYearsButton.setOnClickListener {
                    showMultiSelectDialog("Select Years", yearValues, yearIds, selectedYearIds)
                }
            }.onFailure { throwable ->
                Log.e("ComparisonFragment", "Error fetching years", throwable)
            }
        })

        viewModel.reports.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { reports ->
                if (reports.isNullOrEmpty()) {
                    Log.e("ComparisonFragment", "No data available to display")
                } else {
                    Log.d("ComparisonFragment", "Reports data: $reports")
                    updateChart(reports)
                }
            }.onFailure { throwable ->
                Log.e("ComparisonFragment", "Error fetching reports", throwable)
            }
        })

        viewModel.countryDataValue.observe(viewLifecycleOwner, Observer { value ->
            if (value != null) {
                Log.d("ComparisonFragment", "Fetched country data value: $value")
                // Update the chart data with the fetched value
                val selectedCountryName = selectedCountryIds.first().toString() // Update this to get the actual country name
                val selectedIndicatorName = indicators.find { it.id == selectedIndicatorIds.first() }?.name ?: ""
                val selectedYearValue = years.find { it.id == selectedYearIds.first() }?.year ?: 0

                chartData.add(Pair("$selectedCountryName - $selectedYearValue", value.toFloat()))
                updateChartData()
                Toast.makeText(requireContext(), "Fetched value: $value", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ComparisonFragment", "Failed to fetch country data value")
                Toast.makeText(requireContext(), "Failed to fetch value", Toast.LENGTH_SHORT).show()
            }
        })}


        private fun setupButtons() {
        binding.fetchDataButton.setOnClickListener {
            Log.d("ComparisonFragment", "Selected country IDs: $selectedCountryIds")
            Log.d("ComparisonFragment", "Selected indicator IDs: $selectedIndicatorIds")
            Log.d("ComparisonFragment", "Selected year IDs: $selectedYearIds")

            if (selectedCountryIds.isNotEmpty() && selectedIndicatorIds.isNotEmpty() && selectedYearIds.isNotEmpty()) {
                val selectedCountry = selectedCountryIds.first()
                val selectedIndicatorId = selectedIndicatorIds.first()
                val selectedYearId = selectedYearIds.first()

                val selectedIndicatorName = indicators.find { it.id == selectedIndicatorId }?.name ?: ""
                val selectedYearValue = years.find { it.id == selectedYearId }?.year ?: 0

                Log.d("ComparisonFragment", "Selected country ID: $selectedCountry")
                Log.d("ComparisonFragment", "Selected indicator: $selectedIndicatorName")
                Log.d("ComparisonFragment", "Selected year: $selectedYearValue")

                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    viewModel.fetchReports(ReportRequest(selectedCountryIds, selectedIndicatorIds, selectedYearIds))
                } else {
                    viewModel.fetchCountryDataValue(selectedCountry, selectedIndicatorName, selectedYearValue)
                }
            } else {
                Log.e("ComparisonFragment", "Please select country, indicator, and year.")
                Toast.makeText(requireContext(), "Please select country, indicator, and year.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveChartButton.setOnClickListener {
            val visibleChart = when {
                binding.barChart.visibility == View.VISIBLE -> binding.barChart
                binding.horizontalBarChart.visibility == View.VISIBLE -> binding.horizontalBarChart
                binding.lineChart.visibility == View.VISIBLE -> binding.lineChart
                else -> null
            }
            visibleChart?.let { saveChart(it) }
        }
    }






    private fun setupChartSwitch() {
        binding.barChartIcon.setOnClickListener {
            binding.barChart.visibility = View.VISIBLE
            binding.horizontalBarChart.visibility = View.GONE
            binding.lineChart.visibility = View.GONE
        }

        binding.horizontalBarChartIcon.setOnClickListener {
            binding.barChart.visibility = View.GONE
            binding.horizontalBarChart.visibility = View.VISIBLE
            binding.lineChart.visibility = View.GONE
        }

        binding.lineChartIcon.setOnClickListener {
            binding.barChart.visibility = View.GONE
            binding.horizontalBarChart.visibility = View.GONE
            binding.lineChart.visibility = View.VISIBLE
        }
    }

    private fun showMultiSelectDialog(
        title: String,
        items: Array<String>,
        ids: LongArray,
        selectedIds: MutableList<Long>
    ) {
        val selectedItems = BooleanArray(items.size) { i -> selectedIds.contains(ids[i]) }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMultiChoiceItems(items, selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedIds.add(ids[which])
                } else {
                    selectedIds.remove(ids[which])
                }
            }
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showSingleSelectDialog(
        title: String,
        items: Array<String>,
        ids: LongArray,
        selectedIds: MutableList<Long>
    ) {
        val selectedItemIndex = if (selectedIds.isNotEmpty()) ids.indexOf(selectedIds[0]) else -1
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(items, selectedItemIndex) { dialog, which ->
                selectedIds.clear()
                selectedIds.add(ids[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRegionSelectDialog(
        title: String,
        regions: List<String>,
        countryMap: Map<String, List<Country>>
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_categorized_countries, null)
        val expandableListView = dialogView.findViewById<ExpandableListView>(R.id.expandableListView)
        val adapter = RegionExpandableListAdapter(requireContext(), regions, countryMap, selectedCountryIds)
        expandableListView.setAdapter(adapter)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                updateLineChartIconState()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateLineChartIconState() {
        if (selectedCountryIds.size > 1) {
            binding.lineChartIcon.isEnabled = false
            binding.lineChartIcon.alpha = 0.5f
        } else {
            binding.lineChartIcon.isEnabled = true
            binding.lineChartIcon.alpha = 1.0f
        }
    }

    private fun updateChart(reports: List<Report>) {
        chartData.clear()
        // Existing logic to handle reports data...
        reports.forEach { report ->
            val label = "${report.country.name} - ${report.year.year}"
            chartData.add(Pair(label, report.data.toFloat()))
        }
        updateChartData()
    }

    private fun updateChartData() {
        val barChart: BarChart = binding.barChart
        val horizontalBarChart: HorizontalBarChart = binding.horizontalBarChart
        val lineChart: LineChart = binding.lineChart

        // Clear previous data
        barChart.clear()
        horizontalBarChart.clear()
        lineChart.clear()

        // Determine the current theme
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val textColor = if (isDarkMode) resources.getColor(R.color.white, null) else resources.getColor(R.color.black, null)
        val backgroundColor = if (isDarkMode) resources.getColor(R.color.black, null) else resources.getColor(R.color.white, null)

        val barEntries = ArrayList<BarEntry>()
        val horizontalBarEntries = ArrayList<BarEntry>()
        val lineEntries = ArrayList<Entry>()
        val xLabels = ArrayList<String>()

        chartData.forEachIndexed { index, data ->
            barEntries.add(BarEntry(index.toFloat(), data.second))
            horizontalBarEntries.add(BarEntry(index.toFloat(), data.second))
            lineEntries.add(Entry(index.toFloat(), data.second))
            xLabels.add(data.first)
        }

        // Set up BarChart
        val barDataSet = BarDataSet(barEntries, "Inequality Comparison")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        barDataSet.valueTextColor = textColor
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Customize BarChart appearance
        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(60)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.setBackgroundColor(backgroundColor)

        val barXAxis = barChart.xAxis
        barXAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        barXAxis.granularity = 1f
        barXAxis.position = XAxis.XAxisPosition.BOTTOM
        barXAxis.setDrawLabels(true)
        barXAxis.labelRotationAngle = -45f
        barXAxis.textColor = textColor

        // Set up HorizontalBarChart
        val horizontalBarDataSet = BarDataSet(horizontalBarEntries, "Inequality Comparison")
        horizontalBarDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        horizontalBarDataSet.valueTextColor = textColor
        val horizontalBarData = BarData(horizontalBarDataSet)
        horizontalBarChart.data = horizontalBarData

        // Customize HorizontalBarChart appearance
        horizontalBarChart.description.isEnabled = false
        horizontalBarChart.setDrawValueAboveBar(true)
        horizontalBarChart.setMaxVisibleValueCount(60)
        horizontalBarChart.setPinchZoom(false)
        horizontalBarChart.setDrawGridBackground(false)
        horizontalBarChart.setBackgroundColor(backgroundColor)

        val horizontalBarXAxis = horizontalBarChart.xAxis
        horizontalBarXAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        horizontalBarXAxis.granularity = 1f
        horizontalBarXAxis.position = XAxis.XAxisPosition.BOTTOM
        horizontalBarXAxis.setDrawLabels(true)
        horizontalBarXAxis.labelRotationAngle = -45f
        horizontalBarXAxis.textColor = textColor

        // Set up LineChart
        val lineDataSet = LineDataSet(lineEntries, "Inequality Comparison")
        lineDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        lineDataSet.valueTextColor = textColor
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize LineChart appearance
        lineChart.description.isEnabled = false
        lineChart.setDrawGridBackground(false)
        lineChart.setBackgroundColor(backgroundColor)

        val lineXAxis = lineChart.xAxis
        lineXAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        lineXAxis.granularity = 1f
        lineXAxis.position = XAxis.XAxisPosition.BOTTOM
        lineXAxis.setDrawLabels(true)
        lineXAxis.labelRotationAngle = -45f
        lineXAxis.textColor = textColor

        // Refresh the charts
        barChart.invalidate()
        horizontalBarChart.invalidate()
        lineChart.invalidate()

        barChart.legend.isEnabled = false
        horizontalBarChart.legend.isEnabled = false
        lineChart.legend.isEnabled = false
    }


    private fun saveChart(chart: View) {
        val bitmap = Bitmap.createBitmap(chart.width, chart.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        chart.draw(canvas)

        val file = File(requireContext().filesDir, "chart_${System.currentTimeMillis()}.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val sharedPreferences = requireContext().getSharedPreferences("saved_charts", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val filePaths = sharedPreferences.getStringSet("file_paths", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        filePaths.add(file.absolutePath)
        editor.putStringSet("file_paths", filePaths)
        editor.apply()

        Log.d("ComparisonFragment", "Chart saved to ${file.absolutePath}")
    }
}