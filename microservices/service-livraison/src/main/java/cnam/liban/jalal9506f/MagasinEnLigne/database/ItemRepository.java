/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.database;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import cnam.liban.jalal9506f.MagasinEnLigne.models.Item;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jalal
 */
@Repository
public interface ItemRepository extends MongoRepository<Item, String> {

    @Query("{'status':?0}")
    public List<Item> findItemByStatus(int status);

    @Query("{'id':?0}")
    public Item findItemById(UUID id);

}
