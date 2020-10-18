package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("sex") val sex: Int = -1, // 0 male 1 female -1 unspecified
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("onSale") val onSale: Boolean = false,
    @SerializedName("salePrice") val salePrice: Double = 0.0,
    @SerializedName("ageCategory") val ageCategory: String = "",
    @SerializedName("image") val image: String? = null
)

data class SingleProductResponse(
    @SerializedName("Response") val product: Product
) : CommonResponse()

data class MultipleProductResponse(
    @SerializedName("Response") val products: List<Product>
) : CommonResponse()