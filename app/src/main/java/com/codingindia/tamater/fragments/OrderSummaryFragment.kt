package com.codingindia.tamater.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingindia.tamater.R
import com.codingindia.tamater.adapters.OrderSummaryAdapter
import com.codingindia.tamater.databinding.FragmentOrderSummaryBinding
import com.codingindia.tamater.models.OrderModel
import com.codingindia.tamater.models.OrderSummary
import com.codingindia.tamater.models.User
import com.codingindia.tamater.utils.Converters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


class OrderSummaryFragment : Fragment() {

    private var _odFragmentBinding: FragmentOrderSummaryBinding? = null
    private val odFragmentBinding get() = _odFragmentBinding!!

    private var orderSummary: OrderSummary? = null
    private var userAddress: User? = null

    private var totalPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _odFragmentBinding = FragmentOrderSummaryBinding.inflate(layoutInflater, container, false)
        return _odFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

        odFragmentBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        odFragmentBinding.btnPay.setOnClickListener {
            val bundle = Bundle()
            val foodList: MutableList<String> = mutableListOf()
            for (foods in orderSummary?.foods!!) {
                foodList.add("${foods.food.name} (${foods.cart.quantity})")
            }
            bundle.putString(
                "order", Gson().toJson(
                    OrderModel(
                        foods = foodList,
                        orderBy = Firebase.auth.uid!!,
                        orderPrice = totalPrice,
                        name = userAddress?.name,
                        address = userAddress?.address,
                        city = userAddress?.city,
                        pinCode = userAddress?.pinCode
                    )
                )
            )
            findNavController().navigate(
                R.id.action_orderSummaryFragment_to_paymentFragment,
                bundle
            )
        }
    }

    private fun setup() {
        val gsonOR = arguments?.getString("order-summary")
        val gsonAddres = arguments?.getString("address")
        if (gsonOR != null) {
            orderSummary = Gson().fromJson(gsonOR, OrderSummary::class.java)

            odFragmentBinding.totalPrice.text = Converters.priceFormat(orderSummary?.totalPrice!!)
            odFragmentBinding.textDelivery.text = Converters.priceFormat(40.0)

            totalPrice = orderSummary?.totalPrice!! + 40.0

            odFragmentBinding.textTotal.text = Converters.priceFormat(totalPrice)

            odFragmentBinding.foodRv.layoutManager = LinearLayoutManager(requireContext())
            val adapter = OrderSummaryAdapter(requireContext())
            adapter.submitList(orderSummary?.foods)
            odFragmentBinding.foodRv.adapter = adapter
        }
        if (gsonAddres != null) {
            userAddress = Gson().fromJson(gsonAddres, User::class.java)

            odFragmentBinding.name.text = userAddress?.name
            odFragmentBinding.address.text = userAddress?.address
            odFragmentBinding.city.text = userAddress?.city
            odFragmentBinding.pinCode.text = userAddress?.pinCode
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _odFragmentBinding = null
    }

}