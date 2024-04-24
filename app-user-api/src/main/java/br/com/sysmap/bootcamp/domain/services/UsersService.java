package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repositories.UsersRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user){
        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if(usersOptional.isPresent()){
            throw new RuntimeException("User already exists");
        }
        user = user.toBuilder().password(this.passwordEncoder.encode(user.getPassword())).build();
        Users savedUser = this.usersRepository.save(user);
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal(0));
        wallet.setPoints(0L);
        wallet.setLastUpdate(LocalDateTime.now());
        walletRepository.save(wallet);
        return savedUser;
    }

    public List<Users> getAllUsers(){
        return usersRepository.findAll();
    }

    public Optional<Users> getUserById(Long id){
        return usersRepository.findById(id);
    }

    public Users updateUserWithCurrentUserInfo(Users userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authentication found in context or not authenticated.");
        }
        String currentEmail = authentication.getPrincipal().toString();
        Users currentUser = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setEmail(userDetails.getEmail());
        currentUser.setPassword(userDetails.getPassword());

        return usersRepository.save(currentUser);
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> userAccountsOptional = this.usersRepository.findByEmail(username);

        return userAccountsOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public Users findByEmail(String email){
        return this.usersRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AuthDto auth(AuthDto authDto){
        Users users = this.findByEmail(authDto.getEmail());
       if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())){
           throw new RuntimeException("Invalid password");
       }
       StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());
        return AuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(users.getId()).build();
    }

    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authentication found in context.");
        }
        String email = authentication.getName();
        log.info("Retrieving user for email: {}", email);
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

}
