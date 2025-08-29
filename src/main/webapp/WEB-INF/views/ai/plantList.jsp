<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>식물 전체 리스트</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css"/>

  <!-- 이 페이지 전용 스타일 -->
  <style>
    /* 카드 여백 조금 여유 */
    #plantListPage .board-card h2{margin-bottom:14px}

    /* 스크롤 영역 */
    #plantListPage .table-scroll{
      max-height:72vh; overflow:auto; border-radius:14px; background:#fff;
      box-shadow:0 8px 22px rgba(0,0,0,.06)
    }

    /* 테이블 기본 */
    #plantListPage .board-table{
      width:100%; border-collapse:separate; border-spacing:0; background:transparent
    }
    /* 헤더 고정 + 살짝 유리효과 */
    #plantListPage .board-table thead th{
      position:sticky; top:0; z-index:2;
      background:#f7faf7cc; backdrop-filter:saturate(140%) blur(4px);
      border-bottom:1px solid #e3efe6; text-align:center; font-weight:800;
      padding:12px 14px;
    }
    /* 바디 셀 */
    #plantListPage .board-table tbody td{
      padding:11px 14px; border-bottom:1px solid #eef2f7; vertical-align:middle; line-height:1.35
    }
    /* 줄무늬 + hover */
    #plantListPage .board-table tbody tr:nth-child(odd){ background:#fbfefb }
    #plantListPage .board-table tbody tr:hover{ background:#f2fbf5 }

    /* 정렬/서체 */
    #plantListPage .td-idx{ text-align:center; font-variant-numeric:tabular-nums; width:74px }
    #plantListPage .td-name{ font-weight:700; color:#0f172a }
    #plantListPage .td-center{ text-align:center }
    #plantListPage .td-num{ text-align:right; font-variant-numeric:tabular-nums }

    /* 분류 필칩 */
    #plantListPage .chip{
      display:inline-block; padding:.22rem .6rem; border-radius:999px;
      background:#f0fdf4; border:1px solid rgba(34,197,94,.25);
      color:#065f46; font-weight:700; font-size:.86rem; white-space:nowrap
    }

    /* 난이도 배지 (1 쉬움 → 5 어려움) */
    #plantListPage .diff{ display:inline-flex; align-items:center; gap:6px; font-weight:800 }
    #plantListPage .dot{ width:8px; height:8px; border-radius:999px; background:#a3a3a3; box-shadow:0 0 0 2px #fff inset }
    #plantListPage .d1 .dot{ background:#36d399 }   /* 녹색 */
    #plantListPage .d2 .dot{ background:#86efac }
    #plantListPage .d3 .dot{ background:#fde047 }   /* 노랑 */
    #plantListPage .d4 .dot{ background:#fb923c }   /* 주황 */
    #plantListPage .d5 .dot{ background:#f87171 }   /* 빨강 */

    /* 긴 텍스트 말줄임 */
    #plantListPage .ellipsis{ max-width:340px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; display:inline-block }

    /* 반응형: 좁을 때 일부 칼럼 폭 조정 */
    @media (max-width: 900px){
      #plantListPage .ellipsis{ max-width:220px }
      #plantListPage .td-idx{ width:56px }
    }

    /* 다크모드 대응 */
    body.dark-mode #plantListPage .table-scroll{ background:#1f2937; box-shadow:0 8px 24px rgba(0,0,0,.35) }
    body.dark-mode #plantListPage .board-table thead th{ background:#0f172acc; border-bottom-color:#2a2f3a; color:#e5e7eb }
    body.dark-mode #plantListPage .board-table tbody tr:nth-child(odd){ background:#0b1220 }
    body.dark-mode #plantListPage .board-table tbody tr:hover{ background:#10192c }
    body.dark-mode #plantListPage .board-table tbody td{ color:#e5e7eb; border-bottom-color:#2a2f3a }
    body.dark-mode #plantListPage .chip{ background:#052e1a; border-color:#1b5e41; color:#b7f7d3 }
  </style>
</head>
<body id="plantListPage">
  <%@ include file="/WEB-INF/views/include/nav.jsp" %>

  <section class="board-layout section" style="padding-top:2rem;">
    <div class="board-container">
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">식물 데이터</h3>
          <ul class="sidebar-menu">
            <li class="active"><a href="${pageContext.request.contextPath}/plantList.do">식물 목록</a></li>
          </ul>
        </nav>
      </aside>

      <main class="board-content">
        <div class="board-card">
          <h2>식물 전체 리스트</h2>

          <div class="table-scroll" role="region" aria-label="식물 리스트 스크롤 영역">
            <table class="board-table">
              <thead>
              <tr>
                <th width="8%">번호</th>
                <th>식물명</th>
                <th width="22%">영문명</th>
                <th width="16%">분류</th>
                <th width="12%">난이도</th>
                <th width="18%">적정온도(℃)</th>
                <th width="14%">광량(lux)</th>
              </tr>
              </thead>
              <tbody>
              <c:choose>
                <c:when test="${empty list}">
                  <tr><td colspan="7" class="td-center">등록된 식물이 없습니다.</td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="p" items="${list}" varStatus="st">
                    <tr>
                      <td class="td-idx">${st.index + 1}</td>

                      <td class="td-name">
                        <span class="ellipsis"><c:out value="${p.name}"/></span>
                      </td>

                      <td class="td-center">
                        <span class="ellipsis">
                          <c:out value="${p.englishName != null ? p.englishName : '-'}"/>
                        </span>
                      </td>

                      <td class="td-center">
                        <c:choose>
                          <c:when test="${not empty p.series}">
                            <span class="chip"><c:out value="${p.series}"/></span>
                          </c:when>
                          <c:otherwise>-</c:otherwise>
                        </c:choose>
                      </td>

                      <td class="td-center">
                        <c:choose>
                          <c:when test="${p.difficulty != null && p.difficulty != 0}">
                            <span class="diff d${p.difficulty}">
                              <i class="dot"></i>${p.difficulty}
                            </span>
                          </c:when>
                          <c:otherwise>-</c:otherwise>
                        </c:choose>
                      </td>

                      <td class="td-center">
                        <c:choose>
                          <c:when test="${(p.minTemp != null && p.minTemp != 0) || (p.maxTemp != null && p.maxTemp != 0)}">
                            <c:out value="${p.minTemp != null ? p.minTemp : '-'}"/> ~
                            <c:out value="${p.maxTemp != null && p.maxTemp != 0 ? p.maxTemp : '-'}"/>
                          </c:when>
                          <c:otherwise>-</c:otherwise>
                        </c:choose>
                      </td>

                      <td class="td-num">
                        <c:choose>
                          <c:when test="${p.amountLight != null && p.amountLight != 0}">${p.amountLight}</c:when>
                          <c:otherwise>-</c:otherwise>
                        </c:choose>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  </section>

  <button id="scrollToTopBtn" class="back-to-top" aria-label="맨 위로">⬆</button>

  <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
