package com.codingindia.tamater.repository

import androidx.lifecycle.MutableLiveData
import com.codingindia.tamater.models.OrderModel
import com.codingindia.tamater.utils.NetworkResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class OrderRepository @Inject constructor() {

    private var _placeOrderLiveData = MutableLiveData<NetworkResult<String>>()
    val plaseOrderLiveData: MutableLiveData<NetworkResult<String>> get() = _placeOrderLiveData

    val db = Firebase.firestore.collection("order")

    fun placeOrder(orderModel: OrderModel){
        _placeOrderLiveData.postValue(NetworkResult.Loding())
        try {
            db.document().set(orderModel)
                .addOnSuccessListener {
                    _placeOrderLiveData.postValue(NetworkResult.Success("Your order place successfully"))
                }.addOnFailureListener {
                    _placeOrderLiveData.postValue(NetworkResult.Error(it.message))
                }
        }catch (e:Exception){
            _placeOrderLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

}