package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Client
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_customer.view.*

class CustomersAdapter(
    private val clients: ArrayList<Client>
) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {
    private val customers = ArrayList<Client>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_customer,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val client = clients[position]
        holder.bindCustomer(client)
        holder.itemView.cb_customer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addSelected(client)
            } else {
                removeSelected(client)
            }
        }
        if (customers.find { c -> c.id == client.id } != null) {
            holder.select()
        } else {
            holder.unSelect()
        }
    }


    override fun getItemCount(): Int = clients.size

    fun getCustomers(): ArrayList<Client> = customers

    fun addSelected(client: Client) {
        customers.add(client)
    }

    fun removeSelected(client: Client) {
        customers.remove(client)
    }

    fun clearSelection() = customers.clear()
    fun selectAll() {
        for (c in clients) addSelected(c)
    }

    class CustomerViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindCustomer(client: Client) {
            with(client) {
                itemView.tv_name.text = fullName
            }
        }

        fun select() {
            itemView.cb_customer.isChecked = true
        }

        fun unSelect() {
            itemView.cb_customer.isChecked = false
        }

        fun isSelected(): Boolean = itemView.cb_customer.isChecked
    }
}