package org.spring.projectjs.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.spring.projectjs.jdbc.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
public class EmailVerifyController {

    @Autowired
    private MemberService memberService;

    // x-www-form-urlencoded (purpose 지원)
    @PostMapping(
        value = "/send",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> sendForm(@RequestParam String email,
                                      @RequestParam(required = false, defaultValue = "SU") String purpose) {
        try {
            String e = email == null ? "" : email.trim();
            String p = (purpose == null || purpose.isBlank()) ? "SU" : purpose.trim();

            if (e.isEmpty() || !e.contains("@")) {
                return ResponseEntity.badRequest()
                        .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                        .header("Pragma", "no-cache")
                        .body(Map.of("message", "이메일을 올바르게 입력해 주세요."));
            }

            // 비동기로 메일 발송 (응답을 빠르게)
            CompletableFuture.runAsync(() -> {
                try { memberService.sendEmailVerification(e, p); } catch (Exception ignore) {}
            });

            return ResponseEntity.accepted()
                    .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                    .header("Pragma", "no-cache")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                        "message", "인증 메일을 전송했습니다.",
                        "purpose", p
                    ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                    .header("Pragma", "no-cache")
                    .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                    .header("Pragma", "no-cache")
                    .body(Map.of("message", "메일 전송 실패"));
        }
    }

    // JSON도 허용 (purpose 지원)
    public static record EmailReq(String email, String purpose) {}

    @PostMapping(
        value = "/send",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> sendJson(@RequestBody EmailReq req) {
        String purpose = req.purpose() == null || req.purpose().isBlank() ? "SU" : req.purpose();
        return sendForm(req.email(), purpose);
    }

    // 메일 링크: 인증 완료 처리(HTML은 기존 디자인 그대로 반환)
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        boolean ok = memberService.verifyEmailTokenForSignup(token);
        if (ok) {
            String html = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>이메일 인증 완료 - 마리팜</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                            background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
                            min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px;
                        }
                        .container {
                            background: white; border-radius: 16px; box-shadow: 0 8px 32px rgba(0,0,0,0.1);
                            padding: 48px 40px; text-align: center; max-width: 480px; width: 100%; position: relative; overflow: hidden;
                        }
                        .container::before { content:''; position:absolute; top:0; left:0; right:0; height:4px; background: linear-gradient(135deg,#8FBC8F,#7AA87A); }
                        .success-icon {
                            width: 100px; height: 100px; background: linear-gradient(135deg,#8FBC8F,#7AA87A);
                            border-radius: 50%; display:flex; align-items:center; justify-content:center; margin:0 auto 24px; animation: bounceIn .6s ease-out;
                        }
                        .checkmark { font-size: 48px; color: white; animation: checkPop .4s ease-out .3s both; }
                        .brand { color:#8FBC8F; font-size:18px; font-weight:600; margin-bottom:8px; letter-spacing:-.3px; }
                        .title { font-size:28px; font-weight:700; color:#1f2937; margin-bottom:16px; letter-spacing:-.5px; }
                        .message { font-size:16px; color:#6b7280; line-height:1.6; margin-bottom:32px; }
                        .countdown {
                            display:inline-flex; align-items:center; gap:8px; background:#f8fafc; border:1px solid #e2e8f0;
                            border-radius:8px; padding:12px 16px; font-size:14px; color:#64748b; margin-top:24px;
                        }
                        .countdown-number {
                            background:#8FBC8F; color:white; width:24px; height:24px; border-radius:50%;
                            display:flex; align-items:center; justify-content:center; font-weight:600; font-size:12px;
                        }
                        @keyframes bounceIn { 0%{transform:scale(.3);opacity:0} 50%{transform:scale(1.1)} 100%{transform:scale(1);opacity:1} }
                        @keyframes checkPop { 0%{transform:scale(0)} 50%{transform:scale(1.2)} 100%{transform:scale(1)} }
                        @media (max-width:480px) {
                            .container { padding:32px 24px; margin:16px; }
                            .title { font-size:24px; }
                            .success-icon { width:80px; height:80px; }
                            .checkmark { font-size:36px; }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="brand">🌿 마리팜</div>
                        <div class="success-icon"><div class="checkmark">✓</div></div>
                        <h1 class="title">인증이 완료되었습니다!</h1>
                        <p class="message">이메일 인증이 성공적으로 완료되었습니다.<br>이제 마리팜의 모든 서비스를 이용하실 수 있습니다.</p>
                        <div class="countdown">
                            <span>🕐</span>
                            <span id="countdown-text"><span class="countdown-number" id="countdown-number">3</span> 초 후 이 창이 자동으로 닫힙니다</span>
                        </div>
                    </div>
                    <script>
                        try { window.opener && window.opener.postMessage({ type:'EMAIL_VERIFIED' }, '*'); } catch(e) {}
                        (function () {
                            var s = 3, el = document.getElementById('countdown-number');
                            var t = setInterval(function(){ s--; if(s<=0){ clearInterval(t); try{window.close();}catch(e){} return; } if(el){ el.textContent = s; } }, 1000);
                        })();
                    </script>
                </body>
                </html>
                """;
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
        }

        String errorHtml = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>인증 실패 - 마리팜</title>
                <style>
                    * { margin:0; padding:0; box-sizing:border-box; }
                    body {
                        font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;
                        background:linear-gradient(135deg,#fef2f2 0%,#fee2e2 100%);
                        min-height:100vh; display:flex; align-items:center; justify-content:center; padding:20px;
                    }
                    .container {
                        background:white; border-radius:16px; box-shadow:0 8px 32px rgba(0,0,0,.1);
                        padding:48px 40px; text-align:center; max-width:480px; width:100%; border-top:4px solid #ef4444;
                    }
                    .error-icon { width:80px; height:80px; background:#ef4444; border-radius:50%; display:flex; align-items:center; justify-content:center; margin:0 auto 24px; }
                    .title { font-size:24px; font-weight:700; color:#1f2937; margin-bottom:16px; }
                    .message { font-size:16px; color:#6b7280; line-height:1.6; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="error-icon"><span style="font-size:36px; color:white;">✕</span></div>
                    <h1 class="title">인증에 실패했습니다</h1>
                    <p class="message">토큰이 유효하지 않거나 만료되었습니다.<br>새로운 인증 이메일을 요청해 주세요.</p>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body(errorHtml);
    }

    // 폴링: 상태 조회 (변경 없음)
    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> status(@RequestParam String email) {
        boolean v = memberService.isEmailVerifiedForSignup(email);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .body(Map.of("email", email, "verified", v));
    }
}
