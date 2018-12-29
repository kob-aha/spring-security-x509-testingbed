package edu.ka.testingbed.security.x509.controller;

import edu.ka.testingbed.security.x509.entities.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private static final String DEFAULT_MESSAGE = "hello";
    //    @PreAuthorize("hasAuthority('ROLE_USER')")
//    @RequestMapping(value = "/user")
//    public String user(Model model, Principal principal) {
//        UserDetails currentUser = (UserDetails) ((Authentication) principal).getPrincipal();
//        model.addAttribute("username", currentUser.getUsername());
//        return "user";
//    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="user", defaultValue="dummy") String user) {
        return new Greeting(user, DEFAULT_MESSAGE);
    }


}
