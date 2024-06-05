package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import asgarov.elchin.econvis.databinding.FragmentSecondOnboardingBinding

class SecondOnboardingFragment : Fragment() {
   private lateinit var binding: FragmentSecondOnboardingBinding

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondOnboardingBinding.inflate(inflater,container,false)


        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)

        binding.ob2Button1.setOnClickListener {
            viewPager?.currentItem = 2
        }



        return binding.root

    }

}