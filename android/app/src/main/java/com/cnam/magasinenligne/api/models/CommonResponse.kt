package com.cnam.magasinenligne.api.models

import com.google.gson.annotations.SerializedName

open class CommonResponse(
    @SerializedName("Status") open val status: String? = null,
    @SerializedName("Message") open val message: String? = null,
)