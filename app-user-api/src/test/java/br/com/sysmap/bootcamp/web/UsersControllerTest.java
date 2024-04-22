package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.services.UsersService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private UsersController usersController;

    @Nested
    @DisplayName("POST /users/create")
    class CreateUser {
        @Test
        @DisplayName("Should create user and return created user")
        void shouldCreateUserAndReturnCreatedUser() {
            Users newUser = Users.builder()
                    .email("test@test.com")
                    .password("password")
                    .build();
            Users savedUser = Users.builder()
                    .id(1L)
                    .email("test@test.com")
                    .password("encodedPassword")
                    .build();
            when(usersService.save(any(Users.class))).thenReturn(savedUser);

            ResponseEntity<Users> response = usersController.save(newUser);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(savedUser, response.getBody());
        }
    }

    @Nested
    @DisplayName("POST /users/auth")
    class AuthenticateUser {
        @Test
        @DisplayName("Should authenticate user and return token")
        void shouldAuthenticateUserAndReturnToken() {
            AuthDto authDto = AuthDto.builder()
                    .email("test@test.com")
                    .password("password")
                    .build();
            AuthDto expectedDto = AuthDto.builder()
                    .email("test@test.com")
                    .token("token")
                    .id(1L)
                    .build();
            when(usersService.auth(any(AuthDto.class))).thenReturn(expectedDto);

            ResponseEntity<AuthDto> response = usersController.auth(authDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedDto, response.getBody());
        }
    }

    @Nested
    @DisplayName("GET /users")
    class ListUsers {
        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            List<Users> usersList = List.of(
                    Users.builder().id(1L).email("user1@email.com").password("pass1").build(),
                    Users.builder().id(2L).email("user2@email.com").password("pass2").build()
            );
            when(usersService.getAllUsers()).thenReturn(usersList);

            ResponseEntity<List<Users>> response = usersController.getAllUsers();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(usersList, response.getBody());
        }
    }

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserById {
        @Test
        @DisplayName("Should return user by id")
        void shouldReturnUserById() {
            Long userId = 1L;
            Users user = Users.builder().id(userId).email("test@test.com").password("password").build();

            when(usersService.getUserById(userId)).thenReturn(Optional.of(user));

            ResponseEntity<Optional<Users>> response = usersController.getUserById(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isPresent());
            assertEquals(user, response.getBody().get());
        }
    }

    @Nested
    @DisplayName("PUT /users/update/{id}")
    class UpdateUser {
        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            Long userId = 1L;
            Users userToUpdate = Users.builder()
                    .id(userId)
                    .email("update@test.com")
                    .password("newPassword")
                    .build();

            Users updatedUser = Users.builder()
                    .id(userId)
                    .email("updated@test.com")
                    .password("newPassword")
                    .build();

            when(usersService.updateUser(eq(userId), any(Users.class))).thenReturn(updatedUser);

            ResponseEntity<Users> response = usersController.updateUser(userId, userToUpdate);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(updatedUser, response.getBody());
            verify(usersService).updateUser(userId, userToUpdate);
        }
    }

}
