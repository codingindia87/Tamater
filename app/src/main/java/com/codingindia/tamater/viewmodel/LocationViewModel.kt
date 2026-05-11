package com.codingindia.tamater.viewmodel

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codingindia.tamater.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: LocationRepository):ViewModel() {

    val location: LiveData<Address?> = locationRepository.geLastKnownLocation()

}