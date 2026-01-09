package com.example.f053.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.f053.R
import com.example.f053.db.CoffeeDatabase
import com.example.f053.models.Drink

class FamousDrinksAdapter(
    private val drinks: List<Drink>,
    private val onItemClick: (Drink) -> Unit
) : RecyclerView.Adapter<FamousDrinksAdapter.DrinkViewHolder>() {

    inner class DrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drinkImage: ImageView = itemView.findViewById(R.id.drinkImage)
        val drinkName: TextView = itemView.findViewById(R.id.drinkName)
        val drinkDesc: TextView = itemView.findViewById(R.id.drinkDescription)
        val drinkPrice: TextView = itemView.findViewById(R.id.drinkPrice)
        val badgeText: TextView = itemView.findViewById(R.id.badgeText)
        val addButton: ImageView = itemView.findViewById(R.id.addButton)

        fun bind(drink: Drink) {
            drinkImage.setImageResource(drink.imageRes)
            drinkName.text = drink.name
            drinkDesc.text = drink.description
            drinkPrice.text = "$${drink.price}"

            if (drink.badge != null) {
                badgeText.visibility = View.VISIBLE
                badgeText.text = drink.badge
            } else {
                badgeText.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(drink)
            }

            addButton.setOnClickListener {
                CoffeeDatabase.addToCart(drink)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_famous_drink, parent, false)
        return DrinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        holder.bind(drinks[position])
    }

    override fun getItemCount() = drinks.size
}