package com.codingindia.tamater.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingindia.tamater.R
import com.codingindia.tamater.adapters.OrderHistoryAdapter
import com.codingindia.tamater.databinding.FragmentMyOrderBinding
import com.codingindia.tamater.utils.NetworkResult
import com.codingindia.tamater.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyOrderFragment : Fragment(),OrderHistoryAdapter.OnHistoryClick {

    private var _myOrderBinding: FragmentMyOrderBinding? = null
    private val myOrderBinding get() = _myOrderBinding!!

    private val userViewModel by viewModels<UserViewModel>()

    private var adapter: OrderHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _myOrderBinding = FragmentMyOrderBinding.inflate(layoutInflater, container, false)
        return myOrderBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderHistoryAdapter(requireContext(),this)
        myOrderBinding.orderRv.layoutManager = LinearLayoutManager(requireContext())
        myOrderBinding.orderRv.adapter = adapter

        myOrderBinding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        fetchOrderHistory()

    }

    private fun fetchOrderHistory() {
        userViewModel.getOrderHistory()
        userViewModel.orderHistoryLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkResult.Error -> {
                    myOrderBinding.progressBar.isVisible = false
                    myOrderBinding.emptyFoodText.isVisible = true
                    myOrderBinding.emptyFoodText.text = it.message
                }

                is NetworkResult.Loding -> {

                }

                is NetworkResult.Success -> {
                    myOrderBinding.progressBar.isVisible = false
                    adapter?.submitList(it.data)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myOrderBinding = null
    }

    override fun onTrackClick(orderHistoryKey: String) {
        val bundle = Bundle()
        bundle.putString("order-history-id",orderHistoryKey)
        findNavController().navigate(R.id.action_myOrderFragment_to_mapsFragment,bundle)
    }

}