<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>거래게시판 - 결제</title>
    <c:set var="ctx" value="${pageContext.request.contextPath}" />
    <link rel="stylesheet" href="${ctx}/css/main.css" />
    <link rel="stylesheet" href="${ctx}/css/transactionPayment.css" />
</head>
<body>

    <%@ include file="../include/nav.jsp" %>

    <section class="transaction-layout section">
        <!-- 📌 왼쪽 사이드바 -->
        <aside class="transaction-sidebar">
            <h3>거래 지역</h3>
            <div class="map-box">
                <div class="map-placeholder">지도 영역</div>
            </div>
        </aside>

        <!-- 📌 결제 메인 -->
        <div class="transaction-content">
            <h2 class="page-title">결제 정보 입력</h2>

            <form method="post" action="${ctx}/transaction/processPayment.do" class="payment-form">
                <!-- 고객 기본 정보 -->
                <label for="name">이름</label>
                <input type="text" id="name" name="name" required value="${dto.writer}" />

                <label for="amount">결제 금액 (₩)</label>
                <input type="number" id="amount" name="amount" value="${dto.transaction_price}" required />

                <!-- 결제 수단 선택 -->
                <label for="paymentMethod">결제 수단</label>
                <select id="paymentMethod" name="paymentMethod" onchange="togglePaymentSection()" required>
                    <option value="">-- 선택하세요 --</option>
                    <option value="bank">무통장입금</option>
                    <option value="card">신용카드</option>
                </select>

                <!-- 무통장입금 섹션 -->
                <div id="bankSection" class="payment-section" style="display:none;">
                    <label for="bankName">입금 은행</label>
                    <select id="bankName" name="bankName">
                        <option value="신한은행">신한은행</option>
                        <option value="국민은행">국민은행</option>
                        <option value="우리은행">우리은행</option>
                        <option value="하나은행">하나은행</option>
                    </select>

                    <label for="depositor">입금자명</label>
                    <input type="text" id="depositor" name="depositor" />
                </div>

                <!-- 신용카드 섹션 -->
                <div id="cardSection" class="payment-section" style="display:none;">
                    <label for="cardNumber">카드 번호</label>
                    <input type="text" id="cardNumber" name="cardNumber" placeholder="1234-5678-1234-5678" />

                    <label for="expiry">만료일 (MM/YY)</label>
                    <input type="text" id="expiry" name="expiry" placeholder="12/26" />

                    <label for="cvc">CVC</label>
                    <input type="text" id="cvc" name="cvc" placeholder="123" />
                </div>

                <input type="hidden" name="transaction_idx" value="${dto.transaction_idx}" />

                <button type="submit" class="btn-submit">💳 결제하기</button>
            </form>
        </div>
    </section>

    <script>
        function togglePaymentSection() {
            const method = document.getElementById("paymentMethod").value;
            document.getElementById("bankSection").style.display = (method === "bank") ? "block" : "none";
            document.getElementById("cardSection").style.display = (method === "card") ? "block" : "none";
        }
    </script>

</body>
</html>
