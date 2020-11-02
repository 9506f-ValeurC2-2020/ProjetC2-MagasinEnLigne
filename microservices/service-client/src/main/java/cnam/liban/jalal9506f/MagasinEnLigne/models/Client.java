/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.models;

import java.util.ArrayList;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author jalal
 */
@Document
public class Client {

    @Id
    @Field("id")
    private UUID id;
    @Field("fullName")
    private String fullName;
    @Field("password")
    private String password;
    @Field("phoneNumber")
    private String phoneNumber;
    @Field("email")
    private String email;
    @Field("address")
    private String address;
    @Field("image")
    private byte[] image;
    @Field("wishList")
    private ArrayList<UUID> wishList;

    public Client() {
        this.id = UUID.randomUUID();
    }

    public Client(UUID id, String fullName, String password, String phoneNumber, String email, String address, byte[] image, ArrayList<UUID> wishList) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.image = image;
        this.wishList = wishList;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public ArrayList<UUID> getWishList() {
        if (wishList == null) {
            return new ArrayList();
        }
        return wishList;
    }

    public void setWishList(ArrayList<UUID> wishList) {
        this.wishList = wishList;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
