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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
            wallet.setPoints(0L);  // Ensure points are initialized

            when(usersService.findByEmail("user@email.com")).thenReturn(user);
            when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

            walletService.credit(new BigDecimal("50.00"));

            assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("150.00")));
            verify(walletRepository).save(wallet);
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
