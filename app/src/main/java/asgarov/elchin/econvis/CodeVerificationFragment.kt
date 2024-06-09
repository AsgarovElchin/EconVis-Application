package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import asgarov.elchin.econvis.databinding.FragmentCodeVerificationBinding


class CodeVerificationFragment : Fragment() {
    private lateinit var binding: FragmentCodeVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodeVerificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}
