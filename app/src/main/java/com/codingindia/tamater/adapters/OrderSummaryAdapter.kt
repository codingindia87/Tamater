package com.codingindia.tamater.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.OrderSummaryItemsBinding
import com.codingindia.tamater.models.CartResponse
import com.codingindia.tamater.utils.Converters

class OrderSummaryAdapter(
    val context: Context
) : ListAdapter<CartResponse, OrderSummaryAdapter.OrderSummaryViewHolder>(ComparatorDiffUtil()) {


    class ComparatorDiffUtil : DiffUtil.ItemCallback<CartResponse>() {
        override fun areItemsTheSame(oldItem: CartResponse, newItem: CartResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartResponse, newItem: CartResponse): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val foodItemBinding =
            OrderSummaryItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderSummaryViewHolder(foodItemBinding)
    }

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    inner class OrderSummaryViewHolder(private val orderSummaryItemsBinding: OrderSummaryItemsBinding) :
        RecyclerView.ViewHolder(orderSummaryItemsBinding.root) {
        fun bind(cartResponse: CartResponse) {
            Glide.with(context)
                .load(cartResponse.food.image)
                .placeholder(R.drawable.food_image)
                .into(orderSummaryItemsBinding.foodIv)

            orderSummaryItemsBinding.tvName.text = cartResponse.food.name

            orderSummaryItemsBinding.qPrice.text = "${Converters.priceFormat(cartResponse.food.price)}×${cartResponse.cart.quantity}"

            orderSummaryItemsBinding.tvPrice.text = Converters.priceFormat(cartResponse.food.price.times(cartResponse.cart.quantity))

        }
    }
}