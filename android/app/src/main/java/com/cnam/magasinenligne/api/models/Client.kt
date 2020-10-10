package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id: String = "",
    @SerializedName("fullName") val firstName: String = "",
    @SerializedName("password") val lastName: String = "",
    @SerializedName("phoneNumber") val phoneNumber: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("address") val address: String = ""
)

data class SingleClientResponse(
    @SerializedName("Response") val client: Client
) : CommonResponse()

data class MultipleClientResponse(
    @SerializedName("Response") val clients: List<Client>
) : CommonResponse()