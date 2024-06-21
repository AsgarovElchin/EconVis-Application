package asgarov.elchin.econvis

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.LinearLayoutManager
import asgarov.elchin.econvis.data.model.SavedChart
import asgarov.elchin.econvis.databinding.FragmentSavedChartsBinding
import asgarov.elchin.econvis.utils.SavedChartsAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class SavedChartsFragment : Fragment() {
    private lateinit var binding: FragmentSavedChartsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedChartsBinding.inflate(inflater, container, false)
        loadSavedCharts()

        binding.clearSavedChartsButton.setOnClickListener {
            clearAllSavedCharts()
        }

        return binding.root
    }

    private fun loadSavedCharts() {
        val sharedPreferences = requireContext().getSharedPreferences("saved_charts", Context.MODE_PRIVATE)
        val filePaths = sharedPreferences.getStringSet("file_paths", mutableSetOf())?.toList() ?: listOf()

        val savedCharts = filePaths.map { SavedChart(it) }

        binding.savedChartsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.savedChartsRecyclerView.adapter = SavedChartsAdapter(savedCharts) { savedChart ->
            deleteSavedChart(savedChart)
        }
    }

    private fun clearAllSavedCharts() {
        val sharedPreferences = requireContext().getSharedPreferences("saved_charts", Context.MODE_PRIVATE)
        val filePaths = sharedPreferences.getStringSet("file_paths", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        filePaths.forEach { filePath ->
            File(filePath).delete()
        }

        sharedPreferences.edit().clear().apply()
        loadSavedCharts()
    }

    private fun deleteSavedChart(savedChart: SavedChart) {
        val sharedPreferences = requireContext().getSharedPreferences("saved_charts", Context.MODE_PRIVATE)
        val filePaths = sharedPreferences.getStringSet("file_paths", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        File(savedChart.filePath).delete()
        filePaths.remove(savedChart.filePath)

        sharedPreferences.edit().putStringSet("file_paths", filePaths).apply()
        loadSavedCharts()
    }
}