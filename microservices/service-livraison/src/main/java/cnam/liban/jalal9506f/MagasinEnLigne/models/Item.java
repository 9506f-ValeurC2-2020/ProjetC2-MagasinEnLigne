/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.models;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author jalal
 */
public class Item {
    
    @Id
    @Field("id")
    private UUID id;
    
    @Field("orderId")
    private UUID orderId;
    
    @Field("status")
    private int status; // 0 for pending delivery 1 for delivered item
    
    @Field("deliveryCharges")
    private double deliveryCharges; // order cost+ delivery charges

    public Item() {
        this.id = UUID.randomUUID();
        this.deliveryCharges = 3000;
    }
    
    public Item(UUID id, UUID orderId, int status, double deliveryCharges) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.deliveryCharges = deliveryCharges;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }
    
    
}
