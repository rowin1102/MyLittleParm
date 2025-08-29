package org.spring.projectjs.auth;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod; // ★ 추가: OPTIONS 매처에 필요

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize 등 사용 시 역할 계층 반영
public class WebSecurityConfig {

  @Autowired
  public MyAuthFailureHandler myAuthFailureHandler;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  /** 역할 계층: ADMIN > USER */
  @Bean
  static RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
  }

  /** 메서드 보안(SpEL)에서 역할 계층 적용 */
  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService uds) throws Exception {
    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider, uds);

    http
      .csrf(csrf -> csrf.disable())
      .cors(withDefaults())
      .authorizeHttpRequests(req -> req
        // ★ 프리플라이트 전부 통과
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()

        // 공개 페이지 & 정적 리소스
        .requestMatchers("/", "/regist.do", "/checkDuplicate.do", "/checkNicknameDuplicate.do").permitAll()
        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/favicon.ico", "/webjars/**").permitAll()

        // 공개 API
        .requestMatchers("/api/member/**").permitAll()
        .requestMatchers("/api/board/**").permitAll()
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/recover/**").permitAll()
        .requestMatchers("/api/transaction/**").permitAll()
        .requestMatchers("/api/admin/chat/**").permitAll()  // ★ 추가
        .requestMatchers("/api/plants/**").permitAll()      // ★ 추가

        // 인증 필요 API
        .requestMatchers("/api/calendar/**").authenticated()
        .requestMatchers("/api/character/**").authenticated()
        .requestMatchers("/api/shop/**").authenticated()
        .requestMatchers("/api/quests/**").authenticated()

        // 페이지/액션 접근 정책
        .requestMatchers("/calendar.do", "/calendar/**").hasAnyRole("USER", "ADMIN")
        .requestMatchers("/boardWrite.do").authenticated()

        // 🔒 수정/삭제/댓글 액션은 로그인 필수
        .requestMatchers(
            "/boardEdit.do",
            "/boardDelete.do",
            "/boardDeleteFileOne.do",
            "/boardDeleteFileAll.do",
            "/commentWrite.do",
            "/commentDelete.do"
        ).authenticated()

        // 관리자
        .requestMatchers("/admin/**").hasRole("ADMIN")

        // 그 외
        .anyRequest().permitAll()
      )
      .formLogin(form -> form
        .loginPage("/myLogin.do")
        .loginProcessingUrl("/myLoginAction.do")
        .successHandler(loginSuccessHandler())
        .failureHandler(myAuthFailureHandler)
        .usernameParameter("my_id")
        .passwordParameter("my_pass")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/myLogout.do")
        .logoutSuccessUrl("/")
        .permitAll()
      )
      .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
        // ✅ CORS preflight는 200
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
          res.setStatus(HttpServletResponse.SC_OK);
          return;
        }
        // AJAX/API 요청은 401, 그 외는 로그인 페이지로 리다이렉트
        boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"))
            || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));
        if (isAjax) {
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
          String original = req.getRequestURI()
              + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
          String target = java.net.URLEncoder.encode(original, java.nio.charset.StandardCharsets.UTF_8);
          String ctx = req.getContextPath();
          res.sendRedirect(ctx + "/myLogin.do?redirect=" + target);
        }
      }));

    // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 위치
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  UserDetailsService userDetailsService(DataSource dataSource) {
    JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
    // enabled 컬럼이 없다면 1(활성)로 고정
    manager.setUsersByUsernameQuery(
        "select user_id, password, 1 as enabled from member where user_id = ?");
    // 권한은 ROLE_ 접두사 포함 값이어야 함 (예: ROLE_USER, ROLE_ADMIN)
    manager.setAuthoritiesByUsernameQuery(
        "select user_id, member_auth from member where user_id = ?");
    return manager;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * ✅ CORS 설정(확장형):
   *  - allowedOriginPatterns 로 로컬/사설 IP 와일드카드 허용
   *  - 프리플라이트, 모든 헤더/노출헤더 허용
   *  - withCredentials(true) 대응: allowCredentials(true)
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();

    // ★ allowedOriginPatterns 로 전환(유연한 로컬/사설IP 허용)
    cfg.setAllowedOriginPatterns(List.of(
        "http://localhost:*",
        "http://127.0.0.1:*",
        "http://172.30.*.*:*",
        "http://192.168.*.*:*"
    ));

    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));  // ★ 모든 요청 헤더 허용
    cfg.setExposedHeaders(List.of("*"));  // ★ 모든 응답 헤더 노출
    cfg.setAllowCredentials(true);        // ★ 쿠키/Authorization 허용
    cfg.setMaxAge(3600L);                 // 프리플라이트 캐시(초)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  @Bean
  AuthenticationSuccessHandler loginSuccessHandler() {
    return (request, response, authentication) -> {
      System.out.println("[LOGIN] " + authentication.getName() + " => " + authentication.getAuthorities());
      String target = request.getParameter("redirect");
      if (target != null && !target.isBlank()) response.sendRedirect(target);
      else response.sendRedirect("/");
    };
  }
}
