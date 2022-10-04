package ru.kata.spring.boot_security.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.ApplicationException;
import ru.kata.spring.boot_security.demo.NotFoundApplicationException;
import ru.kata.spring.boot_security.demo.configs.util.UserValidator;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;


@RequestMapping("/rest")
@RestController
// @CrossOrigin // позже для поддержки безопасности
public class RestApiController {
    private static final Logger log = LoggerFactory.getLogger(RestApiController.class);

    private final UserService userService;
    private UserValidator userValidator;

    // кастомизированная JSON сериализация для User
    private final static ObjectMapper objectMapper = new ObjectMapper();
    static {
        // SimpleModule module =
        //         new SimpleModule("CustomUserSerializer", new Version(1, 0, 0, null, null, null));
        // module.addSerializer(User.class, new CustomUserSerializer());
        // objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }
    private final static ObjectWriter objectWriter = objectMapper.writer().with(SerializationFeature.INDENT_OUTPUT);

    @Autowired
    public RestApiController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    /**
     * GET / - получение списка всех пользователей
     */
    @GetMapping()
    @ResponseBody
    public ResponseEntity<String> list() {
        log.debug("list: <- ");

        List<User> users = userService.listAll();

        HttpHeaders headers = makeHttpHeader();
        try {
            String jsonList = objectWriter.writeValueAsString(users);
            log.trace("list: -> " + jsonList);
            return new ResponseEntity<String>(jsonList, headers, HttpStatus.OK);
        } catch (Exception e) {
            return formErrorEntity("list", e);
        }
    }

    /**
     * GET /:id - получение данных о конкретном пользователе
     */
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<String> show(@PathVariable("id") long id) {
        log.debug("show: <- id=" + id);

        HttpHeaders headers = makeHttpHeader();
        try {
            User user = userService.find(id);
            if (user == null) {
                throw new NotFoundApplicationException(String.format("User with id=%d not found", id));
            }
            log.debug("show: user=" + user);
            String jsonUser = objectWriter.writeValueAsString(user);
            log.trace("show: -> " + jsonUser);
            return new ResponseEntity<String>(jsonUser, headers, HttpStatus.OK);

        } catch (Exception e) {
            return formErrorEntity("show", e);
        }
    }

    /**
     *   POST /user -обработка данных из JSON:
     * - создание пользователя по данным из запроса
     */
    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<String> createUser(@RequestBody String json) {
        log.debug("createUser: <- " + json);

        HttpHeaders headers = makeHttpHeader();
        try {
            User user = objectMapper.readValue(json, User.class);
            User u = userService.create(user);
            log.debug("createUser: -> " + u);
            String jsonUser = objectWriter.writeValueAsString(user);
            log.trace("createUser: -> " + jsonUser);
            return new ResponseEntity<String>(jsonUser, headers, HttpStatus.OK);

        } catch (Exception e) {
            return formErrorEntity("createUser", e);
        }
    }

    /**
     * PATCH /juser - обновление данных пользователя c id из JSON объекта
     */
    @PatchMapping("/user")
    @ResponseBody
    public ResponseEntity<String> updateUser(@RequestBody String json) {
        log.debug(String.format("updateUser: <- %s", json));

        HttpHeaders headers = makeHttpHeader();
        try {
            User user = objectMapper.readValue(json, User.class);

            if (user.getId() == null) {
                throw new ApplicationException("Value user.id not found.");
            }
            long id = user.getId();
            User usr = userService.update(id, user);

            log.debug("updateUser: -> " + usr);
            String jsonUser = objectWriter.writeValueAsString(usr);
            log.trace("updateUser: -> " + jsonUser);
            return new ResponseEntity<String>(jsonUser, headers, HttpStatus.OK);

        } catch (Exception e) {
            return formErrorEntity("updateUser", e);
        }
    }

    /**
     * DELETE /user - удаление пользователя c id
     */
    @DeleteMapping("/user")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@RequestParam(value = "id") Long id) {
        log.debug(String.format("deleteUser: <- id=%d", id));

        HttpHeaders headers = makeHttpHeader();
        try {
            userService.delete(id);
            log.debug("deleteUser: -> .");
            return new ResponseEntity<String>(null, headers, HttpStatus.OK);
        } catch (Exception e) {
            return formErrorEntity("deleteUser", e);
        }
    }

    private HttpHeaders makeHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }

    private ResponseEntity<String> formErrorEntity(String methodName, Exception e) {
        HttpHeaders headers = makeHttpHeader();
        String jError = String.format("{\"error\":\"%s\"}", e.getMessage());
        log.warn(methodName + ": error -> " + jError);
        if (e instanceof DataIntegrityViolationException || e instanceof ApplicationException) {
            log.warn(methodName + ": status code="+HttpStatus.BAD_REQUEST);
            return new ResponseEntity<String>(jError, headers, HttpStatus.BAD_REQUEST);
        } else if (e instanceof JsonProcessingException) {
            log.warn(methodName + ": status code="+HttpStatus.UNPROCESSABLE_ENTITY);
            return new ResponseEntity<String>(jError, headers, HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            log.trace(methodName + ":", e);
            log.error(methodName + ": status code="+HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<String>(jError, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

