package com.codingindia.tamater.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codingindia.tamater.R
import com.codingindia.tamater.databinding.MainItemsBinding
import com.codingindia.tamater.models.FoodModel
import com.codingindia.tamater.utils.Converters.priceFormat

class FoodAdapter(
    val context: Context,
    val foodAdapterClick: FoodAdapterClick
):ListAdapter<FoodModel,FoodAdapter.FoodViewHolder>(ComparatorDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val foodItemBinding = MainItemsBinding.inflate(LayoutInflater.from(context),parent,false)
        return FoodViewHolder(foodItemBinding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    inner class FoodViewHolder(private val itemBinding: MainItemsBinding):RecyclerView.ViewHolder(itemBinding.root){
        fun bind(food: FoodModel){

            Glide.with(itemBinding.foodImage.context)
                .load(food.image)
                .placeholder(R.drawable.food_image)
                .into(itemBinding.foodImage)

            itemBinding.tvName.text = food.name
            itemBinding.description.text = food.description
            itemBinding.tvPrice.text = priceFormat(food.price)

            if(food.type == "veg")
                itemBinding.addType.setImageDrawable(context.getDrawable(R.drawable.square_dot_green_24px))
            else
                itemBinding.addType.setImageDrawable(context.getDrawable(R.drawable.square_dot_red_24px))


            itemBinding.parentLayout.setOnClickListener {
                foodAdapterClick.onParentClick(food)
            }


        }
    }

    interface FoodAdapterClick{
        fun onParentClick(food:FoodModel)
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<FoodModel>() {
        override fun areItemsTheSame(oldItem: FoodModel, newItem: FoodModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodModel, newItem: FoodModel): Boolean {
            return oldItem == newItem
        }
    }
}