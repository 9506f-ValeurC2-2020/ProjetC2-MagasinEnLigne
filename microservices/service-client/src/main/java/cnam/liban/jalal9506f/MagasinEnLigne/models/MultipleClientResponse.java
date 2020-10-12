/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.models;

import java.util.Arrays;
import java.util.List;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author jalal
 */
public class MultipleClientResponse extends ClientResponse {

    private List<Client> response;

    public MultipleClientResponse(String status, String message, List<Client> response) {
        super(status, message);
        this.response = response;
    }

    public List<Client> getResponse() {
        return response;
    }

    public void setResponse(List<Client> response) {
        this.response = response;
    }

    @Override
    public ResponseEntity<Object> toJson(int status) {
        HttpStatus httpStatus;
        if (status == 0) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            httpStatus = HttpStatus.OK;
        }
        JSONObject jResponse = new JSONObject();
        jResponse.put("Status", getStatus());
        jResponse.put("Message", getMessage());
        jResponse.put("Response", getResponse());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return new ResponseEntity<>(jResponse, httpHeaders, httpStatus);
    }

}
