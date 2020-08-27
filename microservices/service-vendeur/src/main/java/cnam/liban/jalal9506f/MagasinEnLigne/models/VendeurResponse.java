/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.models;

import org.springframework.http.ResponseEntity;

/**
 *
 * @author jalal
 */
public abstract class VendeurResponse {
    private String status;
    private String message;

    public VendeurResponse() {
    }

    public VendeurResponse(String status, String message) {
        this.status = status;
        this.message = message;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public abstract ResponseEntity<Object> toJson();
}
