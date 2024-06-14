package asgarov.elchin.econvis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import asgarov.elchin.econvis.databinding.FragmentLoginBinding
import asgarov.elchin.econvis.utils.SharedPreferences
import asgarov.ui.login.LogInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LogInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(LogInViewModel::class.java)
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.emailLayout.editText?.text.toString()
            val password = binding.passwordLayout.editText?.text.toString()

            var hasError = false


            if (email.isEmpty()) {
                binding.emailLayout.error = "Email cannot be empty"
                hasError = true
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = "Please enter a valid email address"
                hasError = true
            } else {
                binding.emailLayout.error = null
            }
            if (password.isEmpty()) {
                binding.passwordLayout.error = "Password cannot be empty"
                hasError = true
            } else if (password.length < 6) {
                binding.passwordLayout.error = "Password must be at least 6 characters"
                hasError = true
            } else {
                binding.passwordLayout.error = null
            }
            if (!hasError) {

                viewModel.signIn(email, password)
            }

            viewModel.signInResult.observe(viewLifecycleOwner, Observer { result ->
                result.onSuccess { message ->
                    findNavController().navigate(R.id.action_loginFragment_to_menuContainerActivity)
                }.onFailure { exception ->
                    Toast.makeText(
                        context,
                        "Sign-in failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

            SharedPreferences.setUserLoggedIn(requireContext(), true)

        }

        return binding.root
    }


}