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
public class Order {

    @Id
    @Field("id")
    private UUID id;
    @Field("description")
    private String description;
    @Field("time")
    private Long time;
    @Field("cost")
    private double cost;
    @Field("fromClient")
    private UUID fromClientId;
    @Field("toVendeur")
    private UUID toVendeurId;

    public Order() {
        this.id = UUID.randomUUID();
    }

    public Order(UUID id, String description, Long time, double cost, UUID fromClientId, UUID toVendeurId) {
        this.id = id;
        this.description = description;
        this.time = time;
        this.cost = cost;
        this.fromClientId = fromClientId;
        this.toVendeurId = toVendeurId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public UUID getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(UUID fromClientId) {
        this.fromClientId = fromClientId;
    }

    public UUID getToVendeurId() {
        return toVendeurId;
    }

    public void setToVendeurId(UUID toVendeurId) {
        this.toVendeurId = toVendeurId;
    }

}
