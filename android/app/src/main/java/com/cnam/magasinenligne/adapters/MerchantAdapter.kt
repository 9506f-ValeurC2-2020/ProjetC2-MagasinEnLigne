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

class MerchantAdapter(private val merchants: ArrayList<Vendeur>) :
    RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder>() {
    private val normalViewType = 0
    private val loadingViewType = 1
    private var isLoaderVisible = false
    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == merchants.size - 1) loadingViewType else normalViewType
        } else {
            normalViewType
        }
    }

    fun addItems(mVendeurs: List<Vendeur>) {
        merchants.addAll(mVendeurs)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        merchants.add(Vendeur())
        notifyItemInserted(merchants.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = merchants.size - 1
        val item: Vendeur? = getItem(position)
        if (item != null) {
            merchants.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        merchants.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Vendeur? {
        return merchants[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        return when (viewType) {
            normalViewType -> MerchantViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_merchant,
                        parent,
                        false
                    )
            )
            else -> MerchantViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        if (getItemViewType(position) == normalViewType)
            holder.bindMerchant(merchants[position], position)
    }

    override fun getItemCount(): Int = merchants.size
    class MerchantViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bindMerchant(merchant: Vendeur, position: Int) {
            val mContext = containerView.context
            with(merchant) {
                itemView.tv_merchant_counter.text =
                    mContext.getString(R.string.merchant, position + 1)
                itemView.tv_id.text = mContext.getString(R.string.id, id)
                itemView.tv_name.text = mContext.getString(R.string.name_1, fullName)
                itemView.tv_phone_number.text =
                    mContext.getString(R.string.phone_number_1, phoneNumber)
                if (email.isNullOrEmpty()) itemView.tv_email.hide()
                else itemView.tv_email.text = mContext.getString(R.string.email, email)
            }
        }
    }
}