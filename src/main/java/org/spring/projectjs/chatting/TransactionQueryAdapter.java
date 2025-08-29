package org.spring.projectjs.chatting;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionQueryAdapter implements TransactionQueryPort {
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Long findWriterMemberIdxByTransactionIdx(int transactionIdx) {
    // WRITER가 USER_ID라면 MEMBER 조인해서 MEMBER_IDX 찾음
    String sql =
        "SELECT m.MEMBER_IDX " +
        "  FROM TRANSACTION t LEFT JOIN MEMBER m ON m.USER_ID = t.WRITER " +
        " WHERE t.TRANSACTION_IDX = ?";
    return jdbcTemplate.query(sql, ps -> ps.setInt(1, transactionIdx), rs -> {
      if (rs.next()) return rs.getLong(1);
      return null;
    });
  }
}