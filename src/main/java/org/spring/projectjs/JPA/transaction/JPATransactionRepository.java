package org.spring.projectjs.JPA.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPATransactionRepository extends JpaRepository<JPATransaction, Integer> {

    // 리스트 반환형
    List<JPATransaction>
    findByTransactionTitleContainingIgnoreCaseOrTransactionContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword
    );

    // 페이징 지원형 (원하면)
    Page<JPATransaction>
    findByTransactionTitleContainingIgnoreCaseOrTransactionContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable
    );
}
