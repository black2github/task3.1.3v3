package ru.kata.spring.boot_security.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
    GET /users/ - заполнение данных данных о конкретном пользователе для просмотра
     */
    @GetMapping()
    public String show(ModelMap model, Principal principal) {
        log.debug("show: <- name=" + principal.getName());

        // получение одного пользователя по id и передача на отображение
        User user = userService.findUserByEmail(principal.getName());

        List<User> users = new ArrayList<>(1);
        users.add(user);
        model.addAttribute("users", users);

        log.debug("show: -> " + user);
        return "user/user_panel";
    }

    /*
     GET /users/:id/edit - заполнение объекта данными
     и отправка на форму редактирование данных пользователя
     */
    // @GetMapping("/edit")
    // public String edit(Model model, Principal principal) {
    //     log.debug("edit: <- name=" + principal.getName());
    //     User user = userService.findUserByEmail(principal.getName());
    //     model.addAttribute("user", user);
    //     log.debug("edit: -> "+ user);
    //     return "user/edit";
    // }

    /*
     PATCH /user/:id - обновление данных пользователя c конкретным id
     */
    // @PatchMapping("/")
    // public String update(@ModelAttribute("user") User user, Principal principal) {
    //     log.debug("update: <- name=" + principal.getName());
    //     User org = userService.findUserByEmail(principal.getName());
    //     userService.update(org.getId(), user);
    //     log.debug("update: ->");
    //     return "redirect:/user";
    // }
}
