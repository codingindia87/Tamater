package com.codingindia.tamater.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingindia.tamater.models.User
import com.codingindia.tamater.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel(){

    val loginLiveData get() = userRepository.loginLiveData

    val signupLiveData get() = userRepository.signupLiveData

    val userLiveData get() = userRepository.userLiveData

    val orderHistoryLiveData get() = userRepository.orderHistoryLiveData

    val orderLocationLiveData get() = userRepository.orderLocationLiveData

    fun login(email:String,password:String){
        userRepository.loginUser(email,password)
    }

    fun sign(name:String,email:String,password:String){
        userRepository.signUp(name,email,password)
    }

    fun setUserProfile(user: User){
        userRepository.setUserAddress(user)
    }

    fun getUser(){
        viewModelScope.launch {
            userRepository.getUser()
        }
    }

    fun getOrderHistory(){
        userRepository.getOrderHistory()
    }

    fun getOrderLocation(orderHistoryId: String){
        userRepository.getOrderLocation(orderHistoryId)
    }
}