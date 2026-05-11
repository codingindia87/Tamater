package com.codingindia.tamater.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.OrderHistoryItemBinding
import com.codingindia.tamater.models.OrderModel
import com.codingindia.tamater.utils.Converters

class OrderHistoryAdapter(
    val context: Context,
    val onHistoryClick: OnHistoryClick
) : ListAdapter<OrderModel,OrderHistoryAdapter.OrderHistoryViewHolder>(ComparatorDiffUtil()) {

    class ComparatorDiffUtil : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val ohItemBinding = OrderHistoryItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return OrderHistoryViewHolder(ohItemBinding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    inner class OrderHistoryViewHolder(private val orderHistoryItemBinding: OrderHistoryItemBinding) :
        RecyclerView.ViewHolder(orderHistoryItemBinding.root){
        fun bind(orderModel: OrderModel){
            var foodsName = ""
            for (foods in orderModel.foods!!){
                foodsName += "$foods "
            }

            orderHistoryItemBinding.foodsName.text = foodsName

            orderHistoryItemBinding.totalPrice.text = Converters.priceFormat(orderModel.orderPrice!!)

            orderHistoryItemBinding.orderStatus.text = orderModel.status

            orderHistoryItemBinding.orderTime.text = Converters.timestampToDateTime(orderModel.timestamp)

            if(orderModel.status == "making"){
                orderHistoryItemBinding.orderStatus.setTextColor(context.getColor(R.color.red))
            }

            if(orderModel.status == "complete"){
                orderHistoryItemBinding.orderStatus.setTextColor(context.getColor(R.color.green))
            }


            if(orderModel.status == "delivery"){
                orderHistoryItemBinding.line.isVisible = true
                orderHistoryItemBinding.trackLocation.isVisible = true
                orderHistoryItemBinding.orderStatus.setTextColor(context.getColor(R.color.orange))
                orderHistoryItemBinding.trackLocation.setOnClickListener {
                    onHistoryClick.onTrackClick(orderModel.id!!)
                }
            }
        }
    }

    interface OnHistoryClick{
        fun onTrackClick(orderHistoryKey: String)
    }
}