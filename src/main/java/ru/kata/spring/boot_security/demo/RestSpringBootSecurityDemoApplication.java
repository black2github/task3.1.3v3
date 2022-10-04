package ru.kata.spring.boot_security.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.dao.DaoRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication
public class RestSpringBootSecurityDemoApplication {
	private static final Logger log = LoggerFactory.getLogger(RestSpringBootSecurityDemoApplication.class);

	@Autowired
	private UserService userService;

	@Autowired
	private DaoRepository<Role, Long> roleDaoRepository;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@PostConstruct
	public void init() {

		Role role1 = new Role("USER");
		roleDaoRepository.save(role1);
		Role role2 = new Role("ADMIN");
		roleDaoRepository.save(role2);

		User admin = new User("admin@a.b", bCryptPasswordEncoder.encode("admin"));
		admin.setFirstName("adminFirstName");
		admin.setLastName("adminLastName");
		admin.setAge(99);
		admin.setRoles(new LinkedHashSet<Role>(Arrays.asList(role1, role2)));

		userService.create(admin);

		User user = new User("user@a.b", bCryptPasswordEncoder.encode("user"));
		user.setFirstName("userFirstName");
		user.setLastName("userLastName");
		user.setAge(112);
		Role role3 = roleDaoRepository.findByName("USER").orElse(null);
		user.getRoles().add(role3);

		userService.create(user);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(RestSpringBootSecurityDemoApplication.class, args);
	}

}
