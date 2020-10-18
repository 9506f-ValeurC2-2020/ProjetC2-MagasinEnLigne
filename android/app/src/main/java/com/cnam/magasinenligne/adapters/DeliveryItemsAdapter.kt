package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Item
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_delivery.view.*

class DeliveryItemsAdapter(private val items: ArrayList<Item>) :
    RecyclerView.Adapter<DeliveryItemsAdapter.DeliveryItemViewHolder>() {
    private val normalViewType = 0
    private val loadingViewType = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryItemViewHolder {
        return when (viewType) {
            normalViewType -> DeliveryItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_delivery,
                        parent,
                        false
                    )
            )
            else -> DeliveryItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: DeliveryItemViewHolder, position: Int) {
        if (getItemViewType(position) == normalViewType)
            holder.bindItem(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == items.size - 1) loadingViewType else normalViewType
        } else {
            normalViewType
        }
    }

    fun addItems(mItems: List<Item>) {
        items.addAll(mItems)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        items.add(Item())
        notifyItemInserted(items.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = items.size - 1
        val item: Item? = getItem(position)
        if (item != null) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Item? {
        return items[position]
    }

    class DeliveryItemViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindItem(item: Item, position: Int) {
            val mContext = containerView.context
            with(item) {
                itemView.tv_item_counter.text = mContext.getString(R.string.item, position + 1)
                itemView.tv_id.text = mContext.getString(R.string.id, id)
                itemView.tv_order_id.text = mContext.getString(R.string.order_id, orderId)
                itemView.tv_charges.text =
                    mContext.getString(R.string.delivery_charges, "$charges L.L")
                itemView.tv_status.text = mContext.getString(R.string.status, status)
            }
        }
    }
}