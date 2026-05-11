package com.codingindia.tamater.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingindia.tamater.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val cartRepository: CartRepository):ViewModel() {

    val addCartLiveData get() = cartRepository.addCartLiveData

    val cartFoodsLiveData get() = cartRepository.cartFoodsLiveData

    val deleteLiveData get() = cartRepository.deleteLiveData

    fun addCard(foodId:String){
        viewModelScope.launch {
            cartRepository.addToCard(foodId)
        }
    }

    fun getCart(){
        viewModelScope.launch {
            cartRepository.getCart()
        }
    }

    fun updateCart(cartId: String, quantity: Int){
        cartRepository.updateCart(cartId,quantity)
    }

    fun deleteFood(cardId:String){
        cartRepository.deleteFood(cardId)
    }

    fun deleteCart(){
        viewModelScope.launch {
            cartRepository.deleteCart()
        }
    }
}