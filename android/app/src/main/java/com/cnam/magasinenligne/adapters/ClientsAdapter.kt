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
    private val clients: List<Client>
) : RecyclerView.Adapter<ClientsAdapter.ClientViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        return ClientViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_client,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bindClient(clients[position])
    }

    override fun getItemCount(): Int = clients.size


    class ClientViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindClient(client: Client) {
            val mContext = containerView.context
            with(client) {
                itemView.tv_id.text = mContext.getString(R.string.id, id)
                itemView.tv_name.text = mContext.getString(R.string.name_1, fullName)
                itemView.tv_phone_number.text =
                    mContext.getString(R.string.phone_number_1, phoneNumber)
                if (email.isEmpty()) itemView.tv_email.hide()
                else itemView.tv_email.text = mContext.getString(R.string.email, email)
                itemView.tv_address.text = mContext.getString(R.string.address_1, address)
            }
        }
    }
}