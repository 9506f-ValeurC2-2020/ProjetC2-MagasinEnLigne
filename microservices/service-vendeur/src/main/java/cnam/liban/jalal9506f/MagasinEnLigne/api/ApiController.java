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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
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
import org.springframework.web.multipart.MultipartFile;

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
            return new SingleVendeurResponse("Fail", "Missing fullName, password, phoneNumber fields", null).toJson();
        }
        Vendeur newVendeur = new Vendeur();
        if (map.get("fullName") == null) {
            return new SingleVendeurResponse("Fail", "fullName is required", null).toJson();
        } else {
            newVendeur.setFullName(map.get("fullName"));
        }
        if (map.get("password") == null) {
            return new SingleVendeurResponse("Fail", "password is required", null).toJson();
        } else {
            newVendeur.setPassword(map.get("password"));
        }
        if (map.get("phoneNumber") == null) {
            return new SingleVendeurResponse("Fail", "phoneNumber is required", null).toJson();
        } else {
            newVendeur.setPhoneNumber(map.get("phoneNumber"));
        }
        vendeurRepository.save(newVendeur);
        VendeurResponse response = new SingleVendeurResponse("Success", "Vendeur created successfully", newVendeur);
        return response.toJson();
    }

    @RequestMapping(value = "/getVendeurs", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getVendeurs(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleVendeurResponse("Fail", "Missing parameters 'pageIndex'", null).toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Vendeur> pagedResult = vendeurRepository.findAll(paging);
            List<Vendeur> page = pagedResult.toList();
            List<Vendeur> result = new ArrayList<>();
            page.stream().forEachOrdered(c -> {
                Vendeur p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                result.add(p);
            });
            VendeurResponse response = new MultipleVendeurResponse("Success", "Merchants list", result);
            return response.toJson();
        }
        return new SingleVendeurResponse("Fail", "Missing value of 'pageIndex'", null).toJson();
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
                Vendeur p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                result.add(p);
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
                if (vendeur.getImage() != null) {
                    vendeur.setImage(decompressImage(vendeur.getImage()));
                }
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
                if (vendeur.getImage() != null) {
                    vendeur.setImage(decompressImage(vendeur.getImage()));
                }
                return new SingleVendeurResponse("Success", "Vendeur found", vendeur).toJson();
            } else {
                return new SingleVendeurResponse("Fail", "Vendeur with provided ID does not exist", null).toJson();
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
            return new SingleVendeurResponse("Fail", "Vendeur with provided ID does not exist", null).toJson();
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
                    return new SingleVendeurResponse("Fail", "Vendeur with provided ID does not exist", null).toJson();
                }
                if (oldVendeur.getImage() != null) {
                    oldVendeur.setImage(decompressImage(oldVendeur.getImage()));
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
                return new SingleVendeurResponse("Fail", "Missing id field", null).toJson();
            }

        } else {
            return new SingleVendeurResponse("Fail", "Missing id, fullName, password, phoneNumber fields", null).toJson();
        }
    }

    @RequestMapping(value = "/updatePhoto", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updatePhoto(@RequestParam Map<String, String> paramMap, MultipartFile image) throws Exception {

        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Vendeur oldVendeur = vendeurRepository.findVendeurById((UUID.fromString(paramMap.get("id"))));
                if (oldVendeur == null) {
                    return new SingleVendeurResponse("Fail", "Vendeur with provided ID does not exist", null).toJson();
                }
                if (image != null) {
                    oldVendeur.setImage(compressImage(image.getBytes()));
                } else {
                    oldVendeur.setImage(null);
                }
                vendeurRepository.save(oldVendeur);
                if (oldVendeur.getImage() != null) {
                    oldVendeur.setImage(decompressImage(oldVendeur.getImage()));
                }
                return new SingleVendeurResponse("Success", "Updated successfully", oldVendeur).toJson();

            } else {
                return new SingleVendeurResponse("Fail", "Missing fields: id", null).toJson();
            }

        } else {
            return new SingleVendeurResponse("Fail", "Missing id, fullName, password, phoneNumber and address fields", null).toJson();
        }
    }

    // compress the image bytes before storing it in the database
    public static byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Exception error is: " + e.getMessage());
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    // uncompress the image bytes before returning it to the application
    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ioe) {
            System.out.println("Exception with error: " + ioe.getMessage());
        }
        return outputStream.toByteArray();
    }
}
