package baekgwa.backend.global.security.filter;


import static baekgwa.backend.global.constant.JwtConstants.ACCESS;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH_KEY;

import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.global.security.user.CustomUserDetails;
import baekgwa.backend.model.redis.RedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 로그인 필터
 * Security Configuration Filter Chain 설정에서, Request Path 지정
 */
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;
    private final JWTUtil jwtUtil;
    private final long refreshExpiredMs;
    private final long accessExpiredMs;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        LoginDto loginDto = obtainLoginDto(request);
        String loginId = loginDto.getLoginId();
        String password = loginDto.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String uuid = customUserDetails.getUuid();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", uuid, role, accessExpiredMs);
        String refresh = jwtUtil.createJwt("refresh", uuid, role, refreshExpiredMs);

        redisRepository.delete(REFRESH_KEY + uuid);
        redisRepository.save(REFRESH_KEY + uuid, refresh, refreshExpiredMs, TimeUnit.MILLISECONDS);

        response.setHeader(ACCESS, access);
        response.addCookie(createCookie(REFRESH, refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private LoginDto obtainLoginDto(HttpServletRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(request.getInputStream(), LoginDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read login data from request body", e);
        }
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) refreshExpiredMs/1000);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
