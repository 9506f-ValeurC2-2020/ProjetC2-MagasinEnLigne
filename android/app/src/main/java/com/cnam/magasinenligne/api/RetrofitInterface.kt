package com.cnam.magasinenligne.api

import com.cnam.magasinenligne.api.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.http.*

interface RetrofitInterface {
    //<editor-fold desc="client">
    @POST("/getClients")
    fun getClients(): Callback<MultipleClientResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveClient(@FieldMap map: HashMap<String, Int>): Callback<SingleClientResponse>

    @FormUrlEncoded
    @POST("/findClient")
    fun findClient(@FieldMap map: HashMap<String, Int>): Callback<MultipleClientResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteClient(@FieldMap map: HashMap<String, Int>): Callback<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateClient(@FieldMap map: HashMap<String, Int>): Callback<SingleClientResponse>
    //</editor-fold>


    //<editor-fold desc="vendeur">
    @POST("/getVendeurs")
    fun getVendeurs(): Callback<MultipleVendeurResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveVendeur(@FieldMap map: HashMap<String, Int>): Callback<SingleVendeurResponse>

    @FormUrlEncoded
    @POST("/findVendeur")
    fun findVendeur(@FieldMap map: HashMap<String, Int>): Callback<MultipleVendeurResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteVendeur(@FieldMap map: HashMap<String, Int>): Callback<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateVendeur(@FieldMap map: HashMap<String, Int>): Callback<SingleVendeurResponse>
    //</editor-fold>

    //<editor-fold desc="order">
    @POST("/getOrders")
    fun getOrders(): Callback<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveOrder(@FieldMap map: HashMap<String, Int>): Callback<SingleOrderResponse>

    @FormUrlEncoded
    @POST("/findOrder")
    fun findOrder(@FieldMap map: HashMap<String, Int>): Callback<MultipleOrderResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteOrder(@FieldMap map: HashMap<String, Int>): Callback<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateOrder(@FieldMap map: HashMap<String, Int>): Callback<SingleOrderResponse>
    //</editor-fold>

    //<editor-fold desc="product">
    @FormUrlEncoded
    @POST("/getProducts")
    fun getProducts(@FieldMap map: HashMap<String, Int>): Callback<MultipleProductResponse>

    @Multipart
    @POST("/save")
    fun saveProduct(
        @PartMap fields: HashMap<String, RequestBody>,
        @Part media: MultipartBody.Part?
    ): Callback<SingleProductResponse>

    @FormUrlEncoded
    @POST("/findProduct")
    fun findProduct(@FieldMap map: HashMap<String, Int>): Callback<MultipleProductResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteProduct(@FieldMap map: HashMap<String, Int>): Callback<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateProduct(@FieldMap map: HashMap<String, Int>): Callback<SingleProductResponse>
    //</editor-fold>

    //<editor-fold desc="delivery">
    @POST("/getItems")
    fun getItems(): Callback<MultipleItemResponse>

    @FormUrlEncoded
    @POST("/save")
    fun saveItem(@FieldMap map: HashMap<String, Int>): Callback<SingleItemResponse>

    @FormUrlEncoded
    @POST("/findItem")
    fun findItem(@FieldMap map: HashMap<String, Int>): Callback<MultipleItemResponse>

    @FormUrlEncoded
    @POST("/delete")
    fun deleteItem(@FieldMap map: HashMap<String, Int>): Callback<CommonResponse>

    @FormUrlEncoded
    @POST("/update")
    fun updateItem(@FieldMap map: HashMap<String, Int>): Callback<SingleItemResponse>
    //</editor-fold>

}