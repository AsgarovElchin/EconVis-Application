package asgarov.elchin.econvis

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
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
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.Theme_EconVis)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        binding = FragmentSignUpOrLoginBinding.inflate(localInflater, container, false)

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpOrLoginFragment_to_loginFragment)
        }
        binding.createAnAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpOrLoginFragment_to_signUpFragment)
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Force light theme when this fragment is visible
        activity?.setTheme(R.style.Theme_EconVis)
    }


}