package asgarov.elchin.econvis

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import asgarov.elchin.econvis.databinding.FragmentComparisonBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ComparisonFragment : Fragment() {
    private lateinit var binding: FragmentComparisonBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComparisonBinding.inflate(inflater, container, false)
        return binding.root
    }

}