package asgarov.ui.comparison


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import asgarov.elchin.econvis.R
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.databinding.FragmentComparisonBinding
import asgarov.elchin.econvis.utils.RegionExpandableListAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComparisonFragment : Fragment() {
    private lateinit var binding: FragmentComparisonBinding
    private val viewModel: ComparisonViewModel by viewModels()

    private val selectedCountryIds = mutableListOf<Long>()
    private val selectedIndicatorIds = mutableListOf<Long>()
    private val selectedYearIds = mutableListOf<Long>()

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
            result.onSuccess { indicators ->
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
            result.onSuccess { years ->
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
    }

    private fun setupButtons() {
        binding.fetchDataButton.setOnClickListener {
            Log.d("ComparisonFragment", "Selected country IDs: $selectedCountryIds")
            Log.d("ComparisonFragment", "Selected indicator IDs: $selectedIndicatorIds")
            Log.d("ComparisonFragment", "Selected year IDs: $selectedYearIds")
            if (selectedIndicatorIds.isNotEmpty()) {
                viewModel.fetchReports(ReportRequest(selectedCountryIds, selectedIndicatorIds, selectedYearIds))
            } else {
                Log.e("ComparisonFragment", "Please select at least one indicator.")
            }
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
        val barChart: BarChart = binding.barChart
        val horizontalBarChart: HorizontalBarChart = binding.horizontalBarChart
        val lineChart: LineChart = binding.lineChart

        // Clear previous data
        barChart.clear()
        horizontalBarChart.clear()
        lineChart.clear()

        // Group the data by indicator, year, and country
        val groupedData = reports.groupBy { it.indicator.name }
            .flatMap { (indicator, indicatorReports) ->
                indicatorReports.groupBy { it.year.year }
                    .flatMap { (year, yearReports) ->
                        yearReports.map { report ->
                            Triple(indicator, year, report)
                        }
                    }
            }

        val barEntries = ArrayList<BarEntry>()
        val horizontalBarEntries = ArrayList<BarEntry>()
        val lineEntries = ArrayList<Entry>()
        val xLabels = ArrayList<String>()
        groupedData.forEachIndexed { index, (indicator, year, report) ->
            barEntries.add(BarEntry(index.toFloat(), report.data.toFloat()))
            horizontalBarEntries.add(BarEntry(index.toFloat(), report.data.toFloat()))
            lineEntries.add(Entry(index.toFloat(), report.data.toFloat()))
            xLabels.add("${report.country.name} - $year")
        }

        // Set up BarChart
        val barDataSet = BarDataSet(barEntries, "Inequality Comparison")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        barDataSet.valueTextSize = 12f
        val barData = BarData(barDataSet)
        barChart.data = barData

        // Customize BarChart appearance
        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(60)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)

        val barXAxis = barChart.xAxis
        barXAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        barXAxis.granularity = 1f
        barXAxis.position = XAxis.XAxisPosition.BOTTOM
        barXAxis.setDrawLabels(true)
        barXAxis.labelRotationAngle = -45f

        // Set up HorizontalBarChart
        val horizontalBarDataSet = BarDataSet(horizontalBarEntries, "Inequality Comparison")
        horizontalBarDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        horizontalBarDataSet.valueTextSize = 12f
        val horizontalBarData = BarData(horizontalBarDataSet)
        horizontalBarChart.data = horizontalBarData

        // Customize HorizontalBarChart appearance
        horizontalBarChart.description.isEnabled = false
        horizontalBarChart.setDrawValueAboveBar(true)
        horizontalBarChart.setMaxVisibleValueCount(60)
        horizontalBarChart.setPinchZoom(false)
        horizontalBarChart.setDrawGridBackground(false)

        val horizontalBarXAxis = horizontalBarChart.xAxis
        horizontalBarXAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        horizontalBarXAxis.granularity = 1f
        horizontalBarXAxis.position = XAxis.XAxisPosition.BOTTOM
        horizontalBarXAxis.setDrawLabels(true)
        horizontalBarXAxis.labelRotationAngle = -45f

        // Set up LineChart
        val lineDataSet = LineDataSet(lineEntries, "Inequality Comparison")
        lineDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        lineDataSet.valueTextSize = 12f
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize LineChart appearance
        lineChart.description.isEnabled = false
        lineChart.setDrawGridBackground(false)

        val lineXAxis = lineChart.xAxis
        lineXAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        lineXAxis.granularity = 1f
        lineXAxis.position = XAxis.XAxisPosition.BOTTOM
        lineXAxis.setDrawLabels(true)
        lineXAxis.labelRotationAngle = -45f

        // Refresh the charts
        barChart.invalidate()
        horizontalBarChart.invalidate()
        lineChart.invalidate()

        barChart.legend.isEnabled = false
        horizontalBarChart.legend.isEnabled = false
        lineChart.legend.isEnabled = false
    }
}