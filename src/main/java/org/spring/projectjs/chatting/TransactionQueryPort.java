package org.spring.projectjs.chatting;

public interface TransactionQueryPort {
	  Long findWriterMemberIdxByTransactionIdx(int transactionIdx);
	}
