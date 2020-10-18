/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.api;

import cnam.liban.jalal9506f.MagasinEnLigne.database.VendeurRepository;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleVendeurResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.MultipleVendeurResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleVendeurResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.Vendeur;
import cnam.liban.jalal9506f.MagasinEnLigne.models.VendeurResponse;
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
    private VendeurRepository vendeurRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveVendeur(@RequestParam Map<String, String> map) throws Exception {
        if (map == null) {
            return new SingleVendeurResponse("Fail", "Missing fullName, password, phoneNumber fields",null).toJson();
        }
        Vendeur newVendeur = new Vendeur();
        if (map.get("fullName") == null) {
            return new SingleVendeurResponse("Fail","fullName is required",null).toJson();
        } else {
            newVendeur.setFullName(map.get("fullName"));
        }
        if (map.get("password") == null) {
            return new SingleVendeurResponse("Fail", "password is required",null).toJson();
        } else {
            newVendeur.setPassword(map.get("password"));
        }
        if (map.get("phoneNumber") == null) {
            return new SingleVendeurResponse("Fail", "phoneNumber is required",null).toJson();
        } else {
            newVendeur.setPhoneNumber(map.get("phoneNumber"));
        }
        vendeurRepository.save(newVendeur);
        VendeurResponse response = new SingleVendeurResponse("Success", "Vendeur created successfully", newVendeur);
        return response.toJson();
    }

    @RequestMapping(value = "/getVendeurs", method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getVendeurs() {
        List<Vendeur> list = vendeurRepository.findAll();
        VendeurResponse response = new MultipleVendeurResponse("Success", "vendeurs list", list);
        return response.toJson();
    }

    @RequestMapping(value = "/findVendeur", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findVendeur(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleVendeurResponse("Fail", "Missing parameters 'name'", null).toJson();
        }
        if (paramMap.get("name") != null) {
            String name = paramMap.get("name");
            List<Vendeur> all = vendeurRepository.findAll();
            List<Vendeur> result = new ArrayList<>();
            all.stream().filter(c -> (c.getFullName().startsWith(name))).forEachOrdered(c -> {
                result.add(c);
            });
            VendeurResponse response = new MultipleVendeurResponse("Success", result.size() + " Vendeurs found", result);
            return response.toJson();
        } else {
            return new SingleVendeurResponse("Fail", "Missing parameters 'name'", null).toJson();
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> login(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleVendeurResponse("Fail", "Missing parameters 'phoneNumber' and 'password'", null).toJson();
        }
        if (paramMap.get("phoneNumber") != null) {
            String phoneNumber = paramMap.get("phoneNumber");
            Vendeur vendeur = vendeurRepository.findVendeurByPhoneNumber(phoneNumber);
            if (vendeur != null) {
                if (paramMap.get("password") != null) {
                    String password = paramMap.get("password");
                    if (password.equals(vendeur.getPassword())) {
                        // password matches.. login accepted
                        return new SingleVendeurResponse("Success", "Login accepted", vendeur).toJson();
                    } else {
                        // password does not match.. login refused
                        return new SingleVendeurResponse("Fail", "Login failed: Password does not match", null).toJson();
                    }
                } else {
                    return new SingleVendeurResponse("Fail", "Missing parameters 'password'", null).toJson();
                }
            } else {
                return new SingleVendeurResponse("Fail", "Login failed: No Vendeur found", null).toJson();
            }
        } else {
            return new SingleVendeurResponse("Fail", "Missing parameters 'phoneNumber'", null).toJson();
        }

    }

    @RequestMapping(value = "/checkIn", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> checkIn(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleVendeurResponse("Fail", "Missing parameters 'id'", null).toJson();
        }
        if (paramMap.get("id") != null) {
            String id = paramMap.get("id");
            Vendeur vendeur = vendeurRepository.findVendeurById(UUID.fromString(id));
            if (vendeur != null) {
                return new SingleVendeurResponse("Success", "Vendeur found", vendeur).toJson();
            } else {
                return new SingleVendeurResponse("Fail", "Vendeur with provided ID does not exist",null).toJson();
            }
        } else {
            return new SingleVendeurResponse("Fail", "Missing parameters 'id'", null).toJson();
        }

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> delete(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleVendeurResponse("Fail", "Missing parameters 'id'", null).toJson();
        }
        if (paramMap.get("id") != null) {
            Vendeur vendeur = vendeurRepository.findVendeurById(UUID.fromString(paramMap.get("id")));
            if (vendeur != null) {
                vendeurRepository.delete(vendeur);
                return new SingleVendeurResponse("Success", "Deleted successfully", null).toJson();
            }
            return new SingleVendeurResponse("Fail", "Vendeur with provided ID does not exist",null).toJson();
        } else {
            return new SingleVendeurResponse("Fail", "Missing parameters 'id'", null).toJson();
        }

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> update(@RequestParam Map<String, String> paramMap) throws Exception {

        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Vendeur oldVendeur = vendeurRepository.findVendeurById((UUID.fromString(paramMap.get("id"))));
                if (oldVendeur == null) {
                    return new SingleVendeurResponse("Fail","Vendeur with provided ID does not exist",null).toJson();
                }
                if (paramMap.get("fullName") != null) {
                    oldVendeur.setFullName(paramMap.get("fullName"));
                }
                if (paramMap.get("password") != null) {
                    oldVendeur.setPassword(paramMap.get("password"));
                }
                if (paramMap.get("phoneNumber") != null) {
                    oldVendeur.setPhoneNumber(paramMap.get("phoneNumber"));
                }
                if (paramMap.get("email") != null) {
                    oldVendeur.setEmail(paramMap.get("email"));
                }
                vendeurRepository.save(oldVendeur);
                return new SingleVendeurResponse("Success", "Updated successfully", oldVendeur).toJson();

            } else {
                return new SingleVendeurResponse("Fail", "Missing id field",null).toJson();
            }

        } else {
            return new SingleVendeurResponse("Fail", "Missing id, fullName, password, phoneNumber fields",null).toJson();
        }
    }
}
