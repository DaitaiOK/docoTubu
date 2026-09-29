<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>どこつぶ - Register</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>REGISTER</h1>
        <p style="text-align: center; color: var(--text-secondary); margin-bottom: 30px;">新しいユーザーを登録します</p>

        <%-- 未入力・登録失敗時のエラーメッセージ --%>
        <c:if test="${not empty requestScope.errorMsg}">
            <p style="color: #ff4757; text-align: center; text-shadow: 0 0 5px #ff4757;"><c:out value="${requestScope.errorMsg}" /></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post" autocomplete="off">
            <div class="form-group">
                <input type="text" name="name" placeholder="ユーザー名" maxlength="100" value="<c:out value='${param.name}' />" required>
            </div>
            <div class="form-group">
                <input type="password" name="pass" placeholder="パスワード" maxlength="255" required>
            </div>
            <input type="submit" value="ユーザー登録">
        </form>

        <p style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/index.jsp">ログイン画面へ戻る</a>
        </p>
    </div>
</body>
</html>
