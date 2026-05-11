package com.codingindia.tamater.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingindia.tamater.R
import com.codingindia.tamater.adapters.FoodAdapter
import com.codingindia.tamater.databinding.FragmentMainBinding
import com.codingindia.tamater.models.FoodModel
import com.codingindia.tamater.models.User
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.FoodsViewmodel
import com.codingindia.tamater.viewmodel.LocationViewModel
import com.codingindia.tamater.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), FoodAdapter.FoodAdapterClick {

    private var _bindingMain: FragmentMainBinding? = null
    private val bindingHome: FragmentMainBinding get() = _bindingMain!!

    private val locationViewModel by viewModels<LocationViewModel>()

    private val foodsViewmodel by viewModels<FoodsViewmodel>()

    private val userViewModel by viewModels<UserViewModel>()

    private lateinit var adapter: FoodAdapter

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(requireContext(),"Please provide location permission",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = FoodAdapter(requireContext(),this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindingMain = FragmentMainBinding.inflate(layoutInflater, container, false)
        return _bindingMain?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            getLocation()
        }

        getFoods()

        bindingHome.mainRecyclerview.layoutManager = GridLayoutManager(requireContext(),2)
        bindingHome.mainRecyclerview.adapter = adapter


        bindingHome.searchEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val search = p0?:""
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        bindingHome.imgCart.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_cartCheckoutFragment)
        }

        bindingHome.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLocation(){
        locationViewModel.location.observe(viewLifecycleOwner, { address ->
            address?.let {
                bindingHome.tvLocation.text = "${it.subLocality ?: ""}, ${it.locality}"
                userViewModel.setUserProfile(
                    User(
                        Firebase.auth.currentUser?.displayName!!,
                        it.subLocality,
                        it.locality ?: "",
                        it.postalCode
                    )
                )
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Location is null probably denied permission or GPS is sleeping.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getFoods(){
        var query: Query? = Firebase.firestore.collection("foods").orderBy("name")
        foodsViewmodel.getAllFoods(query!!)
        foodsViewmodel.foods.observe(viewLifecycleOwner,{
            when(it){
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    bindingHome.progressBar.visibility = View.GONE
                }
                is NetworkResult.Loding -> {
                }
                is NetworkResult.Success -> {
                    bindingHome.progressBar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingMain = null
    }

    override fun onParentClick(food:FoodModel) {
        val bundle = Bundle()
        bundle.putString("food", Gson().toJson(food))
        findNavController().navigate(R.id.action_mainFragment_to_foodDetailFragment,bundle)
    }
}