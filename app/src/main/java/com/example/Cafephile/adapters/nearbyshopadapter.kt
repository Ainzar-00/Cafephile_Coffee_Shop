package com.example.f053.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.f053.R
import com.example.f053.models.NearbyShop

class NearbyShopsAdapter(
    private val shops: List<NearbyShop>,
    private val onItemClick: (NearbyShop) -> Unit
) : RecyclerView.Adapter<NearbyShopsAdapter.ShopViewHolder>() {

    inner class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shopName: TextView = itemView.findViewById(R.id.shopName)
        val shopDistance: TextView = itemView.findViewById(R.id.shopDistance)

        fun bind(shop: NearbyShop) {
            shopName.text = shop.name
            shopDistance.text = shop.distance

            itemView.setOnClickListener {
                onItemClick(shop)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nearby_shop, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shops[position])
    }

    override fun getItemCount() = shops.size
}