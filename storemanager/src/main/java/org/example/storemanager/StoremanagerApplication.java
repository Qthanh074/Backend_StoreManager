package org.example.storemanager;

import org.example.storemanager.entity.system.Role;
import org.example.storemanager.entity.system.User;
import org.example.storemanager.repository.system.RoleRepository;
import org.example.storemanager.repository.system.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class StoremanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoremanagerApplication.class, args);
	}


}
