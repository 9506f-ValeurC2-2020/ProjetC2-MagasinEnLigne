/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.models;

import java.util.Arrays;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jalal
 */
public class SingleItemResponse extends ItemResponse {
    private Item response;

    public SingleItemResponse(String status,String message,Item response) {
        super(status,message);
        this.response = response;
    }

    public Item getResponse() {
        return response;
    }

    public void setResponse(Item response) {
        this.response = response;
    }

    
    @Override
    public ResponseEntity<Object> toJson() {
        JSONObject jResponse = new JSONObject();
        jResponse.put("Status", getStatus());
        jResponse.put("Message", getMessage());
        jResponse.put("Response", getResponse());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return new ResponseEntity<>(jResponse, httpHeaders, HttpStatus.OK);
    }
}
