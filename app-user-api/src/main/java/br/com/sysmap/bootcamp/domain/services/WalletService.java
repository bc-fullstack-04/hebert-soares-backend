package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.enums.DayPoints;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService implements Serializable {
    private final UsersService usersService;
    private final WalletRepository walletRepository;

    public void credit(BigDecimal creditAmount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Users user = usersService.findByEmail(userEmail);
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Wallet is not found for user"));

        if (creditAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The credit amount cannot be negative.");
        }

        BigDecimal currentBalance = wallet.getBalance();
        BigDecimal newBalance = currentBalance.add(creditAmount);
        wallet.setBalance(newBalance);
        wallet.setLastUpdate(LocalDateTime.now());

        walletRepository.save(wallet);
    }

    public Optional<Wallet> myWallet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Users user = usersService.findByEmail(userEmail);
        return walletRepository.findByUser(user);
    }

    public void debit(WalletDto walletDto) {
        Users user = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Wallet is not found for email: " + walletDto.getEmail()));

        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        int pointsForToday = DayPoints.getPointsByDayOfWeek(dayOfWeek);

        wallet.setPoints(wallet.getPoints() + pointsForToday);

        walletRepository.save(wallet);
    }
}
