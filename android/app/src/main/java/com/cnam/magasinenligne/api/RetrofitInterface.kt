package com.cnam.magasinenligne.api

import com.cnam.magasinenligne.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    //<editor-fold desc="client">
    @POST("/getClients")
    fun getClients(): Call<MultipleClientResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveClient(@FieldMap map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/findClient")
    fun findClient(@FieldMap map: HashMap<String, String>): Call<MultipleClientResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteClient(@FieldMap map: HashMap<String, String>): Call<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateClient(@FieldMap map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/login")
    fun loginClient(@FieldMap map: HashMap<String, String>): Call<SingleClientResponse>

    @FormUrlEncoded
    @POST("/checkIn")
    fun checkInClient(@FieldMap map: HashMap<String, String>): Call<SingleClientResponse>
    //</editor-fold>


    //<editor-fold desc="vendeur">
    @POST("/getVendeurs")
    fun getVendeurs(): Call<MultipleVendeurResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveVendeur(@FieldMap map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/findVendeur")
    fun findVendeur(@FieldMap map: HashMap<String, String>): Call<MultipleVendeurResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteVendeur(@FieldMap map: HashMap<String, String>): Call<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateVendeur(@FieldMap map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/login")
    fun loginVendeur(@FieldMap map: HashMap<String, String>): Call<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/checkIn")
    fun checkInVendeur(@FieldMap map: HashMap<String, String>): Call<SingleVendeurResponse>
    //</editor-fold>

    //<editor-fold desc="order">
    @POST("/getOrders")
    fun getOrders(): Call<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveOrder(@FieldMap map: HashMap<String, String>): Call<SingleOrderResponse>

    @FormUrlEncoded
    @POST("/findOrder")
    fun findOrder(@FieldMap map: HashMap<String, String>): Call<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteOrder(@FieldMap map: HashMap<String, String>): Call<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateOrder(@FieldMap map: HashMap<String, String>): Call<SingleOrderResponse>
    //</editor-fold>

    //<editor-fold desc="product">
    @FormUrlEncoded
    @POST("/getProducts")
    fun getProducts(@FieldMap map: HashMap<String, String>): Call<MultipleProductResponse>

    @Multipart
    @POST("/save")
    fun saveProduct(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part media: MultipartBody.Part?
    ): Call<SingleProductResponse>

    @FormUrlEncoded
    @POST("/findProduct")
    fun findProduct(@FieldMap map: HashMap<String, String>): Call<MultipleProductResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteProduct(@FieldMap map: HashMap<String, String>): Call<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateProduct(@FieldMap map: HashMap<String, String>): Call<SingleProductResponse>
    //</editor-fold>

    //<editor-fold desc="delivery">
    @POST("/getItems")
    fun getItems(): Call<MultipleItemResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveItem(@FieldMap map: HashMap<String, String>): Call<SingleItemResponse>

    @FormUrlEncoded
    @POST("/findItem")
    fun findItem(@FieldMap map: HashMap<String, String>): Call<MultipleItemResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteItem(@FieldMap map: HashMap<String, String>): Call<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateItem(@FieldMap map: HashMap<String, String>): Call<SingleItemResponse>
    //</editor-fold>

}