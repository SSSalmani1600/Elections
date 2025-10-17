package nl.hva.election_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ElectionBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElectionBackendApplication.class, args);
	}
}
