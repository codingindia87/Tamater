package com.codingindia.tamater.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingindia.tamater.R
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.LocationViewModel
import com.codingindia.tamater.viewmodel.UserViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private val locationViewModel by viewModels<LocationViewModel>()

    private val userViewModel by viewModels<UserViewModel>()

    private var orderHistoryId: String = ""

    private val callback = OnMapReadyCallback { googleMap ->
        setData(googleMap)

        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun setData(googleMap: GoogleMap){

        locationViewModel.location.observe(viewLifecycleOwner, { address ->

            val startPosition = LatLng(address!!.latitude, address.longitude)

            userViewModel.orderLocationLiveData.observe(viewLifecycleOwner,{
                when(it){
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loding -> { }
                    is NetworkResult.Success -> {

                        googleMap.clear()
                        
                        val location = it.data

                        val endPosition = LatLng(location!!.latitude, location.longitude)

                        val maker = MarkerOptions().position(endPosition).title("Food").icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle))

                        googleMap.addMarker(
                            MarkerOptions().position(startPosition).title("ME")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_marker))
                        )
                        googleMap.addMarker(maker)

                        val polylineOptions = PolylineOptions()
                            .add(startPosition)
                            .add(endPosition)
                            .color(Color.BLACK)
                            .width(20f)
                            .geodesic(true)
                        googleMap.addPolyline(polylineOptions)

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(endPosition, 60f))

                    }
                }
            })

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderHistoryId = arguments?.getString("order-history-id")!!
        userViewModel.getOrderLocation(orderHistoryId)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val backButton: ImageView = view.findViewById(R.id.img_back)
        mapFragment?.getMapAsync(callback)

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}