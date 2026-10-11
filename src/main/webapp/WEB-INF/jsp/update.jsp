<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>DOCO-TUBU - Edit</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>EDIT</h1>
        <p style="text-align: center; color: var(--text-secondary); margin-bottom: 30px;">つぶやきを編集します</p>

        <%-- 未入力・更新失敗時のエラーメッセージ --%>
        <c:if test="${not empty requestScope.errorMsg}">
            <p style="color: #ff4757; text-align: center; text-shadow: 0 0 5px #ff4757;"><c:out value="${requestScope.errorMsg}" /></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/updateMutter" method="post" autocomplete="off">
            <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}' />">
            <input type="hidden" name="id" value="<c:out value='${requestScope.id}' />">
            <div class="form-group">
                <textarea name="text" rows="4" maxlength="255" placeholder="いまどうしてる？" required><c:out value="${requestScope.text}" /></textarea>
            </div>
            <input type="submit" value="更新">
        </form>

        <p style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/Main">キャンセル（メイン画面へ戻る）</a>
        </p>
    </div>
</body>
</html>
