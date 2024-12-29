package baekgwa.backend.global.config;

import baekgwa.backend.global.security.filter.JWTFilter;
import baekgwa.backend.global.security.filter.LoginFilter;
import baekgwa.backend.global.security.filter.ReissueJWTFilter;
import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.model.redis.RedisRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Authentication Manager : 인증을 처리하는 인터페이스. PasswordEncoder : Security 내부에서 사용할, 암호화를 검증하는데 사용하는
 * 인터페이스.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final RedisRepository redisRepository;
    @Value("${spring.jwt.refresh.expiredTime}")
    private long refreshExpiredMs;
    @Value("${spring.jwt.access.expiredTime}")
    private long accessExpiredMs;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        return request -> configuration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationConfiguration authenticationConfiguration) throws Exception {

        JWTFilter jwtFilter = new JWTFilter(jwtUtil);

        LoginFilter loginFilter = new LoginFilter(
                authenticationManager(authenticationConfiguration), redisRepository, jwtUtil,
                refreshExpiredMs, accessExpiredMs);
        loginFilter.setFilterProcessesUrl("/login"); //로그인 경로 지정

        ReissueJWTFilter reissueJWTFilter = new ReissueJWTFilter(
                redisRepository, jwtUtil, refreshExpiredMs, accessExpiredMs);
        reissueJWTFilter.setFilterProcessesUrl("/reissue");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/admin").authenticated()
                        .anyRequest().permitAll())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, LoginFilter.class)
                .addFilterBefore(reissueJWTFilter, JWTFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }
}