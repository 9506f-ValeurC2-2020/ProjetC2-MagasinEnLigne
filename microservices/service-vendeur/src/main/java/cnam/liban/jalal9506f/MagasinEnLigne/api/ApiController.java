/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.api;

import cnam.liban.jalal9506f.MagasinEnLigne.database.VendeurRepository;
import cnam.liban.jalal9506f.MagasinEnLigne.models.CommonResponse;
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
            return new CommonResponse("Fail", "Missing fields", "Missing firstName, lastName, phoneNumber and address fields").toJson();
        }
        Vendeur newVendeur = new Vendeur();
        if (map.get("firstName") == null) {
            return new CommonResponse("Fail", "Missing field firstName", "firstName is required").toJson();
        } else {
            newVendeur.setFirstName(map.get("firstName"));
        }
        if (map.get("lastName") == null) {
            return new CommonResponse("Fail", "Missing field lastName", "lastName is required").toJson();
        } else {
            newVendeur.setLastName(map.get("lastName"));
        }
        if (map.get("phoneNumber") == null) {
            return new CommonResponse("Fail", "Missing field phoneNumber", "phoneNumber is required").toJson();
        } else {
            newVendeur.setPhoneNumber(map.get("phoneNumber"));
        }
        if (map.get("address") == null) {
            return new CommonResponse("Fail", "Missing field address", "address is required").toJson();
        } else {
            newVendeur.setAddress(map.get("address"));
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
            return new CommonResponse("Fail", "Missing parameters 'name'", "").toJson();
        }
        if (paramMap.get("name") != null) {
            String name = paramMap.get("name");
            List<Vendeur> all = vendeurRepository.findAll();
            List<Vendeur> result = new ArrayList<>();
            all.stream().filter(c -> (c.getFirstName().startsWith(name))).forEachOrdered(c -> {
                result.add(c);
            });
            VendeurResponse response = new MultipleVendeurResponse("Success", result.size() + " Venderus found", result);
            return response.toJson();
        } else {
            return new CommonResponse("Fail", "Missing parameters 'name'", "").toJson();
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
            Vendeur vendeur = vendeurRepository.findVendeurById(UUID.fromString(paramMap.get("id")));
            if (vendeur != null) {
                vendeurRepository.delete(vendeur);
                return new CommonResponse("Success", "Deleted successfully", "").toJson();
            }
            return new CommonResponse("Fail", "No vendeur", "Vendeur with provided ID does not exist").toJson();
        } else {
            return new CommonResponse("Fail", "Missing parameters 'id'", "").toJson();
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
                    return new CommonResponse("Fail", "No such vendeur", "Vendeur with provided ID does not exist").toJson();
                }
                if (paramMap.get("firstName") != null) {
                    oldVendeur.setFirstName(paramMap.get("firstName"));
                }
                if (paramMap.get("lastName") != null) {
                    oldVendeur.setLastName(paramMap.get("lastName"));
                }
                if (paramMap.get("phoneNumber") != null) {
                    oldVendeur.setPhoneNumber(paramMap.get("phoneNumber"));
                }
                if (paramMap.get("address") != null) {
                    oldVendeur.setAddress(paramMap.get("address"));
                }
                vendeurRepository.save(oldVendeur);
                return new SingleVendeurResponse("Success", "Updated successfully", oldVendeur).toJson();

            } else {
                return new CommonResponse("Fail", "Missing fields", "Missing ID field").toJson();
            }

        } else {
            return new CommonResponse("Fail", "Missing fields", "Missing id, firstName, lastName, phoneNumber and address fields").toJson();
        }
    }
}
