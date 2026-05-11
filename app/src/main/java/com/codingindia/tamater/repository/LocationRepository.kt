package com.codingindia.tamater.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class LocationRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun geLastKnownLocation(): LiveData<Address?>{
        val locationData = MutableLiveData<Address?>()
        if(ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            locationData.postValue(null)
            return locationData
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        locationData.postValue(address)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }.addOnFailureListener {
                locationData.postValue(null)
            }

        return locationData
    }
}