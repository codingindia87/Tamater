package com.codingindia.tamater.repository

import androidx.lifecycle.MutableLiveData
import com.codingindia.tamater.models.CartModel
import com.codingindia.tamater.models.CartResponse
import com.codingindia.tamater.models.FoodModel
import com.codingindia.tamater.utils.NetworkResult
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor() {

    private var _addCartLiveData = MutableLiveData<NetworkResult<String>>()
    val addCartLiveData: MutableLiveData<NetworkResult<String>> get() = _addCartLiveData

    private var _cartFoodsLiveData = MutableLiveData<NetworkResult<ArrayList<CartResponse>>>()
    val cartFoodsLiveData: MutableLiveData<NetworkResult<ArrayList<CartResponse>>> get() = _cartFoodsLiveData

    private var _deleteLiveData = MutableLiveData<NetworkResult<String>>()
    val deleteLiveData: MutableLiveData<NetworkResult<String>> get() = _deleteLiveData

    private val db = Firebase.firestore.collection("users")
        .document(Firebase.auth.uid!!)
        .collection("cart")

    suspend fun addToCard(foodId: String) {
        _addCartLiveData.postValue(NetworkResult.Loding())
        try {
            val cartExist = db.whereEqualTo("id", foodId).get().await()
            if (cartExist.isEmpty) {
                db.document()
                    .set(CartModel(foodId))
                    .addOnSuccessListener {
                        _addCartLiveData.postValue(NetworkResult.Success("Food add to cart successfully."))
                    }.addOnFailureListener {
                        _addCartLiveData.postValue(NetworkResult.Error(it.message))
                    }
            } else {
                _addCartLiveData.postValue(NetworkResult.Error("Food is already in cart"))
            }
        } catch (e: Exception) {
            _addCartLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

    suspend fun getCart() {
        try {
            val cartSanp = db.get().await()
            val cartList: ArrayList<CartResponse> = ArrayList()
            for (cardSnap in cartSanp.documents) {
                val cart = cardSnap.toObject(CartModel::class.java)
                val foodSnap =
                    Firebase.firestore.collection("foods").document(cart!!.id).get().await()
                if (foodSnap.exists()) {
                    val food = foodSnap.toObject(FoodModel::class.java)
                    cartList.add(
                        CartResponse(
                            cart = cart,
                            food = food!!,
                            id = cardSnap.id
                        )
                    )
                }
            }
            _cartFoodsLiveData.postValue(NetworkResult.Success(cartList))

        } catch (e: Exception) {
            _cartFoodsLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

    fun updateCart(cartId: String, quantity: Int) {
        try {
            db.document(cartId).update(mapOf<String, Int>("quantity" to quantity))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFood(cartId: String) {
        try {
            db.document(cartId).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteCart() {
        try {
            val cartsSnap = db.get().await()
            for (cartSnp in cartsSnap.documents) {
                val id = db.document(cartSnp.id)
                id.delete().await()
            }
            _deleteLiveData.postValue(NetworkResult.Success("All cart deleted"))
        } catch (e: Exception) {
            e.printStackTrace()
            _deleteLiveData.postValue(NetworkResult.Error("Food order but cart is not deleted."))
        }
    }
}