package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asgarov.elchin.econvis.databinding.FragmentSignUpOrLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpOrLoginFragment : Fragment() {
    private lateinit var binding: FragmentSignUpOrLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpOrLoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpOrLoginFragment_to_loginFragment)
        }
        binding.createAnAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpOrLoginFragment_to_signUpFragment)
        }




        return binding.root
    }
}