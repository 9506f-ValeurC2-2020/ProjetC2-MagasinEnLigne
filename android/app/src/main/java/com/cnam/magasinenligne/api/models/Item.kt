package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("id") val id: String = "",
    @SerializedName("orderId") val orderId: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("deliveryCharges") val charges: Float = 0f
)

data class SingleItemResponse(
    @SerializedName("Response") val item: Item
) : CommonResponse()

data class MultipleItemResponse(
    @SerializedName("Response") val items: List<Item>
) : CommonResponse()