package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.services.WalletService;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Nested
    @DisplayName("POST /wallet/credit/{value}")
    class CreditValue {
        @Test
        @DisplayName("Should credit value successfully")
        void shouldCreditValueSuccessfully() {
            BigDecimal creditAmount = new BigDecimal("100");

            doNothing().when(walletService).credit(creditAmount);

            ResponseEntity<String> response = walletController.credit(creditAmount);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Credit successful", response.getBody());
            verify(walletService, times(1)).credit(creditAmount);
        }
    }

    @Nested
    @DisplayName("GET /wallet")
    class MyWallet {
        @Test
        @DisplayName("Should return the user's wallet")
        void shouldReturnUsersWallet() {
            Wallet wallet = new Wallet();
            wallet.setBalance(new BigDecimal("1000"));
            wallet.setPoints(100L);

            when(walletService.myWallet()).thenReturn(Optional.of(wallet));

            Optional<Wallet> response = walletController.wallet();

            assertTrue(response.isPresent());
            assertEquals(new BigDecimal("1000"), response.get().getBalance());
            assertEquals(100L, response.get().getPoints());
        }
    }
}
