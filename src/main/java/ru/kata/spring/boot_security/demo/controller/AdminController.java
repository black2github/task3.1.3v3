package ru.kata.spring.boot_security.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.configs.util.UserValidator;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private UserValidator userValidator;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    /**
     * GET /admin/ - получение списка всех пользователей
     */
    @GetMapping()
    public String list(ModelMap model) {
        log.debug("list: <- ");
        List<User> users = userService.listAll();
        model.addAttribute("users", users);
        log.debug("list: -> " + users);
        return "admin/admin_panel";
    }

    /**
     *   POST /users/ - обработка данных с формы:
     * - создание пользователя по объекту, заполненному на форме
     * - перенаправление на начальну страницу вывода списка
     */
    @PostMapping("/user")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             // @RequestParam(value = "roles", required = false) String[] roles,
                             BindingResult bindingResult) {
        log.debug("createUser: <- " + user);

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("createUser: can't create. " + bindingResult.toString());
            return "redirect:/admin";
        }
        User u = userService.create(user);
        log.debug("createUser: -> " + u);
        return "redirect:/admin";
    }

    /**
     * PATCH /user - обновление данных пользователя c id из объекта
     */
    @PatchMapping("/user")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "id") Long id,
                             BindingResult bindingResult) {
        log.debug(String.format("updateUser: <- user=%s, id=%d", user, id));

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("updateUser: can't update. " + bindingResult.toString());
        } else {
            userService.update(id, user);
        }
        log.debug("updateUser: ->");
        return "redirect:/admin";
    }

    /**
     * DELETE /user - обновление данных пользователя c id из объекта
     */
    @DeleteMapping("/user")
    public String deleteUser(@RequestParam(value = "id") Long id) {
        log.debug(String.format("deleteUser: <- id=%d", id));
        userService.delete(id);
        log.debug("deleteUser: ->");
        return "redirect:/admin";
    }
}
