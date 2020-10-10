package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("time") val time: Long = 0L,
    @SerializedName("cost") val cost: Float = 0F,
    @SerializedName("fromClient") val fromClient: String = "",
    @SerializedName("toVendeur") val toVendeur: String = ""
)

data class SingleOrderResponse(
    @SerializedName("Response") val order: Order
) : CommonResponse()

data class MultipleOrderResponse(
    @SerializedName("Response") val orders: List<Order>
) : CommonResponse()