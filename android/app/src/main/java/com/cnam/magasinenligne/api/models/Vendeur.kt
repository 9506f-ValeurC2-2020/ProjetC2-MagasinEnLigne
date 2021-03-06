package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

data class Vendeur(
    @SerializedName("id") val id: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String = "",
    @SerializedName("email") val email: String? = "",
    @SerializedName("image") val image: String? = null
)

data class SingleVendeurResponse(
    @SerializedName("Response") val vendeur: Vendeur
) : CommonResponse()

data class MultipleVendeurResponse(
    @SerializedName("Response") val vendeurs: List<Vendeur>
) : CommonResponse()