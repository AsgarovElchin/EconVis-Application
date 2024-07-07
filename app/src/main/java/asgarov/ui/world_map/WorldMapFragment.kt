package asgarov.ui.world_map

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import asgarov.elchin.econvis.data.model.GiniData
import asgarov.elchin.econvis.databinding.FragmentWorldMapBinding
import asgarov.elchin.econvis.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class WorldMapFragment : Fragment() {
    private lateinit var binding: FragmentWorldMapBinding
    private val worldMapViewModel: WorldMapViewModel by viewModels()
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Apply language preference
        applyLanguagePreference(requireContext())

        binding = FragmentWorldMapBinding.inflate(inflater, container, false)

        webView = binding.webview
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true  // Enable DOM storage
        webSettings.useWideViewPort = true  // Enable viewport meta tag
        webSettings.loadWithOverviewMode =
            true  // Zoom out if the content width is greater than the width of the WebView

        // Enable built-in zoom controls
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false  // Hide the zoom controls

        webView.setLayerType(
            View.LAYER_TYPE_HARDWARE,
            null
        )  // Enable hardware acceleration for better performance

        // Set the WebViewClient
        webView.webViewClient = WebViewClient()

        // Enable WebView debugging
        WebView.setWebContentsDebuggingEnabled(true)

        val years = (1970..2020).toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = adapter

        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedYear = parent.getItemAtPosition(position) as Int
                Log.d("WorldMapFragment", "Selected year: $selectedYear")
                worldMapViewModel.fetchGiniData(selectedYear)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Observe LiveData from ViewModel
        worldMapViewModel.giniData.observe(viewLifecycleOwner, Observer { giniDataList ->
            if (giniDataList.isNullOrEmpty()) {
                Log.e("WorldMapFragment", "No data available for selected year")
            } else {
                Log.d("WorldMapFragment", "Data received: $giniDataList")
                updateWebView(giniDataList, webView)
            }
        })

        // Set initial scale to 100%
        webView.setInitialScale(100)

        // Set up zoom in and zoom out buttons
        binding.zoomInButton.setOnClickListener {
            webView.zoomIn()
        }
        binding.zoomOutButton.setOnClickListener {
            webView.zoomOut()
        }

        return binding.root
    }

    private fun updateWebView(giniDataList: List<GiniData>, webView: WebView) {
        // Ensure the data list is not empty and contains valid data
        if (giniDataList.isNullOrEmpty()) {
            Log.e("WorldMapFragment", "Gini data list is empty or null")
            return
        }

        val dataRows = giniDataList.joinToString(",") { "['${it.iso}', ${it.giniIndex}]" }
        val html = """
        <html>
        <head>
          <script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>
          <script type='text/javascript'>
            google.charts.load('current', {
              'packages':['geochart'],
            });
            google.charts.setOnLoadCallback(drawRegionsMap);

            function drawRegionsMap() {
              var data = google.visualization.arrayToDataTable([
                ['Country', 'Gini Index'],
                $dataRows
              ]);

              var options = {
                colorAxis: {colors: ['#add8e6', '#d2691e']}, // Dark purple to darkest purple gradient
                magnifyingGlass: {enable: true, zoomFactor: 7.5}, // Magnify the region around the cursor
                datalessRegionColor: '#f5f5f5',
                defaultColor: '#f5f5f5'
              };

              var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));

              chart.draw(data, options);
            }
          </script>
        </head>
        <body>
          <div id='regions_div' style='width: 100%; height: 100%;'></div>
        </body>
        </html>
        """.trimIndent()

        Log.d("WorldMapFragment", "HTML content: $html")
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private fun applyLanguagePreference(context: Context) {
        val language = PreferenceHelper.getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
