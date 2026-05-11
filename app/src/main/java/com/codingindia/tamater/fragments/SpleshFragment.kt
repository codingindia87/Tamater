package com.codingindia.tamater.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.FragmentSpleshBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SpleshFragment : Fragment() {

    private var _binding: FragmentSpleshBinding? = null
    private val binding: FragmentSpleshBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpleshBinding.inflate(layoutInflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler()

        handler.postDelayed({
            if (Firebase.auth.currentUser == null)
                findNavController().navigate(R.id.action_spleshFragment_to_loginFragment)
            else
                findNavController().navigate(R.id.action_spleshFragment_to_mainFragment)
        }, 1500)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}