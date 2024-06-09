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
import asgarov.elchin.econvis.databinding.FragmentCodeVerificationBinding
import asgarov.ui.resest_password.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CodeVerificationFragment : Fragment() {
    private lateinit var binding: FragmentCodeVerificationBinding
    private val args: CodeVerificationFragmentArgs by navArgs()
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodeVerificationBinding.inflate(layoutInflater, container, false)


        val email = args.email
        Log.d("CodeVerificationFragment", "Email: $email")

        binding.btnLogin.setOnClickListener {
            val code = binding.verificationCode.editText?.text.toString()
            if (code.isNotEmpty()) {
                resetPasswordViewModel.verifyCode(email, code)
            } else {
                Toast.makeText(requireContext(), "Please enter the verification code", Toast.LENGTH_SHORT).show()
            }
        }

        resetPasswordViewModel.verificationResult.observe(viewLifecycleOwner, Observer { result ->
            result.fold(
                onSuccess = {
                    Log.d("CodeVerificationFragment", "Code verification successful")
                    Toast.makeText(requireContext(), "Code verified", Toast.LENGTH_SHORT).show()
                    val code = binding.verificationCode.editText?.text.toString()
                    val action = CodeVerificationFragmentDirections.actionCodeVerificationFragmentToNewPasswordFragment(email, code)
                    findNavController().navigate(action)
                },
                onFailure = { throwable ->
                    Log.e("CodeVerificationFragment", "Code verification failed: ${throwable.message}", throwable)
                    Toast.makeText(requireContext(), throwable.message ?: "Unknown error occurred", Toast.LENGTH_SHORT).show()
                }
            )
        })


        return binding.root
    }


}
