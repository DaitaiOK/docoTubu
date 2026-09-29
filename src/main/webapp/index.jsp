<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>どこつぶ - Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>DOCO-TUBU</h1>
        <p style="text-align: center; color: var(--text-secondary); margin-bottom: 30px;">どこにいてもつぶやけるサイバー空間</p>

        <%-- ログイン失敗・未入力時のエラーメッセージ --%>
        <c:if test="${not empty requestScope.errorMsg}">
            <p style="color: #ff4757; text-align: center; text-shadow: 0 0 5px #ff4757;"><c:out value="${requestScope.errorMsg}" /></p>
        </c:if>

        <form action="${pageContext.request.contextPath}/Login" method="post" autocomplete="off">
            <div class="form-group">
                <input type="text" name="name" placeholder="ユーザー名" required>
            </div>
            <div class="form-group">
                <input type="password" name="pass" placeholder="パスワード" required>
            </div>
            <input type="submit" value="システムにログイン">
        </form>

        <p style="text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/register">ユーザー未登録の方はこちら</a>
        </p>
    </div>
</body>
</html>
