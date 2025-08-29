<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>병해충 진단 결과</title>
  <!-- 공통 스타일 -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AIservice.css" />
  <script defer src="${pageContext.request.contextPath}/js/aiService.js"></script>
</head>
<body>
 <%@ include file="/WEB-INF/views/include/nav.jsp" %>


  <section class="board-layout section" style="padding-top:2rem;">
    <div class="board-container">
      <!-- 좌측 사이드바 -->
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">AI 추천서비스</h3>
          <ul class="sidebar-menu">
            <li><a href="${pageContext.request.contextPath}/ai/plantRecommend.do">식물 추천 서비스</a></li>
            <li><a href="${pageContext.request.contextPath}/ai/environment.do">최적화 환경 서비스</a></li>
            <li class="active"><a href="${pageContext.request.contextPath}/ai/diagnosis.do">병해충 진단 서비스</a></li>
          </ul>
        </nav>
      </aside>

      <!-- 우측 콘텐츠 -->
      <main class="board-content">
        <div class="board-card">
          <h2 class="upload-title">진단 결과</h2>

          <!-- 에러 메시지 -->
          <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
              <c:out value="${error}"/>
            </div>
          </c:if>

          <!-- 값이 하나도 안 넘어온 경우(직접 진입/새 세션 등) -->
          <c:if test="${empty error and empty plantAndDisease and empty preview}">
            <div class="alert alert-warning" role="alert">
              결과 데이터가 없습니다. 이미지를 업로드해 주세요.
            </div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ai/diagnosis.do">← 업로드 페이지로</a>
          </c:if>

          <!-- 정상 결과 -->
          <c:if test="${not empty plantAndDisease or not empty preview}">
            <div class="result-grid">
              <!-- 좌: 미리보기 -->
              <div class="result-left">
                <div class="result-preview">
                  <img src="${preview}" alt="preview" class="preview-img"
                       onerror="this.style.display='none'"/>
                </div>
              </div>

              <!-- 우: 결과 카드 -->
              <div class="result-right">
                <div class="result-card">
                  <div class="result-row">
                    <span class="result-key">처리 상태</span>
                    <span class="result-val">
                      <c:choose>
                        <c:when test="${result eq 'success'}"><span class="badge bg-success">success</span></c:when>
                        <c:when test="${result eq 'fail'}"><span class="badge bg-danger">fail</span></c:when>
                        <c:otherwise><span class="badge bg-secondary"><c:out value="${result}"/></span></c:otherwise>
                      </c:choose>
                    </span>
                  </div>

                  <div class="result-row">
                    <span class="result-key">식물/병명</span>
                    <span class="result-val"><strong><c:out value="${plantAndDisease}"/></strong></span>
                  </div>

                  <div class="result-row">
                    <span class="result-key">신뢰도</span>
                    <span class="result-val">
                      <strong><c:out value="${confidencePct}"/>%</strong>
                    </span>
                  </div>

                  <%-- <div class="result-row">
                    <span class="result-key">Flask 저장 경로</span>
                    <span class="result-val"><code><c:out value="${filePath}"/></code></span>
                  </div> --%>

                  <div class="result-actions">
					  <a class="btn btn-outline-green" href="${pageContext.request.contextPath}/ai/diagnosis.do">
					    <svg class="ico" width="16" height="16" viewBox="0 0 24 24" aria-hidden="true">
					      <path d="M12 5v6m0 0l-3-3m3 3l3-3M5 15v2a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-2"
					            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
					    </svg>
					    다시 업로드
					  </a>
				</div>
                </div>
              </div>
            </div>

            <!-- 디버그용 RAW JSON -->
            <%-- <c:if test="${not empty raw}">
              <div class="board-card mt-3">
                <details>
                  <summary>Raw JSON 보기</summary>
                  <pre style="white-space:pre-wrap;"><c:out value="${raw}"/></pre>
                </details>
              </div>
            </c:if> --%>
          </c:if>
        </div>
      </main>
    </div>
  </section>

  <!-- 공통 스크립트 -->
  <script src="${pageContext.request.contextPath}/js/main.js"></script>
  <!-- 폴백: main.js 미로드 시 -->
  <script>
    (function(){
      if (window.showToast) return;
      const KEY='theme';
      const btn=document.querySelector('[data-dark-toggle]');
      const root=document.body;
      const saved=localStorage.getItem(KEY);
      if (saved==='dark') root.classList.add('dark-mode');
      const sync=()=>{ btn.textContent = root.classList.contains('dark-mode') ? '☀️ 라이트모드' : '🌙 다크모드'; };
      sync();
      btn.addEventListener('click', ()=>{
        const isDark=root.classList.toggle('dark-mode');
        localStorage.setItem(KEY, isDark ? 'dark' : 'light');
        sync();
      });
    })();
  </script>

  <!-- 결과 페이지 보조 스타일 -->
  <style>
    .result-grid { display:grid; grid-template-columns:1fr 1fr; gap:24px; }
    @media (max-width:992px){ .result-grid { grid-template-columns:1fr; } }
    .result-preview { background:#f7faf7; border:1px dashed #2e7d32; border-radius:16px; padding:16px; }
    .preview-img { max-width:100%; border-radius:12px; display:block; }
    .result-card { background:#fff; border:1px solid #e5e7eb; border-radius:16px; padding:16px; box-shadow:0 1px 2px rgba(0,0,0,.04); }
    .result-row { display:flex; justify-content:space-between; gap:16px; padding:10px 0; border-bottom:1px dashed #e5e7eb; }
    .result-row:last-child { border-bottom:none; }
    .result-key { color:#6b7280; }
    .result-val { color:#111827; word-break:break-all; }
    .badge { padding:.35rem .55rem; border-radius:8px; font-size:.825rem; }
    .bg-success { background:#16a34a; color:#fff; }
    .bg-danger  { background:#ef4444; color:#fff; }
    .bg-secondary{ background:#6b7280; color:#fff; }
    .result-actions { margin-top:12px; display:flex; gap:12px; }
    /* --- 결과 이미지 박스: 사진 크기에 맞게 --- */
/* ===== 결과 이미지 레이아웃 고정 ===== */

/* 왼쪽 = 이미지 박스, 오른쪽 = 카드 */
.result-grid{
  display:grid;
  grid-template-columns: 360px 1fr; /* 왼쪽 고정폭(원하면 300~420px로 조정) */
  gap:24px;
  align-items:flex-start;
}

/* 이미지 박스 */
.result-left{ align-self:flex-start; }
.result-preview{
  position:relative;
  display:inline-flex;
  align-items:center;
  justify-content:center;
  padding:12px;
  border:2px dashed #2e7d32;
  border-radius:16px;
  background:#f7faf7;
  /* 박스가 카드와 겹치지 않도록 */
  z-index:0;
}

/* ★ 업로드 페이지에서 쓰던 absolute 초기화 */
.result-preview .preview-img{
  position:static !important;  /* absolute 강제 해제 */
  top:auto !important; left:auto !important; right:auto !important; bottom:auto !important;
  display:block;
  width:auto; height:auto;
  max-width:336px;   /* = 360 - padding(12*2) */
  max-height:300px;
  object-fit:contain;
  border-radius:14px;
  box-shadow:0 2px 8px rgba(0,0,0,.08);
}

/* 반응형 */
@media (max-width: 992px){
  .result-grid{ grid-template-columns:1fr; }
  .result-preview{ width:100%; }
  .result-preview .preview-img{
    max-width:100%;
    max-height:280px;
  }
}

  </style>
</body>
</html>
