/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.database;

import cnam.liban.jalal9506f.MagasinEnLigne.models.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jalal
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{'name':?0}")
    public List<Product> findProductByName(String name);

    @Query("{'id':?0}")
    public Product findProductById(UUID id);
}
