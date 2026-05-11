package com.codingindia.tamater.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.FragmentProfileBinding
import com.codingindia.tamater.viewmodel.LocationViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _profileBinding: FragmentProfileBinding? = null
    private val profileBinding get() = _profileBinding!!

    private val locationViewModel by viewModels<LocationViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _profileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProfile()

        profileBinding.myOrder.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myOrderFragment)
        }

        profileBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        profileBinding.logoutLayout.setOnClickListener { createLogoutDialog() }

    }

    private fun createLogoutDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout?")
        builder.setMessage("Do you want to logout")
        builder.setPositiveButton("Logout"){
            _,_ ->
            Firebase.auth.signOut()
            findNavController().navigate(
                R.id.loginFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.mainFragment, true)
                    .build()
            )
        }
        builder.setNeutralButton("No"){
            dialog,_ ->
            dialog.cancel()
        }
        builder.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setProfile() {
        val profileImage = Firebase.auth.currentUser?.photoUrl
        val name = Firebase.auth.currentUser?.displayName

        Glide.with(requireContext())
            .load(profileImage)
            .placeholder(R.drawable.user_avtar)
            .into(profileBinding.userImage)

        profileBinding.userName.text = name

        locationViewModel.location.observe(viewLifecycleOwner, { address ->
            address?.let {
                if (it.subLocality != null) {
                    profileBinding.textLocation.text = "${it.subLocality}, ${it.locality}"
                } else
                    profileBinding.textLocation.text = it.locality
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Location is null probably denied permission or GPS is sleeping.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _profileBinding = null
    }

}