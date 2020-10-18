package com.cnam.magasinenligne.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Product
import com.cnam.magasinenligne.utils.logDebug
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter(
    private val products: ArrayList<Product>,
    private val isAdmin: Boolean = false
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val normalViewType = 0
    private val loadingViewType = 1
    private var isLoaderVisible = false
    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == products.size - 1) loadingViewType else normalViewType
        } else {
            normalViewType
        }
    }

    fun addItems(mProducts: List<Product>) {
        products.addAll(mProducts)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        products.add(Product())
        notifyItemInserted(products.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = products.size - 1
        val item: Product? = getItem(position)
        if (item != null) {
            products.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        products.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): Product? {
        return products[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return when (viewType) {
            normalViewType -> ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_merchant,
                        parent,
                        false
                    )
            )
            else -> ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        if (getItemViewType(position) == normalViewType)
            holder.bindProduct(products[position], position)
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        @SuppressLint("StringFormatMatches")
        fun bindProduct(product: Product, position: Int) {
            val mContext = containerView.context
            with(product) {
                if (isAdmin) {
                    itemView.tv_product_counter.text =
                        mContext.getString(R.string.product, position + 1)
                    try {
                        Picasso.get().load(image).into(itemView.iv_image)
                    } catch (e: Exception) {
                        logDebug(e.message.toString())
                    }
                    itemView.tv_id.text = mContext.getString(R.string.id, id)
                    itemView.tv_product_name.text = mContext.getString(R.string.name_1, name)
                    itemView.tv_category.text = mContext.getString(R.string.category_1, category)
                    if (sex != -1) {
                        val sex = if (sex == 0) "male" else "female"
                        itemView.tv_sex.text = mContext.getString(R.string.sex_1, sex)
                        itemView.tv_age_category.text =
                            mContext.getString(R.string.age_category, ageCategory)
                    } else {
                        itemView.group_sex.visibility = View.GONE
                    }
                    if (onSale) {
                        itemView.tv_on_sale.text = mContext.getString(R.string.on_sale, "$onSale")
                        itemView.tv_sale_price.text =
                            mContext.getString(R.string.sale_price, "$salePrice")
                    } else {
                        itemView.group_sales.visibility = View.GONE
                    }
                } else {
                    //itemView.card_shop_item
                }

            }
        }
    }

}