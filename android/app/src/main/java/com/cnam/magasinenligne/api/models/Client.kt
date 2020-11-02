package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String = "",
    @SerializedName("email") val email: String? = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("image") val image: String? = null,
)

data class SingleClientResponse(
    @SerializedName("Response") val client: Client
) : CommonResponse()

data class MultipleClientResponse(
    @SerializedName("Response") val clients: List<Client>
) : CommonResponse()