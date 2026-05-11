package com.codingindia.tamater.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingindia.tamater.R
import com.codingindia.tamater.adapters.CartAdapter
import com.codingindia.tamater.databinding.FragmentCartCheckoutBinding
import com.codingindia.tamater.models.CartResponse
import com.codingindia.tamater.models.OrderSummary
import com.codingindia.tamater.utils.Converters
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.CartViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartCheckoutFragment : Fragment(), CartAdapter.CartChange {

    private var _ccBinding: FragmentCartCheckoutBinding? = null
    private val ccBinding get() = _ccBinding!!

    private val cartViewModel by viewModels<CartViewModel>()

    private var adapter: CartAdapter? = null

    private var totalPrice = 0.0

    private var foodList: ArrayList<CartResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = CartAdapter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _ccBinding = FragmentCartCheckoutBinding.inflate(layoutInflater, container, false)
        return _ccBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ccBinding.imgBack.setOnClickListener { findNavController().popBackStack() }

        ccBinding.checkOutRv.layoutManager = LinearLayoutManager(requireContext())
        ccBinding.checkOutRv.adapter = adapter

        getCart()

        ccBinding.orderNowBtn.setOnClickListener { }

        ccBinding.imgRefresh.setOnClickListener {
            getCart()
        }

        ccBinding.orderNowBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("order-summary", Gson().toJson(OrderSummary(foodList,totalPrice)))
            findNavController().navigate(R.id.action_cartCheckoutFragment_to_addressFragment,bundle)
        }
    }

    private fun getCart() {
        cartViewModel.getCart()
        cartViewModel.cartFoodsLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResult.Error -> {
                    ccBinding.progressBar.isVisible = false
                    Snackbar.make(ccBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }

                is NetworkResult.Loding -> {}
                is NetworkResult.Success -> {
                    ccBinding.progressBar.isVisible = false
                    adapter?.submitList(it.data)
                    ccBinding.placeOrder.isVisible = true
                    foodList = it.data!!
                    if(it.data.isEmpty()){
                        ccBinding.placeOrder.isVisible = false
                        ccBinding.emptyCartText.isVisible = true
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ccBinding = null
    }

    override fun onQuantityUpdate(cartId: String, quantity: Int) {
        cartViewModel.updateCart(cartId, quantity)
    }

    override fun totalPrice(itemTotalPrice: Double, isPlus: Boolean) {
        if (isPlus) {
            totalPrice += itemTotalPrice
        } else {
            totalPrice -= itemTotalPrice
        }
        ccBinding.tvPrice.text = Converters.priceFormat(totalPrice)
    }

    override fun deleteCart(cartId: String) {
        cartViewModel.deleteFood(cartId)
        getCart()
    }
}