<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<main class="board-layout">
  <div class="board-container">
    <%@ include file="/WEB-INF/views/include/sidebar.jsp" %>

    <div class="board-content">
      <div class="board-card">
        <jsp:include page="${contentPage}" />
      </div>
    </div>
  </div>
</main>
