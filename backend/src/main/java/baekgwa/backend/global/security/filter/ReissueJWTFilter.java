package baekgwa.backend.global.security.filter;

import static baekgwa.backend.global.constant.JwtConstants.ACCESS;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH_KEY;

import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.model.redis.RedisRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Token 재발급 Filter
 * setFilterProcessUrl() 로, 작동할 Path 지정 가능.
 */
@RequiredArgsConstructor
public class ReissueJWTFilter extends OncePerRequestFilter {

    private final RedisRepository redisRepository;
    private final JWTUtil jwtUtil;
    private final long refreshExpiredMs;
    private final long accessExpiredMs;

    @Setter private String filterProcessesUrl = "/reissue";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().equals(filterProcessesUrl)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(REFRESH)) {
                refresh = cookie.getValue();
                break;
            }
        }

        if(refresh == null) {
            PrintWriter writer = response.getWriter();
            writer.print("refresh token empty");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("refresh token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals(REFRESH)) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String uuid = jwtUtil.getUuid(refresh);
        String role = jwtUtil.getRole(refresh);

        if(!redisRepository.get(REFRESH_KEY + uuid).equals(refresh)) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid refresh token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String newAccess = jwtUtil.createJwt("access", uuid, role, accessExpiredMs);
        String newRefresh = jwtUtil.createJwt("refresh", uuid, role, refreshExpiredMs);
        redisRepository.delete(REFRESH_KEY + uuid);
        redisRepository.save(REFRESH_KEY + uuid, newRefresh, refreshExpiredMs, TimeUnit.MILLISECONDS);
        response.setHeader(ACCESS, newAccess);
        response.addCookie(createCookie(REFRESH, newRefresh));
        response.setStatus(HttpStatus.OK.value());
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
