package com.codingindia.tamater.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.FragmentAddressBinding
import com.codingindia.tamater.models.CartResponse
import com.codingindia.tamater.models.OrderSummary
import com.codingindia.tamater.models.User
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _addressBinding: FragmentAddressBinding? = null
    private val addressBinding get() = _addressBinding!!

    private var name: String = ""
    private var address: String = ""
    private var city: String = ""
    private var pinCode: String = ""

    private val userViewModel by viewModels<UserViewModel> ()

    private var orderSummary: OrderSummary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val josnNote = arguments?.getString("order-summary")
        if(josnNote!!.isNotEmpty()){
            orderSummary = Gson().fromJson(josnNote,OrderSummary::class.java)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _addressBinding = FragmentAddressBinding.inflate(layoutInflater,container,false)
        return _addressBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        addressBinding.btnUserProfile.setOnCheckedChangeListener { compoundButton, b ->
            if(b) getUserData()
            else{
                addressBinding.editName.setText("")
                addressBinding.editAddress.setText("")
                addressBinding.editCity.setText("")
                addressBinding.editPinCode.setText("")
            }
            addressBinding.editName.isEnabled = !b
            addressBinding.editAddress.isEnabled = !b
            addressBinding.editCity.isEnabled = !b
            addressBinding.editPinCode.isEnabled = !b
        }

        addressBinding.btnProcced.setOnClickListener {
            if(checkValidation()){
                val bundle = Bundle()
                bundle.putString("order-summary", Gson().toJson(orderSummary))
                bundle.putString("address",Gson().toJson(
                    User(
                        name,
                        address,
                        city,
                        pinCode
                    )
                ))
                findNavController().navigate(R.id.action_addressFragment_to_orderSummaryFragment,bundle)
            }
        }
    }

    private fun getUserData(){
        userViewModel.getUser()
        userViewModel.userLiveData.observe(viewLifecycleOwner,{
            when(it){
                is NetworkResult.Error -> {
                    Snackbar.make(addressBinding.root,it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    addressBinding.btnProcced.isVisible = false
                    addressBinding.btnUserProfile.isVisible = true
                }
                is NetworkResult.Loding -> {
                    addressBinding.userProgressBar.isVisible = true
                    addressBinding.btnUserProfile.isVisible = false
                }
                is NetworkResult.Success -> {
                    val user: User = it.data!!
                    addressBinding.userProgressBar.isVisible = false
                    addressBinding.btnUserProfile.isVisible = true
                    addressBinding.editName.setText(user.name)
                    addressBinding.editAddress.setText(user.address)
                    addressBinding.editCity.setText(user.city)
                    addressBinding.editPinCode.setText(user.pinCode)
                }
            }
        })
    }

    private fun checkValidation():Boolean{
        name = addressBinding.editName.text.toString().trim()
        address = addressBinding.editAddress.text.toString().trim()
        city = addressBinding.editCity.text.toString().trim()
        pinCode = addressBinding.editPinCode.text.toString().trim(' ')

        if(TextUtils.isEmpty(name)){
            addressBinding.editName.error = "Enter your name"
            return false
        }
        if(TextUtils.isEmpty(address)){
            addressBinding.editAddress.error = "Enter your address"
            return false
        }
        if(TextUtils.isEmpty(city)){
            addressBinding.editName.error = "Enter your city name"
            return false
        }
        if(TextUtils.isEmpty(pinCode)){
            addressBinding.editName.error = "Enter your pincode"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _addressBinding = null
    }

}