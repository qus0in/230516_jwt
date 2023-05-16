package io.playdata.jwt.filter;

import io.playdata.jwt.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 생성자를 통한 의존성 주입
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // Autowired를 통해서 어노테이션으로 의존성 주입 예시
//    @Autowired
//    private JwtUtils jwtUtils;
//    @Autowired
//    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwtToken = parseJwtToken(request); // http 요청에서 request 파트를 뽑아내서 거기서 jwt를 추출 및 분석
            // hasText - 문자열이 비어있지 않은지 + jwtUtils 토큰 검증 메소드를 사용해서 이게 정상 토큰
            if (StringUtils.hasText(jwtToken) && jwtUtils.validateJwtToken(jwtToken)) {
                // jwtToken에서 username을 추출하고
                String username = jwtUtils.getUsernameFromJwtToken(jwtToken);
                // username으로 UserDetails(유저정보)를 추출
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 유저의 인증정보를 Spring Security 에 전달 -> 인증 처리가 완료
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 진행
            }
        } catch (Exception e) {
            // Handle authentication exception if needed
        }

        filterChain.doFilter(request, response);
    }

    // parseJwtToken -> Jwt를 분석
    private String parseJwtToken(HttpServletRequest request) {
        // request -> http 요청
        // getHeader -> key가 Authorization -> value
        String headerValue = request.getHeader("Authorization");
        // hasText -> 빈 텍스트가 아니라면, Bearer 로 시작하는지를 보고,
        if (StringUtils.hasText(headerValue) && headerValue.startsWith("Bearer ")) {
            // substring -> 일정 인덱스 이후로부터 추출
            return headerValue.substring(7); // 0~6 : 'Bearer ' + 7~: jwt
        }
        return null;
    }
}
