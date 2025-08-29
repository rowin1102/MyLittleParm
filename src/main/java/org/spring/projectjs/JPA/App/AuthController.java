package org.spring.projectjs.JPA.App;

import org.spring.projectjs.JPA.App.Entity.Member;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.spring.projectjs.auth.JwtTokenProvider;
import org.spring.projectjs.jdbc.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            // DB에서 Member 찾아오기 (username = userId 매핑)
            Member member = memberRepository.findByUserId(req.getUsername())
                    .orElseThrow(() -> new RuntimeException("Member not found"));

            System.out.println("[AUTH] userId=" + member.getUserId()
                    + ", memberIdx=" + member.getMemberIdx()
                    + ", gamePoint=" + member.getGamePoint()
                    + ", gameExp=" + member.getGameExp()
                    + ", gameLevel=" + member.getGameLevel());

            long gamePoint  = member.getGamePoint()  == null ? 0L : member.getGamePoint();
            long gameExp    = member.getGameExp()    == null ? 0L : member.getGameExp();
            long gameLevel  = member.getGameLevel()  == null ? 1L : member.getGameLevel();

            // ✅ 토큰 발급 (userIdx + userId 같이 넣기)
            String token = tokenProvider.generateToken(
                    member.getUserId()      , // 로그인 ID
                    member.getMemberIdx() // PK

            );

            // 유저 정보 + 토큰 내려주기
            Map<String, Object> user = new HashMap<>();
            user.put("id", member.getMemberIdx());   // ✅ PK값
            user.put("username", member.getUserId()); // 로그인 ID
            user.put("nickname", member.getNickname()); // 닉네임
            user.put("roles", List.of("USER"));        // ROLE
            user.put("gamePoint", gamePoint);  // ✅ 코인 (gamePoint)
            user.put("gameExp", gameExp);      //
            user.put("gameLevel", gameLevel);  //

            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("user", user);

            return ResponseEntity.ok(body);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(new ErrorResponse("아이디나 비밀번호가 맞지 않습니다."));
        }
    }



    // DTO inner classes or separate files 가능
    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String username;
        private String token;
        public AuthResponse(String username, String token) { this.username = username; this.token = token; }
        // getters
        public String getUsername() { return username; }
        public String getToken() { return token; }
    }

    public static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

}
