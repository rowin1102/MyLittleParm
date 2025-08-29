package org.spring.projectjs.controller;

import java.util.List;
import java.util.Map;

import org.spring.projectjs.jdbc.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recover")
public class AccountRecoveryController {

	@Autowired
	private MemberService memberService;
	
	/* -------------------- 아이디 찾기 -------------------- */

	// 1) 인증 메일 발송 (FI)
	@PostMapping(value = "/find-id/request", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findIdRequest(@RequestParam String email) {
		try {
			memberService.requestFindIdByEmail(email);
			return ResponseEntity.ok(Map.of("message", "인증메일 전송"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("message", "메일 전송 실패"));
		}
	}

	// 2) 인증 후 아이디 목록 조회
	@GetMapping(value = "/find-id", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findIdResult(@RequestParam String email) {
		if (!memberService.isEmailVerified(email)) {
			return ResponseEntity.status(409).body(Map.of("message", "이메일 인증이 필요합니다."));
		}
		List<String> ids = memberService.findUserIdsByEmail(email);
		// 보안상 마스킹 권장
		List<String> masked = ids.stream().map(AccountRecoveryController::maskId).toList();
		return ResponseEntity.ok(Map.of("userIds", masked));
	}

	private static String maskId(String id) {
		if (id == null || id.length() < 3)
			return "***";
		int show = Math.min(3, id.length());
		return id.substring(0, show) + "*".repeat(Math.max(0, id.length() - show));
	}

	/* -------------------- 비밀번호 재설정 -------------------- */

	// 1) 인증 메일 발송 (RP)
	@PostMapping(value = "/reset-password/request", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPwRequest(@RequestParam String userId, @RequestParam String email) {
		try {
			memberService.requestResetPassword(userId, email);
			return ResponseEntity.ok(Map.of("message", "인증메일 전송"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("message", "메일 전송 실패"));
		}
	}

	// 2) 새 비밀번호 저장 (이메일 인증 완료 후만 허용)
	@PostMapping(value = "/reset-password/confirm", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPwConfirm(@RequestParam String userId, @RequestParam String email,
			@RequestParam String newPassword) {
		try {
			memberService.resetPassword(userId, email, newPassword);
			return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
		} catch (IllegalStateException | IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(Map.of("message", "처리 중 오류"));
		}
	}
}
