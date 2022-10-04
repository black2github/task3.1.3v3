package ru.kata.spring.boot_security.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.dao.DaoRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class ModelRepositoryTests {
	private static final Logger log = LoggerFactory.getLogger(ModelRepositoryTests.class);

	private static final int RANGE = 10000;
	private static Random r = new Random();

	@Autowired
	private DaoRepository<User, Long> userDaoRepository;

	@Autowired
	private DaoRepository<Role, Long> roleDaoRepository;

	@Autowired
	//@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("userService")
	private UserDetailsService userDetailsService;

	// @Test
	//   void contextLoads() {
	// }

	@Test
	void createRole() {
		log.debug("createRole: <-");

		Role role1 = new Role("USER" + r.nextInt(1000));
		roleDaoRepository.save(role1);
		Role role2 = new Role("ADMIN" + r.nextInt(1000));
		roleDaoRepository.save(role2);

		User user = new User("user"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		user.setRoles(new LinkedHashSet<Role>(Arrays.asList(role1, role2)));

		user = userDaoRepository.save(user);
		User user2 = userDaoRepository.findById(user.getId()).orElse(null);
		assertNotNull("User not found", user2);
		log.debug("createRole: user2="+user2);
		assertTrue("Role " + role1.getName() + " not found", user2.getRoles().contains(role1));
		assertTrue("Role " + role2.getName() + " not found", user2.getRoles().contains(role2));

		user.getRoles().clear();
		userService.update(user.getId(), user);
		user = userDaoRepository.findById(user.getId()).orElse(null);
		assertTrue("Role Set must be empty", user.getRoles().isEmpty());
		// clean
		userDaoRepository.delete(user);
		roleDaoRepository.delete(role1);
		roleDaoRepository.delete(role2);
	}

	@Test
	void createUser() {
		log.debug("createUser: <-");

		User user = new User("user@domen.ru"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		Role role = new Role("ROLE_USER" + r.nextInt(1000));
		user.getRoles().add(role);
		roleDaoRepository.save(role);
		user = userDaoRepository.save(user);
		assertNotNull("User must not be null.", user);

		User user2 = userDaoRepository.findById(user.getId()).orElse(null);
		assertEquals("Users are not equal.", user, user2);
		// clean
		userDaoRepository.delete(user);
		roleDaoRepository.delete(role);
	}

	@Test
	void deleteUser() {
		User user = new User("user@domen.del"+r.nextInt(RANGE), "user"+r.nextInt(RANGE));
		Role role = new Role("ROLE_USER" + r.nextInt(RANGE));
		user.getRoles().add(role);
		roleDaoRepository.save(role);
		user = userDaoRepository.save(user);
		user = userDaoRepository.findById(user.getId()).orElse(null);
		assertNotNull("deleteUser: Just created user not found", user);

		userDaoRepository.delete(user);
		roleDaoRepository.delete(role);
		user = userDaoRepository.findById(user.getId()).orElse(null);
		assertNull("delete User: Found user after delete", user);
	}

	@Test
	void updateUser() {
		User user = new User("name@domen.up"+r.nextInt(RANGE), "passwd"+r.nextInt(RANGE));
		Role role = new Role("ROLE_USER" + r.nextInt(RANGE));
		user.getRoles().add(role);
		roleDaoRepository.save(role);
		user = userDaoRepository.save(user);

		User user2 = new User("name@domen.qu"+r.nextInt(RANGE), "second"+r.nextInt(RANGE), 10, "fistNameX", "lastNameX");
		Role role2 = new Role("ROLE_ADMIN"+r.nextInt(RANGE));
		Role role3 = new Role("ROLE_ADMIN"+r.nextInt(RANGE));
		user2.getRoles().add(role2);
		user2.getRoles().add(role3);
		roleDaoRepository.save(role2);
		roleDaoRepository.save(role3);
		log.debug("user2="+user2);

		user = userService.update(user.getId(), user2);
		log.debug("user after update = "+user);

		User user3 = userDaoRepository.findById(user.getId()).orElse(null);
		log.debug("user3 = "+user3);
		log.debug("user3==user ? " + user3.equals(user));
		assertEquals("User not updated properly.", user3, user);

		// clean
		userDaoRepository.deleteById(user3.getId());
		roleDaoRepository.delete(role);
		roleDaoRepository.delete(role3);
		roleDaoRepository.delete(role2);
	}

	@Test
	void loadUser() {
		User user = new User("name"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		Role role = new Role("ROLE_USER"+r.nextInt(RANGE));
		user.getRoles().add(role);
		roleDaoRepository.save(role);
		user = userDaoRepository.save(user);

		UserDetails user2 = userDetailsService.loadUserByUsername(user.getEmail());
		assertEquals("usernames are not equal.", user, user2);

		// clean
		userDaoRepository.delete(user);
		roleDaoRepository.delete(role);
	}

	@Test
	void findUserByEmail() {
		User user = new User("name"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		user = userDaoRepository.save(user);
		User user2 = userDaoRepository.findByName(user.getEmail()).orElse(null);
		assertNotNull("User not found by email.", user2);
		assertEquals("Users must be the same.", user, user2);
	}
}
