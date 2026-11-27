package com.thezone.tienda.seguridad;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    private static final String ADMIN = "ADMINISTRADOR";

    private final JwtFiltro jwtFiltro;
    private final List<String> origenesPermitidos;

    public SeguridadConfig(
            JwtFiltro jwtFiltro,
            @Value("${tienda.cors.origenes}") List<String> origenesPermitidos) {
        this.jwtFiltro = jwtFiltro;
        this.origenesPermitidos = origenesPermitidos;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOriginPatterns(origenesPermitidos);
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);

        return fuente;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(login -> login.disable())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/registro",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/productos/criticos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**", "/api/productos/**").permitAll()
                        .requestMatchers("/api/categorias/**", "/api/productos/**").hasRole(ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/ordenes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ordenes/mias").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/ordenes/*").permitAll()
                        .requestMatchers("/api/ordenes/**").hasRole(ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole(ADMIN)
                        .requestMatchers("/api/usuarios/**").authenticated()

                        .anyRequest().authenticated())
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
