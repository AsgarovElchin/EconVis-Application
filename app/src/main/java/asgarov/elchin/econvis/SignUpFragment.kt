package asgarov.elchin.econvis

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.databinding.FragmentSignUpBinding
import asgarov.ui.signup.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.Base_Theme_EconVis)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        binding = FragmentSignUpBinding.inflate(localInflater, container, false)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.btnSignUp.setOnClickListener {
            val username = binding.usernameSignUp.editText?.text.toString()
            val email = binding.emailSignUp.editText?.text.toString()
            val password = binding.passwordSignUp.editText?.text.toString()

            var hasError = false

            if (username.isEmpty()) {
                binding.usernameSignUp.error = "Username cannot be empty"
                hasError = true
            } else {
                binding.usernameSignUp.error = null
            }
            if (email.isEmpty()) {
                binding.emailSignUp.error = "Email cannot be empty"
                hasError = true
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailSignUp.error = "Please enter a valid email address"
                hasError = true
            } else {
                binding.emailSignUp.error = null
            }
            if (password.isEmpty()) {
                binding.passwordSignUp.error = "Password cannot be empty"
                hasError = true
            } else if (password.length < 6) {
                binding.passwordSignUp.error = "Password must be at least 6 characters"
                hasError = true
            } else {
                binding.passwordSignUp.error = null
            }
            if (!hasError) {
                val user = User(username, email, password)
                viewModel.signUp(user)
            }
        }





        viewModel.signupResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess {
                findNavController().navigate(R.id.action_signUpFragment_to_menuContainerActivity)

            }.onFailure {
                Toast.makeText(context, "Sign up failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        })
        return binding.root

    }


}
