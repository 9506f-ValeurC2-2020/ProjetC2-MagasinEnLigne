/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.api;

import cnam.liban.jalal9506f.MagasinEnLigne.database.OrderRepository;
import cnam.liban.jalal9506f.MagasinEnLigne.models.CommonResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.MultipleOrderResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.Order;
import cnam.liban.jalal9506f.MagasinEnLigne.models.OrderResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleOrderResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jalal
 */
@RestController
public class ApiController {

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveOrder(@RequestParam Map<String, String> map) throws Exception {
        if (map == null) {
            return new CommonResponse("Fail", "Missing fields", "Missing description, time, cost, fromClient and toVendeur fields").toJson();
        }
        Order newOrder = new Order();
        if (map.get("description") == null) {
            return new CommonResponse("Fail", "Missing field description", "description is required").toJson();
        } else {
            newOrder.setDescription(map.get("description"));
        }
        if (map.get("time") == null) {
            return new CommonResponse("Fail", "Missing field time", "time is required").toJson();
        } else {
            newOrder.setTime(Long.parseLong(map.get("time")));
        }
        if (map.get("cost") == null) {
            return new CommonResponse("Fail", "Missing field cost", "cost is required").toJson();
        } else {
            newOrder.setCost(Double.parseDouble(map.get("cost")));
        }
        if (map.get("fromClient") == null) {
            return new CommonResponse("Fail", "Missing field fromClient", "fromClient is required").toJson();
        } else {
            newOrder.setFromClientId(UUID.fromString(map.get("fromClient")));
        }
        if (map.get("toVendeur") == null) {
            return new CommonResponse("Fail", "Missing field toVendeur", "toVendeur is required").toJson();
        } else {
            newOrder.setToVendeurId(UUID.fromString(map.get("toVendeur")));
        }

        orderRepository.save(newOrder);
        OrderResponse response = new SingleOrderResponse("Success", "Order created successfully", newOrder);
        return response.toJson();
    }

    @RequestMapping(value = "/getOrders", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getOrders(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'pageIndex'", "").toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Order> pagedResult = orderRepository.findAll(paging);
            List<Order> page = pagedResult.toList();
            OrderResponse response = new MultipleOrderResponse("Success", "Orders list", page);
            return response.toJson();
        }
        return new CommonResponse("Fail", "Missing value of 'pageIndex'", "").toJson();
    }

    @RequestMapping(value = "/findOrder", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findOrder(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson();
        }
        if (paramMap.get("id") != null) {
            UUID id = UUID.fromString(paramMap.get("id"));
            Order order = orderRepository.findOrderById(id);
            OrderResponse response = new SingleOrderResponse("Success", "Order found", order);
            return response.toJson();
        } else {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson();
        }

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> delete(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson();
        }
        if (paramMap.get("id") != null) {
            Order order = orderRepository.findOrderById(UUID.fromString(paramMap.get("id")));
            if (order != null) {
                orderRepository.delete(order);
                return new CommonResponse("Success", "Deleted successfully", "").toJson();
            }
            return new CommonResponse("Fail", "No Order", "Order with provided ID does not exist").toJson();
        } else {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson();
        }

    }

}
