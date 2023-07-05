package ru.team38.userservice.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.team38.common.jooq.tables.records.AccountRecord;
import ru.team38.userservice.data.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    private final AccountRepository accountRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            AccountRecord account = accountRepository.getAccountByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Account does not exist"));
            List<GrantedAuthority> authorities = new ArrayList<>();
            return new User(account.getEmail(), account.getPassword(), true, true,
                    true, account.getIsBlocked(), authorities);
        };
    }
}
