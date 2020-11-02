package com.cnam.magasinenligne.adapters

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.api.models.Product
import com.cnam.magasinenligne.utils.createBitmap
import com.cnam.magasinenligne.utils.hide
import com.cnam.magasinenligne.utils.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_shop.view.*


class ProductAdapter(
    private val products: ArrayList<Product>,
    private val adminMerchantOrClient: Int = 0 // 0 client 1 merchant 2 admin
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val normalViewType = 0
    private val loadingViewType = 1
    private var isLoaderVisible = false

    private lateinit var onClick: OnClick

    interface OnClick {
        fun onClick(position: Int)
    }

    fun click(click: OnClick) {
        onClick = click
    }

    private lateinit var onSaleClick: OnSaleClick

    interface OnSaleClick {
        fun onSaleClick(position: Int)
    }

    fun saleClick(saleClick: OnSaleClick) {
        onSaleClick = saleClick
    }

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
            normalViewType -> if (adminMerchantOrClient == 1 || adminMerchantOrClient == 2) ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_product,
                        parent,
                        false
                    )
            ) else ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_shop,
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
        if (getItemViewType(position) == normalViewType) {
            holder.bindProduct(products[position], position)
            if (adminMerchantOrClient == 0) {
                holder.itemView.bt_get.setOnClickListener {
                    onClick.onClick(position)
                }
            }
            if (adminMerchantOrClient == 1) {
                holder.itemView.bt_put_on_sale.setOnClickListener {
                    onSaleClick.onSaleClick(position)
                }
            }

        }
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        @SuppressLint("StringFormatMatches")
        fun bindProduct(product: Product, position: Int) {
            val mContext = containerView.context
            with(product) {
                if (adminMerchantOrClient == 1 || adminMerchantOrClient == 2) {
                    itemView.tv_product_counter.text =
                        mContext.getString(R.string.product, position + 1)
                    if (!image.isNullOrEmpty()) {
                        itemView.iv_image.setImageBitmap(createBitmap(image))
                    } else {
                        itemView.iv_image.setImageResource(R.drawable.ic_p)
                    }
                    itemView.tv_id.text = mContext.getString(R.string.id, id)
                    itemView.tv_product_name.text = mContext.getString(R.string.name_1, name)
                    itemView.tv_category.text = mContext.getString(R.string.category_1, category)
                    itemView.tv_price.text = mContext.getString(R.string.price_1, "$price")
                    if (category == "clothing") {
                        val sex = if (sex == 0) "male" else "female"
                        itemView.tv_sex.text = mContext.getString(R.string.sex_1, sex)
                        itemView.tv_age_category.text =
                            mContext.getString(R.string.age_category, ageCategory)
                        itemView.group_sex.show()
                    } else {
                        itemView.group_sex.hide()
                    }
                    if (onSale) {
                        itemView.tv_on_sale.text = mContext.getString(R.string.on_sale, "$onSale")
                        itemView.tv_sale_price.text =
                            mContext.getString(R.string.sale_price, "$salePrice")
                        itemView.group_sales.show()
                    } else {
                        itemView.group_sales.hide()
                    }
                    if (adminMerchantOrClient == 1) {
                        itemView.bt_put_on_sale.show()
                    } else {
                        itemView.bt_put_on_sale.hide()
                    }
                } else {
                    if (!image.isNullOrEmpty()) {
                        itemView.iv_item.setImageBitmap(createBitmap(image))
                    } else {
                        itemView.iv_item.setImageResource(R.drawable.no_image)
                    }
                    var description = "$name "
                    if (onSale) {
                        itemView.iv_sale.show()
                        description += "<strike>($price)</strike>"
                        description += " $salePrice"
                        itemView.tv_name.text = Html.fromHtml(description)
                    } else {
                        itemView.iv_sale.hide()
                        description += "($price)"
                        itemView.tv_name.text = description
                    }

                }

            }
        }
    }
}