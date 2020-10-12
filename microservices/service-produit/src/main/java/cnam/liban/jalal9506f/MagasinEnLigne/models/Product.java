/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.models;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author jalal
 */
@Document
public class Product {

    @Id
    @Field("id")
    private UUID id;
    @Field("name")
    private String name;
    @Field("category")
    private String category;
    @Field("sex")
    private int sex; // 0 for male, 1 for female
    @Field("price")
    private double price;
    @Field("onSale")
    private boolean onSale;
    @Field("salePrice")
    private double salePrice;
    @Field("ageCategory")
    private String ageCategory; // ex: 10-12 for clothes 18-28 ...
    @Field("image")
    private byte[] image;

    public Product() {
        this.id = UUID.randomUUID();
    }

    public Product(UUID id, String name, String category, int sex, double price, boolean onSale, double salePrice, String ageCategory, byte[] image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.sex = sex;
        this.price = price;
        this.onSale = onSale;
        this.salePrice = salePrice;
        this.ageCategory = ageCategory;
        this.image = image;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(String ageCategory) {
        this.ageCategory = ageCategory;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
