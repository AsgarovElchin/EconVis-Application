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
import androidx.navigation.fragment.navArgs
import asgarov.elchin.econvis.databinding.FragmentNewPasswordBinding
import asgarov.ui.resest_password.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewPasswordFragment : Fragment() {
    private lateinit var binding: FragmentNewPasswordBinding
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()
    private val args: NewPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        val email = args.email
        val code = args.code
        Log.d("NewPasswordFragment", "Email: $email, Code: $code")

        binding.btnLogin12.setOnClickListener {
            val newPassword = binding.oldPasswordLayoutResetPassword.editText?.text.toString()
            val confirmPassword = binding.newPasswordLayoutResetPassword.editText?.text.toString()
            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    Log.d("NewPasswordFragment", "Passwords match, attempting to reset password")
                    resetPasswordViewModel.resetPassword(email, code, newPassword)
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter the new password and confirm it", Toast.LENGTH_SHORT).show()
            }
        }

        resetPasswordViewModel.resetResult.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = {
                    Log.d("NewPasswordFragment", "Password reset successful")
                    Toast.makeText(requireContext(), "Password reset successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_newPasswordFragment_to_loginFragment)
                },
                onFailure = { throwable ->
                    Log.e("NewPasswordFragment", "Password reset failed: ${throwable.message}", throwable)
                    Toast.makeText(requireContext(), throwable.message ?: "Unknown error occurred", Toast.LENGTH_SHORT).show()
                }
            )
        })

        return binding.root
    }
}
