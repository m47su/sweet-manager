package br.com.confeitaria.sweet_manager.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cadastro.html", "/usuarios/cadastrar", "/assets/**", "/login").permitAll()
                        .requestMatchers("/usuarios/cadastrar-admin").hasRole("ADMIN") 
                        .requestMatchers("/admin.html").hasRole("ADMIN") 
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/cadastro.html")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities();
                            if (roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin.html");
                            } else {
                                response.sendRedirect("/index.html");
                            }
                        })
                        .permitAll());
        return http.build();
    }
}