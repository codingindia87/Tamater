package com.codingindia.tamater.viewmodel

import androidx.lifecycle.ViewModel
import com.codingindia.tamater.repository.FoodsRepository
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodsViewmodel @Inject constructor(private val foodsRepository: FoodsRepository):ViewModel() {

    val foods get() = foodsRepository.foodsLiveData

    fun getAllFoods(query: Query){
        foodsRepository.getFoods(query)
    }
}