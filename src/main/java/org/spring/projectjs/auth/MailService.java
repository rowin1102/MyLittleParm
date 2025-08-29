package org.spring.projectjs.auth;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:}")
    private String from;

    private static final String EMAIL_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>이메일 인증</title>
        </head>
        <body style="margin:0; padding:0; background-color:#f8f9fa; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
          <table role="presentation" style="width:100%; max-width:600px; margin:0 auto; background-color:#fff; border-radius:12px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
            <tr>
              <td style="background:linear-gradient(135deg,#8FBC8F 0%,#7AA87A 100%); padding:40px 30px; text-align:center;">
                <h1 style="margin:0; color:#fff; font-size:28px; font-weight:600; letter-spacing:-0.5px;">🌿 마리팜</h1>
                <p style="margin:8px 0 0 0; color:rgba(255,255,255,0.9); font-size:16px;">이메일 인증이 필요합니다</p>
              </td>
            </tr>

            <tr>
              <td style="padding:40px 30px;">
                <div style="text-align:center; margin-bottom:32px;">
                  <div style="width:80px; height:80px; background:linear-gradient(135deg,#8FBC8F,#7AA87A); border-radius:50%; margin:0 auto 20px auto; display:flex; align-items:center; justify-content:center;">
                    <span style="font-size:36px;">📧</span>
                  </div>
                  <h2 style="margin:0 0 16px 0; color:#333; font-size:24px; font-weight:600;">환영합니다!</h2>
                  <p style="margin:0; color:#666; font-size:16px; line-height:1.6;">
                    마리팜 서비스를 이용하기 위해<br>
                    아래 버튼을 클릭하여 이메일 인증을 완료해 주세요.
                  </p>
                </div>

                <div style="text-align:center; margin:32px 0;">
                  <a href="${VERIFY_LINK}"
                     style="display:inline-block; padding:16px 32px; background:linear-gradient(135deg,#8FBC8F,#7AA87A);
                            color:#fff; text-decoration:none; border-radius:8px; font-size:16px; font-weight:600;
                            transition:all .2s ease; box-shadow:0 4px 12px rgba(143,188,143,.4);">
                    ✅ 이메일 인증하기
                  </a>
                </div>

                <div style="background-color:#f0fdf4; border:1px solid #dcfce7; border-radius:8px; padding:20px; margin:32px 0;">
                  <div style="display:flex; align-items:flex-start; gap:12px;">
                    <span style="font-size:20px;">⏰</span>
                    <div>
                      <p style="margin:0 0 8px 0; color:#6B8E6B; font-weight:600; font-size:14px;">중요 안내</p>
                      <p style="margin:0; color:#666; font-size:14px; line-height:1.5;">
                        이 인증 링크는 <strong>24시간 동안만 유효</strong>합니다.<br>
                        시간이 초과된 경우 새로운 인증 이메일을 요청해 주세요.
                      </p>
                    </div>
                  </div>
                </div>

                <div style="margin-top:24px; padding-top:24px; border-top:1px solid #eee;">
                  <p style="margin:0 0 12px 0; color:#999; font-size:13px; text-align:center;">
                    버튼이 작동하지 않는 경우 아래 링크를 복사하여 브라우저에 붙여넣어 주세요:
                  </p>
                  <div style="background-color:#f8f9fa; border-radius:6px; padding:12px; word-break:break-all;
                              font-size:12px; color:#666; text-align:center;">
                    ${VERIFY_LINK}
                  </div>
                </div>
              </td>
            </tr>

            <tr>
              <td style="background-color:#f8f9fa; padding:24px 30px; text-align:center; border-top:1px solid #eee;">
                <p style="margin:0 0 8px 0; color:#999; font-size:13px;">
                  이 이메일은 마리팜 회원가입 과정에서 자동 발송된 메일입니다.
                </p>
                <p style="margin:0; color:#999; font-size:13px;">
                  문의사항이 있으시면 고객센터로 연락해 주세요.
                </p>
              </td>
            </tr>
          </table>

          <style>
            @media only screen and (max-width: 600px) {
              table { width:100% !important; margin:0 !important; border-radius:0 !important; }
              td { padding:20px !important; }
              h1 { font-size:24px !important; }
              h2 { font-size:20px !important; }
            }
          </style>
        </body>
        </html>
        """;

    /**
     * 기존과 동일한 API이지만, 이제 비동기(백그라운드)로 전송됩니다.
     * 컨트롤러/서비스에서 이 메서드를 호출하면 즉시 리턴되어
     * 화면에는 곧바로 "인증 메일을 전송했습니다" 메시지를 띄울 수 있습니다.
     */
    @Async("mailExecutor")
    public void sendEmailVerification(String to, String verifyLink) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setTo(to);
            if (from != null && !from.isBlank()) {
                helper.setFrom(from);
                // 필요하면 회신 주소도 동일하게
                helper.setReplyTo(from);
            }
            helper.setSubject("[마리팜] 이메일 인증 안내");

            // 링크 치환
            String html = EMAIL_TEMPLATE.replace("${VERIFY_LINK}", verifyLink);
            helper.setText(html, true);

            mailSender.send(mime);
            log.info("Email verification queued -> to={}, link={}", to, verifyLink);
        } catch (Exception e) {
            // 비동기 작업이므로 예외는 로깅만 (호출측 흐름은 막지 않음)
            log.error("메일 전송 실패 to={}", to, e);
        }
    }

    /**
     * 필요 시 동기(Blocking) 전송이 꼭 필요한 곳에서 사용할 수 있도록 보조 메서드도 제공.
     * 사용하지 않으면 삭제해도 됩니다.
     */
    public void sendEmailVerificationBlocking(String to, String verifyLink) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setTo(to);
            if (from != null && !from.isBlank()) helper.setFrom(from);
            helper.setSubject("[마리팜] 이메일 인증 안내");
            helper.setText(EMAIL_TEMPLATE.replace("${VERIFY_LINK}", verifyLink), true);
            mailSender.send(mime);
        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }
}
