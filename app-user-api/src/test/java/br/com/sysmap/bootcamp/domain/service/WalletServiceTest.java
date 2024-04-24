package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.domain.services.UsersService;
import br.com.sysmap.bootcamp.domain.services.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@email.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    class CreditMethod {
        @Test
        @DisplayName("Should credit a positive amount successfully")
        void shouldCreditPositiveAmount() {
            Users user = Users.builder().email("user@email.com").build();
            Wallet wallet = new Wallet();
            wallet.setBalance(new BigDecimal("100.00"));
            wallet.setPoints(0L);

            when(usersService.findByEmail("user@email.com")).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

            walletService.credit(new BigDecimal("50.00"));

            assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("150.00")));
            verify(walletRepository).save(wallet);
        }

        @Test
        @DisplayName("Should credit amount to the correct wallet")
        void shouldCreditAmountToCorrectWallet() {
            Users user = Users.builder().email("user@email.com").build();
            Wallet wallet1 = new Wallet();
            wallet1.setBalance(new BigDecimal("50.00"));
            wallet1.setPoints(0L);
            Wallet wallet2 = new Wallet();
            wallet2.setBalance(new BigDecimal("100.00"));
            wallet2.setPoints(0L);

            when(usersService.findByEmail("user@email.com")).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet1));

            walletService.credit(new BigDecimal("50.00"));

            assertEquals(0, wallet1.getBalance().compareTo(new BigDecimal("100.00")));
            assertEquals(0, wallet2.getBalance().compareTo(new BigDecimal("100.00")));
            verify(walletRepository).save(wallet1);
        }

        @Test
        @DisplayName("Should handle no existing wallet")
        void shouldHandleNoWallet() {
            Users user = Users.builder()
                    .email("user@email.com")
                    .build();

            when(usersService.findByEmail("user@email.com")).thenReturn(user);
            when(walletRepository.findByUser(any())).thenReturn(Optional.empty());
            assertThrows(NoSuchElementException.class, () -> walletService.credit(new BigDecimal("10.00")));
        }
    }

    @Nested
    class MyWalletMethod {
        @Test
        @DisplayName("Should return the wallet for the logged-in user")
        void shouldReturnWallet() {
            Users user = Users.builder().email("user@email.com").build();
            Wallet wallet = new Wallet();
            wallet.setPoints(0L);

            when(usersService.findByEmail("user@email.com")).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
            Optional<Wallet> result = walletService.myWallet();

            assertTrue(result.isPresent());
            assertEquals(wallet, result.get());
        }

        @Test
        @DisplayName("Should return empty optional if user not found")
        void shouldReturnEmptyOptionalIfUserNotFound() {
            when(usersService.findByEmail(anyString())).thenReturn(null);
            Optional<Wallet> result = walletService.myWallet();
            assertFalse(result.isPresent());
        }
    }

    @Nested
    class DebitMethod {
        @Test
        @DisplayName("Should debit an amount successfully")
        void shouldDebitAmount() {
            WalletDto walletDto = new WalletDto("user@email.com", new BigDecimal("50.00"));
            Users user = Users.builder().email("user@email.com").build();
            Wallet wallet = new Wallet();
            wallet.setBalance(new BigDecimal("200.00"));
            wallet.setPoints(0L);

            when(usersService.findByEmail("user@email.com")).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

            walletService.debit(walletDto);

            assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("150.00")));
            verify(walletRepository).save(wallet);
        }
    }
}

