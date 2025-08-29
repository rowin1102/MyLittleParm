package org.spring.projectjs.jdbc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.spring.projectjs.auth.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

  @Autowired private IRegistService iRegistService;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private MailService mailService;

  @Value("${app.base-url}")
  private String baseUrl;

  private static final SecureRandom RNG = new SecureRandom();

  private static String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  /** 목적 접두사가 포함된 새 토큰 생성 (예: SU.xxxxx, UE.xxxxx, FI.xxxxx, RP.xxxxx) */
  private String newToken(String purpose) {
    byte[] buf = new byte[32]; // 256-bit
    RNG.nextBytes(buf);
    String rnd = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    return purpose + "." + rnd;
  }

  /** email + domain 입력을 분리해두었을 때 보정 (회원가입 시 사용) */
  private String resolveEmail(MemberDTO dto) {
    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
      if (dto.getEmail().contains("@")) return dto.getEmail();
      if (dto.getDomain() != null && !dto.getDomain().isBlank()) {
        return dto.getEmail() + "@" + dto.getDomain();
      }
    }
    return dto.getEmail();
  }

  /** full-email을 local/domain으로 분해 */
  private static final class EmailParts {
    final String local; final String domain;
    EmailParts(String local, String domain) { this.local = local; this.domain = domain; }
  }
  private static EmailParts splitEmail(String full) {
    String s = normalizeEmail(full);
    if (s == null) return new EmailParts("", "");
    int at = s.indexOf('@');
    if (at < 0) return new EmailParts(s.trim(), "");
    return new EmailParts(s.substring(0, at).trim(), s.substring(at + 1).trim());
  }

  /* ===================== 사전 이메일 인증(공용) ===================== */

  /** 목적별 공용 메일 발송 (SU/UE/FI/RP 공용) */
  @Transactional
  public void sendEmailVerification(String email, String purpose) {
    String norm = normalizeEmail(email);
    if (norm == null || norm.isBlank() || !norm.contains("@"))
      throw new IllegalArgumentException("이메일을 올바르게 입력해 주세요.");

    String token = newToken(purpose);
    iRegistService.upsertEmailVerifyTemp(norm, token); // EMAIL_VERIFY_TEMP.EMAIL 은 full-email 저장

    String link = baseUrl + "/api/auth/email/verify?token=" +
                  URLEncoder.encode(token, StandardCharsets.UTF_8);
    mailService.sendEmailVerification(norm, link);
  }

  /** 회원가입용 포장 */
  @Transactional
  public void sendEmailVerificationForSignup(String email) {
    sendEmailVerification(email, "SU");
  }

  @Transactional
  public boolean verifyEmailTokenForSignup(String token) {
    EmailVerifyTempDTO row = iRegistService.findTempByToken(token); // VERIFIED='N' && not expired
    if (row == null) return false;
    iRegistService.markTempVerified(normalizeEmail(row.getEmail()));
    return true;
  }

  @Transactional(readOnly = true)
  public boolean isEmailVerifiedForSignup(String email) {
    String flag = iRegistService.findTempVerifiedFlag(normalizeEmail(email));
    return "Y".equalsIgnoreCase(flag);
  }

  /** 목적 일반화 버전(아이디찾기/비번재설정 등) */
  @Transactional(readOnly = true)
  public boolean isEmailVerified(String email) {
    return isEmailVerifiedForSignup(email);
  }

  /* ===================== 회원가입(사전 인증 필수) ===================== */

  @Transactional
  public void register(MemberDTO memberDTO) {
    String fullEmail = normalizeEmail(resolveEmail(memberDTO));

    if (!isEmailVerifiedForSignup(fullEmail)) {
      throw new IllegalStateException("이메일 인증이 필요합니다.");
    }

    memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
    memberDTO.setEmail_verified("Y");
    iRegistService.insert(memberDTO);

    iRegistService.deleteTemp(fullEmail);
  }

  /* ===================== 아이디 찾기/비밀번호 재설정 ===================== */

  /** 아이디 찾기: 인증 메일 발송(FI) */
  @Transactional
  public void requestFindIdByEmail(String email) {
    sendEmailVerification(email, "FI");
  }

  /** 이메일 인증 후, 해당 이메일(local+domain)로 가입한 아이디 목록 조회 */
  @Transactional(readOnly = true)
  public List<String> findUserIdsByEmail(String email) {
    EmailParts p = splitEmail(email);
    return iRegistService.findUserIdsByLocalAndDomain(p.local, p.domain);
  }

  /** 비밀번호 재설정: 인증 메일 발송(RP) — userId + (email,domain) 매칭 검증 */
  @Transactional
  public void requestResetPassword(String userId, String email) {
    EmailParts p = splitEmail(email);
    if (!iRegistService.existsByUserIdAndDomain(userId, p.local, p.domain)) {
      throw new IllegalArgumentException("일치하는 계정이 없습니다.");
    }
    sendEmailVerification(p.local + "@" + p.domain, "RP");
  }

  /** 비밀번호 재설정 확정 */
  @Transactional
  public void resetPassword(String userId, String email, String newPassword) {
    EmailParts p = splitEmail(email);
    String full = p.local + "@" + p.domain;

    if (!isEmailVerified(full)) {
      throw new IllegalStateException("이메일 인증이 필요합니다.");
    }
    if (!iRegistService.existsByUserIdAndDomain(userId, p.local, p.domain)) {
      throw new IllegalArgumentException("일치하는 계정이 없습니다.");
    }

    String encoded = passwordEncoder.encode(newPassword);
    int updated = iRegistService.updatePassword(userId, encoded);
    if (updated != 1) throw new IllegalStateException("비밀번호 변경 실패");

    iRegistService.deleteTemp(full);
  }

  /* ===================== 조회 ===================== */

  public MemberDTO findByUserId(String userId) {
    return iRegistService.selectByUserId(userId);
  }

  public int checkDuplicateId(String userId) {
    return iRegistService.checkDuplicateId(userId);
  }

  public int checkDuplicateNickname(String nickname) {
    return iRegistService.checkDuplicateNickname(nickname);
  }
}
