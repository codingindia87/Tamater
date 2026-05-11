package com.codingindia.tamater.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.codingindia.tamater.PaymentActivity
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.FragmentPaymentBinding
import com.codingindia.tamater.models.OrderModel
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.CartViewModel
import com.codingindia.tamater.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private var _paymentBinding: FragmentPaymentBinding? = null
    private val paymentBinding get() = _paymentBinding!!

    private val orderViewmodel by viewModels<OrderViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()

    private var orderModel: OrderModel? = null

    private var isCOD = true

    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val status = data.getBooleanExtra("status", false)
                    val id = data.getStringExtra("id")
                    val error = data.getStringExtra("error")
                    if (status) {
                        orderModel?.orderId = id
                        orderModel?.paymentStatus = "success"
                        createOrder()
                    } else {
                        Snackbar.make(paymentBinding.root, "Error: ${error}", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gsonOrder = arguments?.getString("order")
        if (gsonOrder != null) {
            orderModel = Gson().fromJson(gsonOrder, OrderModel::class.java)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _paymentBinding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        return _paymentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentBinding.rBCod.isChecked = true
        paymentBinding.paymentMethodRg.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.r_b_online -> {
                    isCOD = false
                }

                R.id.r_b_cod -> {
                    isCOD = true
                }
            }
        }


        paymentBinding.btnPay.setOnClickListener {
            if (isCOD) createOrder()
            else openPaymnetActivity()
        }

        paymentBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun createOrder() {
        if (isCOD) orderModel?.paymentMethod = "cash on delivery"
        else orderModel?.paymentMethod = "online"
        orderViewmodel.placeOrder(orderModel!!)
        orderViewmodel.placeOrderLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResult.Error -> {
                    Snackbar.make(paymentBinding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                    paymentBinding.payText.isVisible = true
                    paymentBinding.payProgress.isVisible = false
                }

                is NetworkResult.Loding -> {
                    paymentBinding.payText.isVisible = false
                    paymentBinding.payProgress.isVisible = true
                }

                is NetworkResult.Success -> {
                    deleteCart(it.data!!)
                }
            }
        })
    }

    fun deleteCart(message: String) {
        cartViewModel.deleteCart()
        cartViewModel.deleteLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResult.Error -> {
                    Snackbar.make(paymentBinding.root, it.message!!, Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(
                        R.id.mainFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.mainFragment, true)
                            .build()
                    )
                }

                is NetworkResult.Loding -> {

                }

                is NetworkResult.Success -> {
                    Snackbar.make(paymentBinding.root, message, Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(
                        R.id.mainFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.mainFragment, true)
                            .build()
                    )
                }
            }
        })
    }

    fun openPaymnetActivity() {
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra("price", orderModel?.orderPrice)
        resultLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _paymentBinding = null
    }


}