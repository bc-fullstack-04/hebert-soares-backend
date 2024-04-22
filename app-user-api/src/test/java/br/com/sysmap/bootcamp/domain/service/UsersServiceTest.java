package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repositories.UsersRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.domain.services.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private UsersService usersService;

    @Captor
    private ArgumentCaptor<Users> userCaptor;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class CreateUser {
        @Test
        @DisplayName("Should create a user and wallet with success")
        void shouldCreateAUserAndWallet() {
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            Users newUser = Users.builder()
                    .email("email@email.com")
                    .password("password")
                    .build();
            Users savedUser = Users.builder()
                    .id(1L)
                    .email("email@email.com")
                    .password("encodedPassword")
                    .build();
            Wallet newWallet = new Wallet();
            newWallet.setUser(savedUser);
            newWallet.setBalance(new BigDecimal("0"));
            newWallet.setPoints(0L);
            newWallet.setLastUpdate(LocalDateTime.now());

            when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(usersRepository.save(any(Users.class))).thenReturn(savedUser);
            when(walletRepository.save(any(Wallet.class))).thenReturn(newWallet);

            Users result = usersService.save(newUser);

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals("email@email.com", result.getEmail()),
                    () -> assertEquals("encodedPassword", result.getPassword())
            );
            verify(walletRepository, times(1)).save(any(Wallet.class));
        }

        @Test
        @DisplayName("Should throw exception when user exists")
        void shouldThrowExceptionWhenUserExists() {
            Users existingUser = Users.builder()
                    .email("email@email.com")
                    .password("password")
                    .build();
            when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

            Exception exception = assertThrows(RuntimeException.class, () -> usersService.save(existingUser));

            assertEquals("User already exists", exception.getMessage());
        }
    }

    @Nested
    class GetUser {
        @Test
        @DisplayName("Should return a user by ID")
        void shouldReturnUserById() {
            Long userId = 1L;
            Users user = Users.builder()
                    .id(userId)
                    .email("email@email.com")
                    .password("password")
                    .build();

            when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));

            Optional<Users> result = usersService.getUserById(userId);

            assertTrue(result.isPresent());
            assertEquals(userId, result.get().getId());
        }
    }

    @Nested
    class ListUsers {
        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() {
            var users = List.of(
                    Users.builder().id(1L).email("user1@email.com").password("pass1").build(),
                    Users.builder().id(2L).email("user2@email.com").password("pass2").build()
            );

            when(usersRepository.findAll()).thenReturn(users);

            List<Users> result = usersService.getAllUsers();

            assertEquals(2, result.size());
            verify(usersRepository, times(1)).findAll();
        }
    }

    @Nested
    class UpdateUser {
        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            Long userId = 1L;
            Users existingUser = Users.builder()
                    .id(userId)
                    .email("email@email.com")
                    .password("password")
                    .build();
            Users userUpdates = Users.builder()
                    .email("newemail@email.com")
                    .password("newPassword")
                    .build();

            when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(usersRepository.save(any(Users.class))).thenReturn(existingUser.toBuilder()
                    .email(userUpdates.getEmail())
                    .password(userUpdates.getPassword())
                    .build());

            Users result = usersService.updateUser(userId, userUpdates);

            assertNotNull(result);
            assertEquals(userUpdates.getEmail(), result.getEmail());
            assertEquals(userUpdates.getPassword(), result.getPassword());

            verify(usersRepository).save(any(Users.class));
        }
    }

}