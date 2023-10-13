package com.example.Julio.TodoList.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private InterfaceUserRepository repository; 

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel model)
    {
        var user = this.repository.findByUsername(model.getUsername());
        
        if(user == null) {
            var passwordHashred = BCrypt.withDefaults().
                hashToString(12, model.getPassword().toCharArray());
                            
            model.setPassword(passwordHashred);
        
            var userCreated = this.repository.save(model);
            return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
    }
}
