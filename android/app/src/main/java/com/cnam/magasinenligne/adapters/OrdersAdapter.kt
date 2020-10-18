package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Order
import com.cnam.magasinenligne.utils.getTimeFromLong
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.view.*

class OrdersAdapter(private val orders: ArrayList<Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    private val normalViewType = 0
    private val loadingViewType = 1
    private var isLoaderVisible = false

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == orders.size - 1) loadingViewType else normalViewType
        } else {
            normalViewType
        }
    }

    fun addItems(mOrders: List<Order>) {
        orders.addAll(mOrders)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        orders.add(Order())
        notifyItemInserted(orders.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = orders.size - 1
        val item: Order? = getItem(position)
        if (item != null) {
            orders.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        orders.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Order? {
        return orders[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return when (viewType) {
            normalViewType -> OrderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_merchant,
                        parent,
                        false
                    )
            )
            else -> OrderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        if (getItemViewType(position) == normalViewType)
            holder.bindOrder(orders[position], position)
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindOrder(order: Order, position: Int) {
            val mContext = containerView.context
            with(order) {
                itemView.tv_order_counter.text = mContext.getString(R.string.order, position + 1)
                itemView.tv_id.text = mContext.getString(R.string.id, id)
                itemView.tv_time.text = mContext.getString(R.string.time, getTimeFromLong(time))
                itemView.tv_description.text = mContext.getString(R.string.description, description)
                itemView.tv_cost.text = mContext.getString(R.string.cost, "$cost L.L")
                itemView.tv_from_client.text = mContext.getString(R.string.from_client, fromClient)
                itemView.tv_to_merchant.text = mContext.getString(R.string.to_merchant, toVendeur)
            }
        }
    }
}