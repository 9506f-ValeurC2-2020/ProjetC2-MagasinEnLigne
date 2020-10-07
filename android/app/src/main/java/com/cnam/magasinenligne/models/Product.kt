package com.cnam.magasinenligne.models

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id:String,
    @SerializedName("name") val name:String,
    @SerializedName("details") val details:String,
    @SerializedName("price") val price:Float,
    @SerializedName("category") val category:Float,
    @SerializedName("gender") val gender:String,
    @SerializedName("age") val age:String,
    @SerializedName("image") val image:String
)