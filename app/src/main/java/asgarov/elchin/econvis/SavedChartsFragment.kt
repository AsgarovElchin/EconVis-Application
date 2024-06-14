package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asgarov.elchin.econvis.databinding.FragmentSavedChartsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedChartsFragment : Fragment() {
    private lateinit var binding: FragmentSavedChartsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedChartsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}
