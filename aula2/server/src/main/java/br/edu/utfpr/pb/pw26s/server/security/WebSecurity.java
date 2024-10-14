package br.edu.utfpr.pb.pw26s.server.security;

import br.edu.utfpr.pb.pw26s.server.service.AuthService;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@Configuration
public class WebSecurity {

    private final AuthService authService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public WebSecurity(AuthService authService,
                       AuthenticationEntryPoint authenticationEntryPoint) {
        this.authService = authService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    @Bean
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // authenticationManager -> responsável pela autenticação dos usuários
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(authService)
                .passwordEncoder( passwordEncoder() );
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        //Configuração para funcionar o console do H2.
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint));
        http.cors(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(antMatcher(HttpMethod.POST, "/users/**")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers(antMatcher("/error/**")).permitAll()
                .requestMatchers(antMatcher("/v3/**")).permitAll()
                .requestMatchers(antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(antMatcher("/actuator/**")).permitAll()
                .anyRequest().authenticated()
        );
        http.authenticationManager(authenticationManager)
                //Filtro de Autenticação
                .addFilter(new JWTAuthenticationFilter(authenticationManager, authService))
                //Filtro de Autorização
                .addFilter(new JWTAuthorizationFilter(authenticationManager, authService))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
