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
import cnam.liban.jalal9506f.MagasinEnLigne.models.MultipleClientResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleClientResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private ClientRepository clientRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveClient(@RequestParam Map<String, String> map) throws Exception {
        if (map == null) {
            return new SingleClientResponse("Fail", "Missing fullName, password, phoneNumber and address fields", null).toJson();
        }
        Client newClient = new Client();
        if (map.get("fullName") == null) {
            return new SingleClientResponse("Fail", "Missing field fullName", null).toJson();
        } else {
            newClient.setFullName(map.get("fullName"));
        }
        if (map.get("password") == null) {
            return new SingleClientResponse("Fail", "Missing field password", null).toJson();
        } else {
            newClient.setPassword(map.get("password"));
        }
        if (map.get("address") == null) {
            return new SingleClientResponse("Fail", "Missing field address", null).toJson();
        } else {
            newClient.setAddress(map.get("address"));
        }
        if (map.get("phoneNumber") == null) {
            return new SingleClientResponse("Fail", "Missing field phoneNumber", null).toJson();
        } else {
            String phoneNumber = map.get("phoneNumber");
            Client client = clientRepository.findClientByPhoneNumber(phoneNumber);
            if (client != null) {
                client.setFullName(map.get("fullName"));
                client.setPassword(map.get("password"));
                client.setAddress(map.get("address"));
                clientRepository.save(client);
                ClientResponse response = new SingleClientResponse("Success", "Client created successfully", client);
                return response.toJson();
            }
            newClient.setPhoneNumber(phoneNumber);
        }

        clientRepository.save(newClient);
        ClientResponse response = new SingleClientResponse("Success", "Client created successfully", newClient);
        return response.toJson();
    }

    @RequestMapping(value = "/getClients", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getClients(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleClientResponse("Fail", "Missing parameters 'pageIndex'", null).toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Client> pagedResult = clientRepository.findAll(paging);
            List<Client> page = pagedResult.toList();
            List<Client> result = new ArrayList<>();
            page.stream().forEachOrdered(c -> {
                Client p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                result.add(p);
            });
            ClientResponse response = new MultipleClientResponse("Success", "Clients list", result);
            return response.toJson();
        }
        return new SingleClientResponse("Fail", "Missing value of 'pageIndex'", null).toJson();
    }

    @RequestMapping(value = "/findClient", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findClient(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleClientResponse("Fail", "Missing parameters 'name'", null).toJson();
        }
        if (paramMap.get("name") != null) {
            String name = paramMap.get("name");
            List<Client> all = clientRepository.findAll();
            List<Client> result = new ArrayList<>();
            all.stream().filter(c -> (c.getFullName().startsWith(name))).forEachOrdered(c -> {
                Client p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                result.add(p);
            });
            ClientResponse response = new MultipleClientResponse("Success", result.size() + " Clients found", result);
            return response.toJson();
        } else {
            return new SingleClientResponse("Fail", "Missing parameters 'name'", null).toJson();
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> login(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleClientResponse("Fail", "Missing parameters 'phoneNumber' and 'password'", null).toJson();
        }
        if (paramMap.get("phoneNumber") != null) {
            String phoneNumber = paramMap.get("phoneNumber");
            Client client = clientRepository.findClientByPhoneNumber(phoneNumber);
            if (client != null) {
                if (client.getImage() != null) {
                    client.setImage(decompressImage(client.getImage()));
                }
                if (paramMap.get("password") != null) {
                    String password = paramMap.get("password");
                    if (password.equals(client.getPassword())) {
                        // password matches.. login accepted
                        return new SingleClientResponse("Success", "Login accepted", client).toJson();
                    } else {
                        // password does not match.. login refused
                        return new SingleClientResponse("Fail", "Login failed Password does not match", null).toJson();
                    }
                } else {
                    return new SingleClientResponse("Fail", "Missing parameters 'password'", null).toJson();
                }
            } else {
                return new SingleClientResponse("Fail", "Login failed: No Client found", null).toJson();
            }
        } else {
            return new SingleClientResponse("Fail", "Missing parameters 'phoneNumber'", null).toJson();
        }

    }

    @RequestMapping(value = "/checkIn", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> checkIn(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleClientResponse("Fail", "Missing parameters 'id'", null).toJson();
        }
        if (paramMap.get("id") != null) {
            String id = paramMap.get("id");
            Client client = clientRepository.findClientById(UUID.fromString(id));
            if (client != null) {
                if (client.getImage() != null) {
                    client.setImage(decompressImage(client.getImage()));
                }
                return new SingleClientResponse("Success", "Client found", client).toJson();
            } else {
                return new SingleClientResponse("Fail", "No Client: Wrong id", null).toJson();
            }
        } else {
            return new SingleClientResponse("Fail", "Missing parameters 'id'", null).toJson();
        }

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> delete(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleClientResponse("Fail", "Missing parameters 'id'", null).toJson();
        }
        if (paramMap.get("id") != null) {
            Client client = clientRepository.findClientById(UUID.fromString(paramMap.get("id")));
            if (client != null) {
                clientRepository.delete(client);
                return new SingleClientResponse("Success", "Deleted successfully", null).toJson();
            }
            return new SingleClientResponse("Fail", "Client with provided ID does not exist", null).toJson();
        } else {
            return new SingleClientResponse("Fail", "Missing parameters 'id'", null).toJson();
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
                    return new SingleClientResponse("Fail", "Client with provided ID does not exist", null).toJson();
                }
                if (oldClient.getImage() != null) {
                    oldClient.setImage(decompressImage(oldClient.getImage()));
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
                return new SingleClientResponse("Success", "Updated successfully", oldClient).toJson();

            } else {
                return new SingleClientResponse("Fail", "Missing fields: id", null).toJson();
            }

        } else {
            return new SingleClientResponse("Fail", "Missing id, fullName, password, phoneNumber and address fields", null).toJson();
        }
    }

    @RequestMapping(value = "/updatePhoto", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updatePhoto(@RequestParam Map<String, String> paramMap, MultipartFile image) throws Exception {

        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Client oldClient = clientRepository.findClientById((UUID.fromString(paramMap.get("id"))));
                if (oldClient == null) {
                    return new SingleClientResponse("Fail", "Client with provided ID does not exist", null).toJson();
                }
                if (image != null) {
                    oldClient.setImage(compressImage(image.getBytes()));
                } else {
                    oldClient.setImage(null);
                }

                clientRepository.save(oldClient);
                if (oldClient.getImage() != null) {
                    oldClient.setImage(decompressImage(oldClient.getImage()));
                }
                return new SingleClientResponse("Success", "Updated successfully", oldClient).toJson();

            } else {
                return new SingleClientResponse("Fail", "Missing fields: id", null).toJson();
            }

        } else {
            return new SingleClientResponse("Fail", "Missing id, fullName, password, phoneNumber and address fields", null).toJson();
        }
    }

    @RequestMapping(value = "/addToWish", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addToWish(@RequestParam Map<String, String> paramMap) throws Exception {
        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Client oldClient = clientRepository.findClientById((UUID.fromString(paramMap.get("id"))));
                if (oldClient == null) {
                    return new SingleClientResponse("Fail", "Client with provided ID does not exist", null).toJson();
                }
                if (oldClient.getImage() != null) {
                    oldClient.setImage(decompressImage(oldClient.getImage()));
                }
                if (paramMap.get("wishId") != null) {
                    ArrayList wishes = oldClient.getWishList();
                    wishes.add(UUID.fromString(paramMap.get("wishId")));
                    oldClient.setWishList(wishes);
                } else {
                    return new SingleClientResponse("Fail", "wishId is required", null).toJson();
                }

                clientRepository.save(oldClient);
                return new SingleClientResponse("Success", "Updated successfully", oldClient).toJson();

            } else {
                return new SingleClientResponse("Fail", "Missing fields: id", null).toJson();
            }

        } else {
            return new SingleClientResponse("Fail", "Missing id, wishId fields", null).toJson();
        }
    }

    @RequestMapping(value = "/removeWish", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> removeWish(@RequestParam Map<String, String> paramMap) throws Exception {
        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Client oldClient = clientRepository.findClientById((UUID.fromString(paramMap.get("id"))));
                if (oldClient == null) {
                    return new SingleClientResponse("Fail", "Client with provided ID does not exist", null).toJson();
                }
                if (oldClient.getImage() != null) {
                    oldClient.setImage(decompressImage(oldClient.getImage()));
                }
                if (paramMap.get("wishId") != null) {
                    ArrayList wishes = oldClient.getWishList();
                    wishes.remove(UUID.fromString(paramMap.get("wishId")));
                    oldClient.setWishList(wishes);
                } else {
                    return new SingleClientResponse("Fail", "wishId is required", null).toJson();
                }

                clientRepository.save(oldClient);
                return new SingleClientResponse("Success", "Updated successfully", oldClient).toJson();

            } else {
                return new SingleClientResponse("Fail", "Missing fields: id", null).toJson();
            }

        } else {
            return new SingleClientResponse("Fail", "Missing id, wishId fields", null).toJson();
        }
    }

    @RequestMapping(value = "/findClientById", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findClientById(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleClientResponse("Fail", "Missing parameters 'name'", null).toJson();
        }
        if (paramMap.get("clientId") != null) {
            String id = paramMap.get("clientId");
            Client client = clientRepository.findClientById(UUID.fromString(id));
            if (client != null) {
                if (client.getImage() != null) {
                    client.setImage(decompressImage(client.getImage()));
                }
                ClientResponse response = new SingleClientResponse("Success", "Client found", client);
                return response.toJson();
            }
            ClientResponse response = new SingleClientResponse("Fail", "No Such id", null);
            return response.toJson();
        } else {
            return new SingleClientResponse("Fail", "Missing parameters 'name'", null).toJson();
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
