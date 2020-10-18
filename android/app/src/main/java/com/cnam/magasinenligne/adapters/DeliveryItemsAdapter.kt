package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Item
import kotlinx.android.extensions.LayoutContainer

class DeliveryItemsAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<DeliveryItemsAdapter.DeliveryItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryItemViewHolder {
        return DeliveryItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_delivery,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: DeliveryItemViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount(): Int = items.size

    class DeliveryItemViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindItem(item: Item) {
            with(item) {}
        }
    }
}