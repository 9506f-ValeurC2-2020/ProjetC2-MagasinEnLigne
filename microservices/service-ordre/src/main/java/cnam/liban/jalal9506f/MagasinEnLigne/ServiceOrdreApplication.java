package cnam.liban.jalal9506f.MagasinEnLigne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceOrdreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrdreApplication.class, args);
    }

}
