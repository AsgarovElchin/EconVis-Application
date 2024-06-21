package asgarov.elchin.econvis

import android.annotation.SuppressLint
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
import asgarov.elchin.econvis.databinding.FragmentSavedChartsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SavedChartsFragment : Fragment() {
    private lateinit var binding: FragmentSavedChartsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedChartsBinding.inflate(inflater, container, false)
        return binding.root
    }


}