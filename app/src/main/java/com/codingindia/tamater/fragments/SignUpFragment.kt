package com.codingindia.tamater.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.FragmentSignUpBinding
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {


    private var _signUpFragmentBinding: FragmentSignUpBinding? = null
    private val signUpFragmentBinding: FragmentSignUpBinding get() = _signUpFragmentBinding!!

    private var name = ""
    private var email = ""
    private var password = ""

    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _signUpFragmentBinding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return _signUpFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUpFragmentBinding.btnSignup.setOnClickListener {
            if (validateUser()) {
                createUser()
            }
        }

        signUpFragmentBinding.tvSignin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun createUser() {
        userViewModel.sign(name, email, password)
        userViewModel.signupLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResult.Error -> {
                    showProgress(false)
                    Snackbar.make(
                        signUpFragmentBinding.root,
                        it.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loding -> {
                    showProgress(true)
                }

                is NetworkResult.Success -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
                }
            }
        })
    }

    private fun validateUser(): Boolean {
        name = signUpFragmentBinding.txtFullName.text.toString().trim()
        email = signUpFragmentBinding.txtEmail.text.toString().trim(' ')
        password = signUpFragmentBinding.txtPassword.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            signUpFragmentBinding.txtFullName.error = "Enter your name"
            return false
        }
        if (TextUtils.isEmpty(email)) {
            signUpFragmentBinding.txtEmail.error = "Enter your email address"
            return false
        }
        if (TextUtils.isEmpty(password)) {
            signUpFragmentBinding.txtPassword.error = "Create a password"
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _signUpFragmentBinding = null
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            signUpFragmentBinding.btnSignup.visibility = View.GONE
            signUpFragmentBinding.progressbar.visibility = View.VISIBLE
        } else {
            signUpFragmentBinding.btnSignup.visibility = View.VISIBLE
            signUpFragmentBinding.progressbar.visibility = View.GONE
        }
    }

}