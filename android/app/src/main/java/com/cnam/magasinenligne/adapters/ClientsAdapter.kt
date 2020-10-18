package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Client
import com.cnam.magasinenligne.utils.hide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_client.view.*

class ClientsAdapter(
    private val clients: ArrayList<Client>
) : RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {
    private val normalViewType = 0
    private val loadingViewType = 1
    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        return when (viewType) {
            normalViewType -> ClientViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_client,
                        parent,
                        false
                    )
            )
            else -> ClientViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        if (getItemViewType(position) == normalViewType)
            holder.bindClient(clients[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == clients.size - 1) loadingViewType else normalViewType
        } else {
            normalViewType
        }
    }

    override fun getItemCount(): Int = clients.size

    fun addItems(mClients: List<Client>) {
        clients.addAll(mClients)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        clients.add(Client())
        notifyItemInserted(clients.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = clients.size - 1
        val item: Client? = getItem(position)
        if (item != null) {
            clients.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        clients.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Client? {
        return clients[position]
    }

    class ClientViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindClient(client: Client, position: Int) {
            val mContext = containerView.context
            with(client) {
                itemView.tv_client_counter.text = mContext.getString(R.string.client, position + 1)
                itemView.tv_id.text = mContext.getString(R.string.id, id)
                itemView.tv_name.text = mContext.getString(R.string.name_1, fullName)
                itemView.tv_phone_number.text =
                    mContext.getString(R.string.phone_number_1, phoneNumber)
                if (email.isNullOrEmpty()) itemView.tv_email.hide()
                else itemView.tv_email.text = mContext.getString(R.string.email, email)
                itemView.tv_address.text = mContext.getString(R.string.address_1, address)
            }
        }
    }
}