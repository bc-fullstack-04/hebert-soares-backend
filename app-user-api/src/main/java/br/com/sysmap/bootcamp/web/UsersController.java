package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.services.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Users API")
@Order(2)
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "Save user")
    @PostMapping("/create")
    public ResponseEntity<Users> save (@RequestBody Users users){
        return ResponseEntity.ok(this.usersService.save(users));
    }

    @Operation(summary = "Auth user")
    @PostMapping("/auth")
    public ResponseEntity<AuthDto> auth (@RequestBody AuthDto user){
        return ResponseEntity.ok(this.usersService.auth(user));
    }

    @Operation(summary = "List users")
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(){
        return ResponseEntity.ok( this.usersService.getAllUsers());
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Users>> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(this.usersService.getUserById(id));
    }

    @Operation(summary = "Update user")
    @PutMapping("/update/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users users){
        Users updatedUser = usersService.updateUser(id, users);
        return ResponseEntity.ok(updatedUser);
    }
}
