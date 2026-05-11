package com.codingindia.tamater.repository

import androidx.lifecycle.MutableLiveData
import com.codingindia.tamater.models.FoodModel
import com.codingindia.tamater.utils.NetworkResult
import com.google.firebase.firestore.Query
import javax.inject.Inject

class FoodsRepository @Inject constructor() {

    private var _foodsLiveData = MutableLiveData<NetworkResult<ArrayList<FoodModel>>>()
    val foodsLiveData: MutableLiveData<NetworkResult<ArrayList<FoodModel>>> get() = _foodsLiveData

    fun getFoods(query: Query){
        _foodsLiveData.postValue(NetworkResult.Loding())
        try {
                query.get().addOnSuccessListener {
                    val foods: ArrayList<FoodModel> = ArrayList()
                    val documents = it.documents
                    for (foodsSnap in documents){
                        val food: FoodModel? = foodsSnap.toObject(FoodModel::class.java)
                        food?.id = foodsSnap.id
                        foods.add(food!!)
                    }
                    _foodsLiveData.postValue(NetworkResult.Success(foods))
                }.addOnFailureListener {
                    _foodsLiveData.postValue(NetworkResult.Error(it.message))
                }
        }catch (e:Exception){
            _foodsLiveData.postValue(NetworkResult.Error("something went wrong"))
        }
    }

}