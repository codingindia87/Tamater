package com.codingindia.tamater.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.CartItemsBinding
import com.codingindia.tamater.models.CartResponse
import com.codingindia.tamater.utils.Converters

class CartAdapter(
    val context: Context,
    val cartChange: CartChange
) : ListAdapter<CartResponse, CartAdapter.CartViewHolder>(ComparatorDiffUtil()) {

    var itemTotalPrice = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val foodItemBinding = CartItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(foodItemBinding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    inner class CartViewHolder(private val cartItemsBinding: CartItemsBinding) :
        RecyclerView.ViewHolder(cartItemsBinding.root) {
        fun bind(cartResponse: CartResponse) {
            Glide.with(context)
                .load(cartResponse.food.image)
                .placeholder(R.drawable.food_image)
                .into(cartItemsBinding.foodIv)

            cartItemsBinding.tvName.text = cartResponse.food.name

            var price = cartResponse.food.price
            var quantity = cartResponse.cart.quantity

            itemTotalPrice = price.times(quantity)

            cartItemsBinding.tvPrice.text = Converters.priceFormat(itemTotalPrice)

            cartItemsBinding.tvFoodQuantity.text = quantity.toString()

            cartChange.totalPrice(itemTotalPrice)

            cartItemsBinding.imgAdd.setOnClickListener {
                if (quantity != 10) {
                    quantity++
                    itemTotalPrice = price.times(quantity)
                    cartItemsBinding.tvFoodQuantity.text = quantity.toString()
                    cartItemsBinding.tvPrice.text = Converters.priceFormat(itemTotalPrice)
                    cartChange.onQuantityUpdate(cartResponse.id, quantity)
                    cartChange.totalPrice(cartResponse.food.price)
                    cartResponse.cart.quantity = quantity
                }
            }

            cartItemsBinding.imgRemove.setOnClickListener {
                if (quantity != 1) {
                    quantity--
                    itemTotalPrice = price.times(quantity)
                    cartItemsBinding.tvFoodQuantity.text = quantity.toString()
                    cartItemsBinding.tvPrice.text = Converters.priceFormat(itemTotalPrice)
                    cartChange.onQuantityUpdate(cartResponse.id, quantity)
                    cartChange.totalPrice(cartResponse.food.price, false)
                    cartResponse.cart.quantity = quantity
                }
            }

            cartItemsBinding.deleteItems.setOnClickListener {
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setTitle("Delete Food")
                dialogBuilder.setMessage("Do you truly delete this food form your cart")
                dialogBuilder.setPositiveButton(
                    "DELETE"
                ) { _, _ ->
                    cartChange.totalPrice(price.times(quantity),false)
                    cartChange.deleteCart(cartResponse.id)
                }
                dialogBuilder.setNeutralButton("NO") { _, _ ->
                    {

                    }
                }
                dialogBuilder.show()
            }
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<CartResponse>() {
        override fun areItemsTheSame(oldItem: CartResponse, newItem: CartResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartResponse, newItem: CartResponse): Boolean {
            return oldItem == newItem
        }
    }

    interface CartChange {
        fun onQuantityUpdate(cartId: String, quantity: Int)
        fun totalPrice(totalPrice: Double, isPlus: Boolean = true)
        fun deleteCart(cartID:String)
    }

}