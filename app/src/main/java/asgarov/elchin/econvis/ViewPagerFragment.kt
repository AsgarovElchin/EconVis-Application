package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asgarov.elchin.econvis.databinding.FragmentViewPagerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerFragment : Fragment() {
   private lateinit var binding:FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPagerBinding.inflate(inflater,container,false)


        val fragmentList = arrayListOf<Fragment>(
            FirstOnboardingFragment(),
            SecondOnboardingFragment(),
            ThirdOnboardingFragment()

        )

        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter



        return binding.root
    }

    fun nextPage() {
        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
    }


}