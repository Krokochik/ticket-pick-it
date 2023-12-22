package krokochik.backend.config;

import krokochik.backend.repo.RememberMeTokenRepository;
import krokochik.backend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfigurer {

    @Autowired
    ApplicationArguments args;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RememberMeTokenRepository tokenRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.convertToUser(userRepository
                    .findByUsername(username));
            if (user != null) {
                return user;
            } else {
                throw new UsernameNotFoundException("User not found");
            }
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/clinics/**", "/clinic/**", "/medic/**",
                "/speciality/**", "/ping");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET)
                            .permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/signup")
                            .access((auth, o) ->
                                new AuthorizationDecision(
                                        auth.get().getAuthorities()
                                                .contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                                ))
                        .requestMatchers(HttpMethod.POST, "/ping")
                            .permitAll()
                        .anyRequest().authenticated()
                );

        if ((args.getOptionValues("csrf") == null ||
                !args.getOptionValues("csrf").contains("on")) &&
                !args.getNonOptionArgs().contains("-csrf")) {
            http.csrf(AbstractHttpConfigurer::disable);
        } else {
            http.csrf(conf -> conf
                    .csrfTokenRepository(CookieCsrfTokenRepository
                            .withHttpOnlyFalse()));
            log.info("CSRF protection enabled");
        }

        if ((args.getOptionValues("cors") == null ||
                !args.getOptionValues("cors").contains("on")) &&
                !args.getNonOptionArgs().contains("-cors")) {
            http.cors(AbstractHttpConfigurer::disable);
        } else {
            http.cors(request ->
                    new CorsConfiguration().applyPermitDefaultValues());
            log.info("CORS headers configured");
        }

        http.userDetailsService(
                userDetailsService());
        http.authenticationProvider(authenticationProvider());

        http.formLogin(login -> login
                .loginPage("/auth/login")
                .failureUrl("/auth/failure")
                .defaultSuccessUrl("/auth/success")
                .permitAll());
        http.logout(LogoutConfigurer::permitAll);

        http.rememberMe(conf -> conf
                .rememberMeParameter("remember")
                .userDetailsService(userDetailsService())
                .tokenRepository(tokenRepository)
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(5)));
        return http.build();
    }
}
