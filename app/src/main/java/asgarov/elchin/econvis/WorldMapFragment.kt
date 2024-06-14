package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import asgarov.elchin.econvis.databinding.FragmentWorldMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorldMapFragment : Fragment() {
    private lateinit var binding: FragmentWorldMapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorldMapBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


}