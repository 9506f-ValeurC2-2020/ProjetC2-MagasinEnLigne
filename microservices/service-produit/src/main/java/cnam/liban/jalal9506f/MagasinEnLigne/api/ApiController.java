/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.api;

import cnam.liban.jalal9506f.MagasinEnLigne.database.ProductRepository;
import cnam.liban.jalal9506f.MagasinEnLigne.models.MultipleProductResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.Product;
import cnam.liban.jalal9506f.MagasinEnLigne.models.ProductResponse;
import cnam.liban.jalal9506f.MagasinEnLigne.models.SingleProductResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
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
    private ProductRepository productRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> saveProduct(@RequestParam Map<String, String> map, MultipartFile image) throws Exception {
        if (map == null) {
            return new SingleProductResponse("Fail", "Missing name, category, sex, price and ageCategory fields", null).toJson();
        }
        Product newProduct = new Product();
        if (map.get("name") == null) {
            return new SingleProductResponse("Fail", "name is required", null).toJson();
        } else {
            newProduct.setName(map.get("name"));
        }
        if (map.get("category") == null) {
            return new SingleProductResponse("Fail", "category is required", null).toJson();
        } else {
            newProduct.setCategory(map.get("category"));
        }
        String cat = map.get("category");
        if (cat.equals("clothing")) {
            if (map.get("sex") == null) {
                return new SingleProductResponse("Fail", "sex is required", null).toJson();
            } else {
                newProduct.setSex(Integer.parseInt(map.get("sex")));
            }
            if (map.get("ageCategory") == null) {
                return new SingleProductResponse("Fail", "ageCategory is required", null).toJson();
            } else {
                newProduct.setAgeCategory(map.get("ageCategory"));
            }
        }
        if (map.get("price") == null) {
            return new SingleProductResponse("Fail", "price is required", null).toJson();
        } else {
            newProduct.setPrice(Double.parseDouble(map.get("price")));
        }
        if (map.get("providedBy") == null) {
            return new SingleProductResponse("Fail", "providedBy is required", null).toJson();
        } else {
            newProduct.setProvidedBy(UUID.fromString(map.get("providedBy")));
        }

        if (map.get("onSale") != null) {
            newProduct.setOnSale(Boolean.parseBoolean(map.get("onSale")));
        }
        if (map.get("salePrice") != null) {
            newProduct.setSalePrice(Double.parseDouble(map.get("onSale")));
        }
        if (image != null) {
            newProduct.setImage(compressImage(image.getBytes()));
        }
        productRepository.save(newProduct);
        ProductResponse response = new SingleProductResponse("Success", "Product created successfully", newProduct);
        return response.toJson();
    }

    @RequestMapping(value = "/getProducts", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getProducts(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleProductResponse("Fail", "Missing parameters 'pageIndex'", null).toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Product> pagedResult = productRepository.findAll(paging);
            List<Product> page = pagedResult.toList();
            List<Product> result = new ArrayList<>();
            page.stream().forEachOrdered(c -> {
                Product p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                result.add(p);
            });
            ProductResponse response = new MultipleProductResponse("Success", "Products list", result);
            return response.toJson();
        }
        return new SingleProductResponse("Fail", "Missing value of 'pageIndex'", null).toJson();
    }

    @RequestMapping(value = "/findProduct", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findProduct(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleProductResponse("Fail", "Missing parameters 'name'", null).toJson();
        }
        if (paramMap.get("name") != null) {
            String name = paramMap.get("name");
            List<Product> all = productRepository.findAll();
            List<Product> result = new ArrayList<>();
            all.stream().filter(c -> (c.getName().startsWith(name))).forEachOrdered(c -> {
                Product p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                result.add(p);
            });
            ProductResponse response = new MultipleProductResponse("Success", result.size() + " Products found", result);
            return response.toJson();
        } else {
            return new SingleProductResponse("Fail", "Missing parameters 'name'", null).toJson();
        }

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> delete(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleProductResponse("Fail", "Missing parameters 'id'", null).toJson();
        }
        if (paramMap.get("id") != null) {
            Product product = productRepository.findProductById(UUID.fromString(paramMap.get("id")));
            if (product != null) {
                productRepository.delete(product);
                return new SingleProductResponse("Success", "Deleted successfully", null).toJson();
            }
            return new SingleProductResponse("Fail", "Product with provided ID does not exist", null).toJson();
        } else {
            return new SingleProductResponse("Fail", "Missing parameters 'id'", null).toJson();
        }

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> update(@RequestParam Map<String, RequestBody> paramMap, MultipartFile image) throws Exception {

        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Product oldProduct = productRepository.findProductById((UUID.fromString(paramMap.get("id").toString())));
                if (oldProduct == null) {
                    return new SingleProductResponse("Fail", "Product with provided ID does not exist", null).toJson();
                }
                if (paramMap.get("name") != null) {
                    oldProduct.setName(paramMap.get("name").toString());
                }
                if (paramMap.get("category") != null) {
                    oldProduct.setCategory(paramMap.get("category").toString());
                }
                if (paramMap.get("sex") != null) {
                    oldProduct.setSex(Integer.parseInt(paramMap.get("sex").toString()));
                }
                if (paramMap.get("price") != null) {
                    oldProduct.setPrice(Double.parseDouble(paramMap.get("price").toString()));
                }
                if (paramMap.get("ageCategory") != null) {
                    oldProduct.setAgeCategory(paramMap.get("ageCategory").toString());
                }
                if (image != null) {
                    oldProduct.setImage(compressImage(image.getBytes()));
                }
                productRepository.save(oldProduct);
                return new SingleProductResponse("Success", "Updated successfully", oldProduct).toJson();

            } else {
                return new SingleProductResponse("Fail", "Missing id field", null).toJson();
            }

        } else {
            return new SingleProductResponse("Fail", "Missing id, name, category, sex, price and ageCategory fields", null).toJson();
        }
    }

    @RequestMapping(value = "/putOnSale", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> putOnSale(@RequestParam Map<String, String> paramMap) throws Exception {
        if (paramMap != null) {
            if (paramMap.get("id") != null) {
                Product oldProduct = productRepository.findProductById((UUID.fromString(paramMap.get("id"))));
                if (oldProduct == null) {
                    return new SingleProductResponse("Fail", "Product with provided id does not exist", null).toJson();
                }
                if (paramMap.get("salePrice") != null) {
                    double salePrice = Double.parseDouble(paramMap.get("salePrice"));
                    if (salePrice >= oldProduct.getPrice()) {
                        return new SingleProductResponse("Fail", "Sale price must be less than old price", null).toJson();
                    }
                    oldProduct.setOnSale(true);
                    oldProduct.setSalePrice(salePrice);
                    productRepository.save(oldProduct);
                    return new SingleProductResponse("Success", "Product is now on sale", oldProduct).toJson();
                } else {
                    return new SingleProductResponse("Fail", "Missing salePrice field", null).toJson();
                }

            } else {
                return new SingleProductResponse("Fail", "Missing id field", null).toJson();
            }
        } else {
            return new SingleProductResponse("Fail", "Missing id, oldPrice fields", null).toJson();
        }
    }

    @RequestMapping(value = "/findSales", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> findSales(@RequestParam Map<String, String> paramMap) throws Exception {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleProductResponse("Fail", "Missing parameters 'pageIndex'", null).toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Product> pagedResult = productRepository.findAll(paging);
            List<Product> page = pagedResult.toList();
            List<Product> result = new ArrayList<>();
            page.stream().forEachOrdered(c -> {
                Product p = c;
                if (c.getImage() != null) {
                    p.setImage(decompressImage(c.getImage()));
                }
                if (p.isOnSale()) {
                    result.add(p);
                }
            });
            ProductResponse response = new MultipleProductResponse("Success", "Products list", result);
            return response.toJson();
        }
        return new SingleProductResponse("Fail", "Missing value of 'pageIndex'", null).toJson();
    }

    @RequestMapping(value = "/getMerchantProducts", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getMerchantProducts(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleProductResponse("Fail", "Missing parameters 'pageIndex'", null).toJson();
        }
        if (paramMap.get("merchantId") == null) {
            return new SingleProductResponse("Fail", "Missing value of 'merchantId'", null).toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Product> pagedResult = productRepository.findAll(paging);
            List<Product> page = pagedResult.toList();
            List<Product> result = new ArrayList<>();
            page.stream().filter(o -> (o.getProvidedBy().equals(UUID.fromString(paramMap.get("merchantId"))))).forEachOrdered(o -> {
                Product p = o;
                if (o.getImage() != null) {
                    p.setImage(decompressImage(o.getImage()));
                }
                result.add(o);
            });
            ProductResponse response = new MultipleProductResponse("Success", "Products list", result);
            return response.toJson();
        }
        return new SingleProductResponse("Fail", "Missing value of 'pageIndex'", null).toJson();
    }

    @RequestMapping(value = "/getWishes", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getWishes(@RequestParam Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return new SingleProductResponse("Fail", "Missing parameters 'pageIndex'", null).toJson();
        }
        if (paramMap.get("wishes") == null) {
            return new SingleProductResponse("Fail", "Missing value of 'wishes'", null).toJson();
        }
        if (paramMap.get("pageIndex") != null) {
            int index = Integer.parseInt(paramMap.get("pageIndex"));
            Pageable paging = PageRequest.of(index, 20);
            Page<Product> pagedResult = productRepository.findAll(paging);
            List<Product> page = pagedResult.toList();
            List<Product> result = new ArrayList<>();
            String wishes = paramMap.get("wishes");
            var arrayOfWish = wishes.split(",");
            for (String s : arrayOfWish) {
                var product = productRepository.findProductById(UUID.fromString(s));
                if (product != null) {
                    if (product.getImage() != null) {
                        product.setImage(decompressImage(product.getImage()));
                    }
                    result.add(product);
                }
            }
            ProductResponse response = new MultipleProductResponse("Success", "Products list", result);
            return response.toJson();
        }
        return new SingleProductResponse("Fail", "Missing value of 'pageIndex'", null).toJson();
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
