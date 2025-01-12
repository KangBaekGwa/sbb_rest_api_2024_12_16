package baekgwa.backend.global.config;

import baekgwa.backend.global.constant.JwtConstants;
import baekgwa.backend.global.security.entrypoint.CustomAccessDeniedHandler;
import baekgwa.backend.global.security.entrypoint.CustomAuthenticationEntryPoint;
import baekgwa.backend.global.security.filter.JWTFilter;
import baekgwa.backend.global.security.filter.LoginFilter;
import baekgwa.backend.global.security.filter.ReissueJWTFilter;
import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.model.redis.RedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final ObjectMapper objectMapper;
    private final JWTFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

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
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Collections.singletonList(JwtConstants.ACCESS));

        return request -> configuration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationConfiguration authenticationConfiguration) throws Exception {

        LoginFilter loginFilter = new LoginFilter(
                authenticationManager(authenticationConfiguration), redisRepository, jwtUtil,
                refreshExpiredMs, accessExpiredMs, objectMapper);
        loginFilter.setFilterProcessesUrl("/login"); //로그인 경로 지정

        ReissueJWTFilter reissueJWTFilter = new ReissueJWTFilter(
                redisRepository, jwtUtil, refreshExpiredMs, accessExpiredMs, objectMapper);
        reissueJWTFilter.setFilterProcessesUrl("/reissue");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/admin").authenticated()
                        .requestMatchers(HttpMethod.POST, "/board/question").authenticated()
                        .requestMatchers(HttpMethod.POST, "/board/{questionId}/answer").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint) //인증 오류
                        .accessDeniedHandler(customAccessDeniedHandler)) //권한 부족
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, LoginFilter.class)
                .addFilterBefore(reissueJWTFilter, JWTFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }
}