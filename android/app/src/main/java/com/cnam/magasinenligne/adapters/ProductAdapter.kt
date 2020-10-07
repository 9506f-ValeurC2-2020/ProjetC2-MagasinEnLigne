package com.cnam.magasinenligne.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnam.magasinenligne.R
import com.cnam.magasinenligne.models.Product
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_shop.view.*

class ProductAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_shop,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        holder.bindProduct(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindProduct(product: Product){
            with(product){
                itemView.card_shop_item
            }
        }
    }

}