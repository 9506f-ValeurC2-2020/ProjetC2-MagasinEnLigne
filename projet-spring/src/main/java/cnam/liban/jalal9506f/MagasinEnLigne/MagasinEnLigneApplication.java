package cnam.liban.jalal9506f.MagasinEnLigne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MagasinEnLigneApplication {

	public static void main(String[] args) {
		SpringApplication.run(MagasinEnLigneApplication.class, args);
	}

}
