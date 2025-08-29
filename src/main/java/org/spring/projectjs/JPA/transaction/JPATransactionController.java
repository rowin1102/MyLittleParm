package org.spring.projectjs.JPA.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
// 프런트가 3000에서 돈다면 개발 중엔 허용 (운영에선 Security 설정으로 이동 권장)
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class JPATransactionController {

    private final JPATransactionService service;

    public JPATransactionController(JPATransactionService service) {
        this.service = service;
    }

    // 전체 조회
    @GetMapping
    public List<JPATransaction> getAll() {
        return service.getAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public JPATransaction getOne(@PathVariable int id) { // Long -> int 로 통일
        return service.getById(id);
    }

    // 생성
    @PostMapping
    public ResponseEntity<JPATransaction> create(@RequestBody JPATransaction body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body));
    }

    // 수정 (부분 갱신)
    @PatchMapping("/{id}")
    public JPATransaction patch(@PathVariable int id, @RequestBody JPATransaction body) {
        return service.update(id, body);
    }

    // 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    // 검색 (제목/내용)
    @GetMapping("/search")
    public List<JPATransaction> search(@RequestParam String keyword) {
        return service.searchByTitleOrContent(keyword);
    }

    // ---------------- 사진 전용 엔드포인트 ----------------

    // 업로드: form-data (file)
    @PostMapping(value = "/{id}/photo/{slot}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JPATransaction uploadPhoto(
            @PathVariable int id,
            @PathVariable int slot,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        if (slot < 1 || slot > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slot은 1~3만 허용");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file이 비어있습니다");
        }

        Path dir = Path.of("uploads", "transactions", String.valueOf(id));
        Files.createDirectories(dir);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String cleanName = file.getOriginalFilename() == null ? "unknown"
                : file.getOriginalFilename().replaceAll("[\\\\/:*?\"<>|]", "_");
        String saveName = ts + "_" + (int) (Math.random() * 9000 + 1000) + "_" + cleanName;

        Path target = dir.resolve(saveName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return service.updatePhoto(id, slot, saveName);
    }

    // 이름만 직접 세팅 (파일 업로드 없이 경로/파일명 교체)
    @PatchMapping("/{id}/photo/{slot}")
    public JPATransaction setPhotoName(
            @PathVariable int id,
            @PathVariable int slot,
            @RequestParam("name") String name
    ) {
        if (slot < 1 || slot > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slot은 1~3만 허용");
        }
        return service.updatePhoto(id, slot, name);
    }

    // 해당 슬롯 사진 제거
    @DeleteMapping("/{id}/photo/{slot}")
    public JPATransaction clearPhoto(@PathVariable int id, @PathVariable int slot) {
        if (slot < 1 || slot > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slot은 1~3만 허용");
        }
        return service.clearPhoto(id, slot);
    }
}
