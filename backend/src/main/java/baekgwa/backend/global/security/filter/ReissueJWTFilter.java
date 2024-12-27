package baekgwa.backend.global.security.filter;

import static baekgwa.backend.global.constant.JwtConstants.ACCESS;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH;

import baekgwa.backend.global.security.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Token 재발급 Filter
 * setFilterProcessUrl() 로, 작동할 Path 지정 가능.
 */
@RequiredArgsConstructor
public class ReissueJWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
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

        String newAccess = jwtUtil.createJwt("access", uuid, role);
        response.setHeader(ACCESS, newAccess);
        response.setStatus(HttpServletResponse.SC_OK);
        return;
    }
}
