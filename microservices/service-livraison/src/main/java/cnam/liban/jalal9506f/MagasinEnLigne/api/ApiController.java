/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.api;

import cnam.liban.jalal9506f.MagasinEnLigne.database.ItemRepository;
import cnam.liban.jalal9506f.MagasinEnLigne.models.Item;
import cnam.liban.jalal9506f.MagasinEnLigne.models.ItemResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.MultipleItemResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleItemResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ItemRepository itemRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveItem(@RequestParam Map<String, String> map) throws Exception {
        if (map == null) {
            return new SingleItemResponse("Fail", "Missing field: 'orderId'", null).toJson();
        }
        Item newItem = new Item();
        if (map.get("orderId") == null) {
            return new SingleItemResponse("Fail", "Missing field: 'orderId'", null).toJson();
        } else {
            newItem.setOrderId(UUID.fromString(map.get("orderId")));
        }
        itemRepository.save(newItem);
        ItemResponse response = new SingleItemResponse("Success", "Item created successfully", newItem);
        return response.toJson();
    }

    @RequestMapping(value = "/getItems", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getItems() {
        List<Item> list = itemRepository.findAll();
        ItemResponse response = new MultipleItemResponse("Success", "Items list", list);
        return response.toJson();
    }

    @RequestMapping(value = "/findItem", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findItem(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleItemResponse("Fail", "Missing field: 'status'", null).toJson();
        }
        if (paramMap.get("status") != null) {
            int status = Integer.parseInt(paramMap.get("status"));
            List<Item> all = itemRepository.findAll();
            List<Item> result = new ArrayList<>();
            all.stream().filter(item -> (item.getStatus() == status)).forEachOrdered(item -> {
                result.add(item);
            });
            ItemResponse response = new MultipleItemResponse("Success", result.size() + " Items found", result);
            return response.toJson();
        } else {
            return new SingleItemResponse("Fail", "Missing field: 'status'", null).toJson();
        }

    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> update(@RequestParam Map<String, String> paramMap) throws Exception {

        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Item oldItem = itemRepository.findItemById((UUID.fromString(paramMap.get("id"))));
                if (oldItem == null) {
                    return new SingleItemResponse("Fail", "Item with provided ID does not exist", null).toJson();
                }
                oldItem.setStatus(1);
                itemRepository.save(oldItem);
                return new SingleItemResponse("Success", "Updated successfully", oldItem).toJson();

            } else {
                return new SingleItemResponse("Fail", "Missing field: 'id'", null).toJson();
            }

        } else {
            return new SingleItemResponse("Fail", "Missing field: 'id'", null).toJson();
        }
    }
}
