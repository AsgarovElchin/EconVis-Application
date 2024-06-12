package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asgarov.elchin.econvis.databinding.FragmentThirdOnboardingBinding
import asgarov.elchin.econvis.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdOnboardingFragment : Fragment() {
   private lateinit var binding: FragmentThirdOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentThirdOnboardingBinding.inflate(inflater,container,false)


        binding.ob3Button1.setOnClickListener {
            SharedPreferences.setUserOnboarded(requireContext(), true)
            findNavController().navigate(R.id.action_viewPagerFragment_to_signUpOrLoginFragment)
        }


        return binding.root




    }



}
