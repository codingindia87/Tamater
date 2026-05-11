package com.codingindia.tamater.viewmodel

import androidx.lifecycle.ViewModel
import com.codingindia.tamater.models.OrderModel
import com.codingindia.tamater.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepository: OrderRepository):ViewModel() {

    val placeOrderLiveData get() = orderRepository.plaseOrderLiveData

    fun placeOrder(orderModel: OrderModel){
        orderRepository.placeOrder(orderModel)
    }
}