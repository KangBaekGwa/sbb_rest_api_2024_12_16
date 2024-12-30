package baekgwa.backend.global.security.filter;

import static baekgwa.backend.global.constant.JwtConstants.ACCESS;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH;
import static baekgwa.backend.global.constant.JwtConstants.REFRESH_KEY;

import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.global.response.SuccessCode;
import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.model.redis.RedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Token 재발급 Filter setFilterProcessUrl() 로, 작동할 Path 지정 가능.
 */
@RequiredArgsConstructor
public class ReissueJWTFilter extends OncePerRequestFilter {

    private final RedisRepository redisRepository;
    private final JWTUtil jwtUtil;
    private final long refreshExpiredMs;
    private final long accessExpiredMs;
    private final ObjectMapper objectMapper;

    @Setter
    private String filterProcessesUrl = "/reissue";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().equals(filterProcessesUrl)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("POST")) {
            writeErrorResponse(response, ErrorCode.NOT_SUPPORTED_REISSUE_METHOD);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH)) {
                refresh = cookie.getValue();
                break;
            }
        }

        if (refresh == null) {
            writeErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return;
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, ErrorCode.REFRESH_TOKEN_EXPIRED);
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals(REFRESH)) {
            writeErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return;
        }

        String uuid = jwtUtil.getUuid(refresh);
        String role = jwtUtil.getRole(refresh);

        if (!redisRepository.get(REFRESH_KEY + uuid).equals(refresh)) {
            writeErrorResponse(response, ErrorCode.INVALID_REFRESH_TOKEN);
            return;
        }

        String newAccess = jwtUtil.createJwt("access", uuid, role, accessExpiredMs);
        String newRefresh = jwtUtil.createJwt("refresh", uuid, role, refreshExpiredMs);
        redisRepository.delete(REFRESH_KEY + uuid);
        redisRepository.save(REFRESH_KEY + uuid, newRefresh, refreshExpiredMs,
                TimeUnit.MILLISECONDS);

        BaseResponse<Void> successResponse = BaseResponse.ok(SuccessCode.REISSUE_TOKEN_SUCCESS);
        response.setHeader(ACCESS, newAccess);
        response.addCookie(createCookie(REFRESH, newRefresh));
        response.setContentType("application/json");
        response.setStatus(SuccessCode.REISSUE_TOKEN_SUCCESS.getHttpStatus().value());
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(successResponse));
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) refreshExpiredMs / 1000);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode)
            throws IOException {
        BaseResponse<Void> errorResponse = BaseResponse.fail(errorCode);
        response.setContentType("application/json");
        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
