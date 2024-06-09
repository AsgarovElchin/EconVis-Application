package asgarov.elchin.econvis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import asgarov.elchin.econvis.databinding.FragmentResetPasswordBinding
import asgarov.ui.resest_password.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentResetPasswordBinding

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)

        binding.btnLogin.setOnClickListener {
            val email = binding.emailLayoutResetPassword.editText?.text.toString()
            if (email.isNotEmpty()) {
                Log.d("ResetPasswordFragment", "Requesting password reset for email: $email")
                resetPasswordViewModel.requestPasswordReset(email)
            } else {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        resetPasswordViewModel.passwordResetResult.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = {
                    Log.d("ResetPasswordFragment", "Password reset successful")
                    Toast.makeText(requireContext(), "Verification code sent to your email", Toast.LENGTH_SHORT).show()
                    val email = binding.emailLayoutResetPassword.editText?.text.toString()
                    val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToCodeVerificationFragment(email)
                    findNavController().navigate(action)
                },
                onFailure = { throwable ->
                    Log.e("ResetPasswordFragment", "Password reset failed: ${throwable.message}", throwable)
                    Toast.makeText(requireContext(), throwable.message ?: "Unknown error occurred", Toast.LENGTH_SHORT).show()
                }
            )
        })

        return binding.root
    }
}