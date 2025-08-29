package org.spring.projectjs.JPA.transaction;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class JPATransactionService {

    private final JPATransactionRepository transactionRepository;

    public JPATransactionService(JPATransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 전체 조회
    public List<JPATransaction> getAll() {
        return transactionRepository.findAll();
    }

    // 생성
    public JPATransaction create(JPATransaction tx) {
        if (tx.getTransactionDate() == null) tx.setTransactionDate(new Date());
        if (tx.getTransactionIsTrans() == '\u0000') tx.setTransactionIsTrans('N'); // 기본값
        return transactionRepository.save(tx);
    }

    // 단건 조회 (by PK)
    public JPATransaction getById(int transactionIdx) {
        return transactionRepository.findById(transactionIdx)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "거래가 없습니다. id=" + transactionIdx));
    }

    // 수정 (전달된 값만 반영)
    public JPATransaction update(int transactionIdx, JPATransaction updated) {
        JPATransaction existing = transactionRepository.findById(transactionIdx)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "거래가 없습니다. id=" + transactionIdx));

        if (updated.getTransactionDate() != null) {
            existing.setTransactionDate(updated.getTransactionDate());
        }
        if (updated.getTransactionIsTrans() != '\u0000') {
            existing.setTransactionIsTrans(updated.getTransactionIsTrans()); // 'Y'/'N'
        }
        if (updated.getTransactionPrice() != 0) { // 0으로 세팅해야 한다면 로직 조정 필요
            existing.setTransactionPrice(updated.getTransactionPrice());
        }
        if (updated.getTransactionTitle() != null && !updated.getTransactionTitle().isEmpty()) {
            existing.setTransactionTitle(updated.getTransactionTitle());
        }
        if (updated.getTransactionContent() != null && !updated.getTransactionContent().isEmpty()) {
            existing.setTransactionContent(updated.getTransactionContent());
        }

        if (updated.getOfile1() != null) existing.setOfile1(updated.getOfile1());
        if (updated.getOfile2() != null) existing.setOfile2(updated.getOfile2());
        if (updated.getOfile3() != null) existing.setOfile3(updated.getOfile3());

        return existing; // dirty checking
    }

    // 삭제
    public void delete(int transactionIdx) {
        if (!transactionRepository.existsById(transactionIdx)) {
            throw new ResponseStatusException(NOT_FOUND, "거래가 없습니다. id=" + transactionIdx);
        }
        transactionRepository.deleteById(transactionIdx);
    }

    // 검색 (제목/내용)
    public List<JPATransaction> searchByTitleOrContent(String keyword) {
        return transactionRepository
                .findByTransactionTitleContainingIgnoreCaseOrTransactionContentContainingIgnoreCase(keyword, keyword);
    }

    // 사진 파일명 교체
    public JPATransaction updatePhoto(int transactionIdx, int slot, String newName) {
        JPATransaction e = getById(transactionIdx);
        switch (slot) {
            case 1 -> e.setOfile1(newName);
            case 2 -> e.setOfile2(newName);
            case 3 -> e.setOfile3(newName);
            default -> throw new IllegalArgumentException("slot은 1~3만 허용");
        }
        return e;
    }

    // 사진 비우기
    public JPATransaction clearPhoto(int transactionIdx, int slot) {
        return updatePhoto(transactionIdx, slot, null);
    }
}
