/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnam.liban.jalal9506f.MagasinEnLigne.database;

import cnam.liban.jalal9506f.MagasinEnLigne.models.Vendeur;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 *
 * @author jalal
 */
@Service
@Repository
public interface VendeurRepository extends MongoRepository<Vendeur, String> {

    @Query("{'fullName':?0}")
    public List<Vendeur> findVendeurByFirstName(String name);

    @Query("{'phoneNumber':?0}")
    public Vendeur findVendeurByPhoneNumber(String name);

    @Query("{'id':?0}")
    public Vendeur findVendeurById(UUID id);
}
