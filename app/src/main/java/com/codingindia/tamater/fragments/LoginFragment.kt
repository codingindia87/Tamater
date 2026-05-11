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
import com.codingindia.tamater.databinding.FragmentLoginBinding
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _loginBinding: FragmentLoginBinding? = null
    private val loadingBinding: FragmentLoginBinding get() = _loginBinding!!

    private val userViewModel by viewModels<UserViewModel>()

    private var email = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _loginBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return _loginBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBinding.btnLogin.setOnClickListener {
            if (validateUser()) login()
        }

        loadingBinding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun validateUser(): Boolean {
        email = loadingBinding.txtEmail.text.toString().trim(' ')
        password = loadingBinding.txtPassword.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            loadingBinding.txtEmail.error = "Enter your email address"
            return false
        }
        if (TextUtils.isEmpty(password)) {
            loadingBinding.txtPassword.error = "Enter your password"
            return false
        }
        return true
    }

    private fun login() {
        userViewModel.login(email, password)
        userViewModel.loginLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResult.Error -> {
                    showProgress(false)
                    Snackbar.make(loadingBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }

                is NetworkResult.Loding -> {
                    showProgress(true)
                }

                is NetworkResult.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
            }
        })
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            loadingBinding.btnLogin.visibility = View.GONE
            loadingBinding.progressbar.visibility = View.VISIBLE
        } else {
            loadingBinding.btnLogin.visibility = View.VISIBLE
            loadingBinding.progressbar.visibility = View.GONE
        }
    }

}