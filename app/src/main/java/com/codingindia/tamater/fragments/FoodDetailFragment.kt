package com.codingindia.tamater.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codingindia.tamater.R
import com.codingindia.tamater.adapters.FoodAdapter
import com.codingindia.tamater.databinding.FragmentFoodDetailBinding
import com.codingindia.tamater.models.FoodModel
import com.codingindia.tamater.utils.Converters
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.CartViewModel
import com.codingindia.tamater.viewmodel.FoodsViewmodel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodDetailFragment : Fragment(),FoodAdapter.FoodAdapterClick {

    private var _fdBinding: FragmentFoodDetailBinding? = null
    private val fdBinding: FragmentFoodDetailBinding get() = _fdBinding!!

    private var food: FoodModel? = null

    private var adapter: FoodAdapter? = null

    private val foodsViewmodel by viewModels<FoodsViewmodel>()

    private val cartViewmodel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = FoodAdapter(requireContext(),this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fdBinding = FragmentFoodDetailBinding.inflate(layoutInflater,container,false)
        return _fdBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialData()

        fdBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        fdBinding.btnAddCart.setOnClickListener {
            cartViewmodel.addCard(food!!.id)
            cartViewmodel.addCartLiveData.observe(viewLifecycleOwner,{
                when(it){
                    is NetworkResult.Error -> {
                        Snackbar.make(fdBinding.root,it.message.toString(),Snackbar.LENGTH_SHORT).show()
                        fdBinding.btnLayout.isVisible = true
                        fdBinding.cartProgress.isVisible = false
                    }
                    is NetworkResult.Loding -> {
                        fdBinding.btnLayout.isVisible = false
                        fdBinding.cartProgress.isVisible = true
                    }
                    is NetworkResult.Success -> {
                        fdBinding.btnLayout.isVisible = true
                        fdBinding.cartProgress.isVisible = false
                        Snackbar.make(fdBinding.root,it.data.toString(),Snackbar.LENGTH_SHORT).show()
                        fdBinding.btnAddCart.isEnabled = false
                    }
                }
            })
        }

        fdBinding.suggestedRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        fdBinding.suggestedRv.adapter = adapter

        getSFood()

        fdBinding.imgCart.setOnClickListener {
            findNavController().navigate(R.id.action_foodDetailFragment_to_cartCheckoutFragment)
        }
    }

    private fun setInitialData() {
        val josnNote = arguments?.getString("food")
        if (josnNote != null){
            food = Gson().fromJson(josnNote,FoodModel::class.java)
            food?.let {
                Glide.with(requireContext())
                    .load(it.image)
                    .placeholder(R.drawable.food_image)
                    .into(fdBinding.foodImage)

                fdBinding.tvName.text = it.name
                fdBinding.tvPrice.text = Converters.priceFormat(it.price)

                fdBinding.foodDescription.text = it.description

                fdBinding.totalPrice.text = "Total: ${Converters.priceFormat(it.price)}"

                if(it.type == "veg")
                    fdBinding.addType.setImageDrawable(getDrawable(requireContext(),R.drawable.square_dot_green_24px))
                else
                    fdBinding.addType.setImageDrawable(getDrawable(requireContext(),R.drawable.square_dot_red_24px))

                var query: Query? = Firebase.firestore.collection("foods").whereEqualTo("type",food?.type)
                foodsViewmodel.getAllFoods(query!!)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fdBinding = null
    }

    private fun getSFood(){

        foodsViewmodel.foods.observe(viewLifecycleOwner,{
            when(it){
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loding -> {

                }
                is NetworkResult.Success -> {
                    adapter?.submitList(it.data)
                    fdBinding.suggestedRv.smoothScrollToPosition(it.data?.size!! - 1)
                }
            }
        })
    }

    override fun onParentClick(food: FoodModel) {

    }


}