package com.cnam.magasinenligne.api

import com.cnam.magasinenligne.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    //<editor-fold desc="client">
    @FormUrlEncoded
    @POST("/getClients")
    fun getClients(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleClientResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveClient(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/findClient")
    fun findClient(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleClientResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteClient(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateClient(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/login")
    fun loginClient(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/checkIn")
    fun checkInClient(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/addToWish")
    fun addToWish(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/removeWish")
    fun removeWish(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/findClientById")
    fun findClientById(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/sendBroadcast")
    fun sendBroadcast(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleClientResponse>

    @Multipart
    @POST("/updatePhoto")
    fun updateClientPhoto(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part media: MultipartBody.Part?
    ): Call<SingleClientResponse>
    //</editor-fold>


    //<editor-fold desc="vendeur">
    @FormUrlEncoded
    @POST("/getVendeurs")
    fun getVendeurs(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleVendeurResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveVendeur(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/findVendeur")
    fun findVendeur(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleVendeurResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteVendeur(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateVendeur(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/login")
    fun loginVendeur(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/checkIn")
    fun checkInVendeur(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleVendeurResponse>

    @Multipart
    @POST("/updatePhoto")
    fun updateMerchantPhoto(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part media: MultipartBody.Part?
    ): Call<SingleVendeurResponse>
    //</editor-fold>

    //<editor-fold desc="order">
    @FormUrlEncoded
    @POST("/getOrders")
    fun getOrders(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveOrder(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleOrderResponse>

    @FormUrlEncoded
    @POST("/findOrder")
    fun findOrder(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteOrder(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleOrderResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateOrder(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleOrderResponse>

    @FormUrlEncoded
    @POST("/getClientOrders")
    fun getClientOrders(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/getMerchantOrders")
    fun getMerchantOrders(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleOrderResponse>
    //</editor-fold>

    //<editor-fold desc="product">
    @FormUrlEncoded
    @POST("/getProducts")
    fun getProducts(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleProductResponse>

    @FormUrlEncoded
    @POST("/getMerchantProducts")
    fun getMerchantProducts(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleProductResponse>

    @FormUrlEncoded
    @POST("/getWishes")
    fun getWishes(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleProductResponse>

    @Multipart
    @POST("/save")
    fun saveProduct(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part media: MultipartBody.Part?
    ): Call<SingleProductResponse>

    @FormUrlEncoded
    @POST("/findProduct")
    fun findProduct(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleProductResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteProduct(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleProductResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateProduct(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleProductResponse>

    @FormUrlEncoded
    @POST("/findSales")
    fun findSales(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleProductResponse>

    @FormUrlEncoded
    @POST("/putOnSale")
    fun putOnSale(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleProductResponse>

    //</editor-fold>

    //<editor-fold desc="delivery">
    @FormUrlEncoded
    @POST("/getItems")
    fun getItems(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleItemResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveItem(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleItemResponse>

    @FormUrlEncoded
    @POST("/findItem")
    fun findItem(@FieldMap(encoded = true) map: HashMap<String, String>): Call<MultipleItemResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteItem(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleItemResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateItem(@FieldMap(encoded = true) map: HashMap<String, String>): Call<SingleItemResponse>
    //</editor-fold>

}