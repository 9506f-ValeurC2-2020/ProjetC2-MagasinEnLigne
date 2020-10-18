package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Vendeur
import com.cnam.magasinenligne.utils.hide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_merchant.view.*

class MerchantAdapter(private val merchants: List<Vendeur>) :
    RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        return MerchantViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_merchant,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        holder.bindMerchant(merchants[position])
    }

    override fun getItemCount(): Int = merchants.size
    class MerchantViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindMerchant(merchant: Vendeur) {
            val mContext = containerView.context
            with(merchant) {
                itemView.tv_id.text = mContext.getString(R.string.id, id)
                itemView.tv_name.text = mContext.getString(R.string.name_1, fullName)
                itemView.tv_phone_number.text =
                    mContext.getString(R.string.phone_number_1, phoneNumber)
                if (email.isEmpty()) itemView.tv_email.hide()
                else itemView.tv_email.text = mContext.getString(R.string.email, email)
            }
        }
    }
}