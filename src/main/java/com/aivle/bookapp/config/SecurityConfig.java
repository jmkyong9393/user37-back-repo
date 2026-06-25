package com.aivle.bookapp.config;

import com.aivle.bookapp.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
                        }
                ))
                .authorizeHttpRequests(auth -> auth
                        // 1. K8s Actuator 헬스체크 무조건 허용 (제일 중요!)
                        .requestMatchers("/actuator/**").permitAll()
                        // 2. 브라우저 CORS 사전 요청(Preflight) 무조건 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // 3. 로그인/회원가입/H2 허용
                        .requestMatchers("/users/register", "/users/login", "/users/refresh").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        
                        // 4. 도서 및 댓글 '조회(GET)'는 누구나 가능
                        .requestMatchers(HttpMethod.GET, "/books", "/books/**").permitAll()
                        
                        // 5. 생성/수정/삭제 로직은 인증(로그인) 필요 (중복 제거됨)
                        .requestMatchers(HttpMethod.POST, "/books").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/books/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/books/*/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/books/*/comments/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/books/*/comments/*/like").authenticated()
                        
                        // 그 외 나머지는 다 막음
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}