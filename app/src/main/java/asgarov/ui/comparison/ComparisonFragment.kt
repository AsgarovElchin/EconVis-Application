package asgarov.ui.comparison

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import asgarov.elchin.econvis.R
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.Year
import asgarov.elchin.econvis.databinding.FragmentComparisonBinding
import asgarov.elchin.econvis.utils.NetworkStatusHelper
import asgarov.elchin.econvis.utils.NetworkUtils
import asgarov.elchin.econvis.utils.RegionExpandableListAdapter
import asgarov.elchin.econvis.utils.SnackbarUtils
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

        NetworkStatusHelper.instance.networkStatus.observe(viewLifecycleOwner, Observer { isOnline ->
            val previousStatus = NetworkStatusHelper.instance.getPreviousNetworkStatus()
            Log.d("WorldMapFragment", "Network status observed: isOnline=$isOnline, previousStatus=$previousStatus")
            val message = when {
                isOnline && previousStatus == false -> "You are in online mode"
                !isOnline && previousStatus == true -> "You are in offline mode"
                else -> null
            }
            message?.let {
                Log.d("WorldMapFragment", "Showing Snackbar: $it")
                SnackbarUtils.showCustomSnackbar(binding.root, it, android.R.color.darker_gray)
            }
        })
        return binding.root
    }

    private fun setupObservers() {
        viewModel.countriesByRegion.observe(viewLifecycleOwner, Observer { groupedCountries ->
            setupCountrySelection(groupedCountries)
        })

        viewModel.indicators.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { indicatorsList ->
                indicators = indicatorsList
                setupIndicatorSelection(indicators)
            }.onFailure { throwable ->
                Log.e("ComparisonFragment", "Error fetching indicators", throwable)
            }
        })

        viewModel.years.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { yearsList ->
                years = yearsList
                setupYearSelection(years)
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

        viewModel.countryDataValues.observe(viewLifecycleOwner, Observer { values ->
            if (values != null && values.isNotEmpty()) {
                Log.d("ComparisonFragment", "Fetched country data values: $values")
                // Update the chart data with the fetched values
                values.forEach { data ->
                    val selectedCountryName = selectedCountryIds.first().toString() // Update this to get the actual country name
                    val selectedIndicatorName = indicators.find { it.id == selectedIndicatorIds.first() }?.name ?: ""
                    val selectedYearValue = data.year

                    chartData.add(Pair("$selectedCountryName - $selectedYearValue", data.value.toFloat()))
                }
                updateChartData()
                Toast.makeText(requireContext(), "Fetched values: $values", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ComparisonFragment", "Failed to fetch country data values")
                Toast.makeText(requireContext(), "Failed to fetch values", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCountrySelection(groupedCountries: Map<String, List<Country>>) {
        val regionList = groupedCountries.keys.toList()
        val countryMap = groupedCountries.mapValues { entry -> entry.value }

        binding.selectCountriesButton.setOnClickListener {
            showRegionSelectDialog("Select Countries", regionList, countryMap)
        }
    }

    private fun setupIndicatorSelection(indicators: List<Indicator>) {
        val indicatorNames = indicators.map { it.name }.toTypedArray()
        val indicatorIds = indicators.map { it.id }.toLongArray()

        binding.selectIndicatorsButton.setOnClickListener {
            showSingleSelectDialog("Select Indicator", indicatorNames, indicatorIds, selectedIndicatorIds)
        }
    }

    private fun setupYearSelection(years: List<Year>) {
        val yearValues = years.map { it.year.toString() }.toTypedArray()
        val yearIds = years.map { it.id }.toLongArray()

        binding.selectYearsButton.setOnClickListener {
            showMultiSelectDialog("Select Years", yearValues, yearIds, selectedYearIds)
        }
    }

    private fun setupButtons() {
        binding.fetchDataButton.setOnClickListener {
            Log.d("ComparisonFragment", "Selected country IDs: $selectedCountryIds")
            Log.d("ComparisonFragment", "Selected indicator IDs: $selectedIndicatorIds")
            Log.d("ComparisonFragment", "Selected year IDs: $selectedYearIds")

            if (selectedCountryIds.isNotEmpty() && selectedIndicatorIds.isNotEmpty() && selectedYearIds.isNotEmpty()) {
                val selectedCountry = selectedCountryIds.first()
                val selectedIndicatorId = selectedIndicatorIds.first()
                val selectedYearIdsList = selectedYearIds.mapNotNull { yearId ->
                    years.find { it.id == yearId }?.year
                }

                val selectedIndicatorName = indicators.find { it.id == selectedIndicatorId }?.name ?: ""

                Log.d("ComparisonFragment", "Selected country ID: $selectedCountry")
                Log.d("ComparisonFragment", "Selected indicator: $selectedIndicatorName")
                Log.d("ComparisonFragment", "Selected years: $selectedYearIdsList")

                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    viewModel.fetchReports(
                        ReportRequest(
                            selectedCountryIds,
                            selectedIndicatorIds,
                            selectedYearIds
                        )
                    )
                } else {
                    viewModel.fetchCountryDataValues(
                        selectedCountry,
                        selectedIndicatorName,
                        selectedYearIdsList
                    )
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

        // Ensure data synchronization
        if (chartData.isEmpty()) return

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

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val textColor = if (isDarkMode) resources.getColor(R.color.white, null) else resources.getColor(R.color.black, null)
        val backgroundColor = if (isDarkMode) resources.getColor(R.color.black, null) else resources.getColor(R.color.white, null)

        val barDataSet = BarDataSet(barEntries, "Inequality Comparison").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextColor = textColor
        }
        val barData = BarData(barDataSet)
        barChart.data = barData
        configureChartAppearance(barChart, xLabels, textColor, backgroundColor)

        val horizontalBarDataSet = BarDataSet(horizontalBarEntries, "Inequality Comparison").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextColor = textColor
        }
        val horizontalBarData = BarData(horizontalBarDataSet)
        horizontalBarChart.data = horizontalBarData
        configureChartAppearance(horizontalBarChart, xLabels, textColor, backgroundColor)

        val lineDataSet = LineDataSet(lineEntries, "Inequality Comparison").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextColor = textColor
        }
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        configureChartAppearance(lineChart, xLabels, textColor, backgroundColor)

        // Force re-layout to ensure the chart updates correctly
        barChart.invalidate()
        horizontalBarChart.invalidate()
        lineChart.invalidate()
        barChart.requestLayout()
        horizontalBarChart.requestLayout()
        lineChart.requestLayout()
        binding.root.requestLayout()  // Force re-layout on the parent container

        // Add a post-delay to ensure the layout is completely updated
        binding.root.postDelayed({
            barChart.invalidate()
            horizontalBarChart.invalidate()
            lineChart.invalidate()
            barChart.requestLayout()
            horizontalBarChart.requestLayout()
            lineChart.requestLayout()
        }, 100)
    }



    private fun configureChartAppearance(chart: BarChart, xLabels: List<String>, textColor: Int, backgroundColor: Int) {
        chart.apply {
            description.isEnabled = false
            setBackgroundColor(backgroundColor)
            setDrawValueAboveBar(true)

            val xAxis = xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.labelRotationAngle = -45f
            xAxis.textColor = textColor

            val yAxisLeft = axisLeft
            yAxisLeft.textColor = textColor

            val yAxisRight = axisRight
            yAxisRight.textColor = textColor

            data?.dataSets?.forEach { set ->
                set.valueTextSize = 10f // Set value text size
                set.setDrawValues(true) // Ensure values are drawn
            }

            invalidate()
            legend.isEnabled = false
        }
    }

    private fun configureChartAppearance(chart: HorizontalBarChart, xLabels: List<String>, textColor: Int, backgroundColor: Int) {
        chart.apply {
            description.isEnabled = false
            setBackgroundColor(backgroundColor)
            setDrawValueAboveBar(true) // Draw values above bars

            val xAxis = xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.labelRotationAngle = -45f
            xAxis.textColor = textColor

            val yAxisLeft = axisLeft
            yAxisLeft.textColor = textColor

            val yAxisRight = axisRight
            yAxisRight.textColor = textColor

            // Ensure data is not null and set value text size and draw values
            data?.dataSets?.forEach { set ->
                set.valueTextSize = 12f // Set value text size
                set.setDrawValues(true) // Ensure values are drawn
            }

            // If data is null, create dummy data to force chart rendering (for debugging purposes)
            if (data == null) {
                val dummyEntries = listOf(BarEntry(0f, 0f))
                val dummyDataSet = BarDataSet(dummyEntries, "Dummy").apply {
                    valueTextSize = 10f
                    setDrawValues(true)
                }
                data = BarData(dummyDataSet)
            }

            invalidate()
            requestLayout()
            legend.isEnabled = false
        }
    }

    private fun configureChartAppearance(chart: LineChart, xLabels: List<String>, textColor: Int, backgroundColor: Int) {
        chart.apply {
            description.isEnabled = false
            setBackgroundColor(backgroundColor)

            val xAxis = xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            xAxis.granularity = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawLabels(true)
            xAxis.labelRotationAngle = -45f
            xAxis.textColor = textColor

            val yAxisLeft = axisLeft
            yAxisLeft.textColor = textColor

            val yAxisRight = axisRight
            yAxisRight.textColor = textColor

            data?.dataSets?.forEach { set ->
                set.valueTextSize = 10f // Set value text size
                set.setDrawValues(true) // Ensure values are drawn
            }

            invalidate()
            legend.isEnabled = false
        }
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
