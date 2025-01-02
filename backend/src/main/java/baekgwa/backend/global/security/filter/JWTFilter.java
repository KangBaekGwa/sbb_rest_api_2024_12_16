package baekgwa.backend.global.security.filter;

import static baekgwa.backend.global.constant.JwtConstants.ACCESS;

import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.global.security.user.CustomUserDetails;
import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Filter
 * 입력받은, Access Token 으로, Security Context Holder 에, 인증을 추가.
 */
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader(ACCESS);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
            return;
        } catch (JwtException e) {
            writeErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals(ACCESS)) {
            writeErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
            return;
        }

        String uuid = jwtUtil.getUuid(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User userData = User
                .builder()
                .uuid(uuid)
                .role(UserRole.valueOf(role))
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(userData);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        BaseResponse<Void> errorResponse = BaseResponse.fail(errorCode);
        response.setContentType("application/json");
        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
