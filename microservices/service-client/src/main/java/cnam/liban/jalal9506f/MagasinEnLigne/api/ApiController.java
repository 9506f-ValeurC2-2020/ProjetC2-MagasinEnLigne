/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.api;

import cnam.liban.jalal9506f.MagasinEnLigne.database.ClientRepository;
import java.util.List;
import cnam.liban.jalal9506f.MagasinEnLigne.models.Client;
import cnam.liban.jalal9506f.MagasinEnLigne.models.ClientResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.CommonResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.MultipleClientResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleClientResponse;
import java.util.ArrayList;
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
    private ClientRepository clientRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveClient(@RequestParam Map<String, String> map) throws Exception {
        if (map == null) {
            return new CommonResponse("Fail", "Missing fields", "Missing fullName, password, phoneNumber and address fields").toJson(0);
        }
        Client newClient = new Client();
        if (map.get("fullName") == null) {
            return new CommonResponse("Fail", "Missing field fullName", "fullName is required").toJson(0);
        } else {
            newClient.setFullName(map.get("fullName"));
        }
        if (map.get("password") == null) {
            return new CommonResponse("Fail", "Missing field password", "password is required").toJson(0);
        } else {
            newClient.setPassword(map.get("password"));
        }
        if (map.get("phoneNumber") == null) {
            return new CommonResponse("Fail", "Missing field phoneNumber", "phoneNumber is required").toJson(0);
        } else {
            newClient.setPhoneNumber(map.get("phoneNumber"));
        }
        if (map.get("address") == null) {
            return new CommonResponse("Fail", "Missing field address", "address is required").toJson(0);
        } else {
            newClient.setAddress(map.get("address"));
        }
        clientRepository.save(newClient);
        ClientResponse response = new SingleClientResponse("Success", "Client created successfully", newClient);
        return response.toJson(1);
    }

    @RequestMapping(value = "/getClients", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getClients() {
        List<Client> list = clientRepository.findAll();
        ClientResponse response = new MultipleClientResponse("Success", "Clients list", list);
        return response.toJson(1);
    }

    @RequestMapping(value = "/findClient", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findClient(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'name'", "").toJson(0);
        }
        if (paramMap.get("name") != null) {
            String name = paramMap.get("name");
            List<Client> all = clientRepository.findAll();
            List<Client> result = new ArrayList<>();
            all.stream().filter(c -> (c.getFullName().startsWith(name))).forEachOrdered(c -> {
                result.add(c);
            });
            ClientResponse response = new MultipleClientResponse("Success", result.size() + " Clients found", result);
            return response.toJson(1);
        } else {
            return new CommonResponse("Fail", "Missing parameters 'name'", "").toJson(0);
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> login(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'phoneNumber' and 'password'", "").toJson(0);
        }
        if (paramMap.get("phoneNumber") != null) {
            String phoneNumber = paramMap.get("phoneNumber");
            Client client = clientRepository.findClientByPhoneNumber(phoneNumber);
            if (client != null) {
                if (paramMap.get("password") != null) {
                    String password = paramMap.get("password");
                    if (password.equals(client.getPassword())) {
                        // password matches.. login accepted
                        return new SingleClientResponse("Success", "Login accepted", client).toJson(1);
                    } else {
                        // password does not match.. login refused
                        return new CommonResponse("Fail", "Login failed: Password does not match", "").toJson(0);
                    }
                } else {
                    return new CommonResponse("Fail", "Missing parameters 'password'", "").toJson(0);
                }
            } else {
                return new CommonResponse("Fail", "Login failed: No Client found", "").toJson(0);
            }
        } else {
            return new CommonResponse("Fail", "Missing parameters 'phoneNumber'", "").toJson(0);
        }

    }

    @RequestMapping(value = "/checkIn", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> checkIn(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson(0);
        }
        if (paramMap.get("id") != null) {
            String id = paramMap.get("id");
            Client client = clientRepository.findClientById(UUID.fromString(id));
            if (client != null) {
                return new SingleClientResponse("Success", "Client found", client).toJson(1);
            } else {
                return new CommonResponse("Fail", "No Client", "Wrong id").toJson(0);
            }
        } else {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson(0);
        }

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> delete(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson(0);
        }
        if (paramMap.get("id") != null) {
            Client client = clientRepository.findClientById(UUID.fromString(paramMap.get("id")));
            if (client != null) {
                clientRepository.delete(client);
                return new CommonResponse("Success", "Deleted successfully", "").toJson(1);
            }
            return new CommonResponse("Fail", "No client", "Client with provided ID does not exist").toJson(0);
        } else {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson(0);
        }

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> update(@RequestParam Map<String, String> paramMap) throws Exception {

        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Client oldClient = clientRepository.findClientById((UUID.fromString(paramMap.get("id"))));
                if (oldClient == null) {
                    return new CommonResponse("Fail", "No such client", "Client with provided ID does not exist").toJson(0);
                }
                if (paramMap.get("fullName") != null) {
                    oldClient.setFullName(paramMap.get("fullName"));
                }
                if (paramMap.get("password") != null) {
                    oldClient.setPassword(paramMap.get("password"));
                }
                if (paramMap.get("phoneNumber") != null) {
                    oldClient.setPhoneNumber(paramMap.get("phoneNumber"));
                }
                if (paramMap.get("email") != null) {
                    oldClient.setEmail(paramMap.get("email"));
                }
                if (paramMap.get("address") != null) {
                    oldClient.setAddress(paramMap.get("address"));
                }
                clientRepository.save(oldClient);
                return new SingleClientResponse("Success", "Updated successfully", oldClient).toJson(1);

            } else {
                return new CommonResponse("Fail", "Missing fields", "Missing ID field").toJson(0);
            }

        } else {
            return new CommonResponse("Fail", "Missing fields", "Missing id, fullName, password, phoneNumber and address fields").toJson(0);
        }
    }

}
