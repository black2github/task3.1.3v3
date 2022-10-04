package ru.kata.spring.boot_security.demo.configs.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.dao.DaoRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Component
public class UserValidator implements Validator {

    private final UserService userService;
     private DaoRepository<Role, Long> roleDaoRepository;

    @Autowired
    public UserValidator(UserService userService, DaoRepository<Role, Long> roleDaoRepository) {
        this.userService = userService;
        this.roleDaoRepository = roleDaoRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            // поле, код ошибки, сообщение ошибки
            errors.rejectValue("email", "", "Email can't be null or empty");
        }

        // Проверяем наличие email
        if (userService.findUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "", "Email already bound to someone");
        }

        if (user.getPassword().isEmpty() || user.getPassword().isBlank()) {
            errors.rejectValue("password", "", "Password can't be null or empty");
        }

        if (user.getAge() < 0 || user.getAge() > 150) {
            // поле, код ошибки, сообщение ошибки
            errors.rejectValue("age", "", "Age must be between 0 to 150");
        }

        if (!user.getRoles().isEmpty()) {
            // такие роли доступны ?
            for (Role role : user.getRoles()) {
                Role r = roleDaoRepository.findByName(role.getName()).orElse(null);
                if (r == null) {
                    errors.rejectValue("roles", "", String.format("Role %s is unknown", role.getName()));
                }
            }
        }
    }
}
