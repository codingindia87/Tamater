package com.codingindia.tamater.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.codingindia.tamater.models.FoodModel
import com.codingindia.tamater.models.OrderModel
import com.codingindia.tamater.models.User
import com.codingindia.tamater.utils.NetworkResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor() {

    private var _loginLiveData = MutableLiveData<NetworkResult<AuthResult>>()
    val loginLiveData:  MutableLiveData<NetworkResult<AuthResult>> get() = _loginLiveData

    private var _signupLiveData = MutableLiveData<NetworkResult<AuthResult>>()
    val signupLiveData: MutableLiveData<NetworkResult<AuthResult>> get() = _signupLiveData

    private var _userLiveData = MutableLiveData<NetworkResult<User>>()
    val userLiveData: MutableLiveData<NetworkResult<User>> get() = _userLiveData

    private var _orderHistoryLiveData = MutableLiveData<NetworkResult<ArrayList<OrderModel>>>()
    val orderHistoryLiveData: MutableLiveData<NetworkResult<ArrayList<OrderModel>>> get() = _orderHistoryLiveData

    private var _orderLocationLiveData = MutableLiveData<NetworkResult<GeoPoint>>()
    val orderLocationLiveData: MutableLiveData<NetworkResult<GeoPoint>> get() = _orderLocationLiveData

    fun loginUser(email:String,password:String){
       try {
           _loginLiveData.postValue(NetworkResult.Loding())
           Firebase.auth.signInWithEmailAndPassword(email,password)
               .addOnSuccessListener {
                   _loginLiveData.postValue(NetworkResult.Success(it))
               }.addOnFailureListener {
                   _loginLiveData.postValue(NetworkResult.Error(it.message))
               }
       }catch (e:Exception){
           _loginLiveData.postValue(NetworkResult.Error("something went wrong"))
       }
    }

    fun signUp(name:String,email:String,password:String){
       try {
           _signupLiveData.postValue(NetworkResult.Loding())
           Firebase.auth.createUserWithEmailAndPassword(email,password)
               .addOnSuccessListener { authResult ->
                   authResult.user?.updateProfile(userProfileChangeRequest {
                       displayName = name
                   })?.addOnSuccessListener {
                       _signupLiveData.postValue(NetworkResult.Success(authResult))
                   }
               }.addOnFailureListener {
                   _signupLiveData.postValue(NetworkResult.Error(it.message))
               }
       }catch (e:Exception){
           _signupLiveData.postValue(NetworkResult.Error("something went wrong"))
       }
    }

    fun setUserAddress(user: User){
        try {
            Firebase.firestore.collection("users").document(Firebase.auth.uid!!)
                .set(user)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    suspend fun getUser(){
        _userLiveData.postValue(NetworkResult.Loding())
        try {
            val userSnap = Firebase.firestore.collection("users").document(Firebase.auth.uid!!).get().await()
            val user: User? = userSnap.toObject(User::class.java)
            if(user != null){
                _userLiveData.postValue(NetworkResult.Success(user))
            }else
                _userLiveData.postValue(NetworkResult.Error("User not found."))
        }catch (e:Exception){
            _userLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

    fun getOrderHistory(){
        _orderHistoryLiveData.postValue(NetworkResult.Loding())
        try {
            val db = Firebase.firestore.collection("order").whereEqualTo("orderBy",Firebase.auth.uid)
            db.get().addOnSuccessListener {
                if(!it.isEmpty){
                    val orderList: ArrayList<OrderModel> = ArrayList()
                    val documents = it.documents
                    for (orderSnp in documents){
                        val order: OrderModel? = orderSnp.toObject(OrderModel::class.java)
                        order?.id = orderSnp.id
                        orderList.add(order!!)
                    }
                    _orderHistoryLiveData.postValue(NetworkResult.Success(orderList))
                }else{
                    _orderHistoryLiveData.postValue(NetworkResult.Error("Order History not found"))
                }
            }.addOnFailureListener {
                _orderHistoryLiveData.postValue(NetworkResult.Error(it.message))
            }
        }catch (e:Exception){
            _orderHistoryLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

    fun getOrderLocation(orderHistoryId: String){
        _orderLocationLiveData.postValue(NetworkResult.Loding())
        Firebase.firestore.collection("order")
            .document(orderHistoryId)
            .addSnapshotListener { value, error ->
                if(error != null){
                    _orderLocationLiveData.postValue(NetworkResult.Error(error.message))
                }else{
                    val location: GeoPoint = value?.getGeoPoint("location")!!
                    _orderLocationLiveData.postValue(NetworkResult.Success(location))
                }
            }
    }

}