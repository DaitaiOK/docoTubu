<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>どこつぶ</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <c:choose>
        <c:when test="${not empty sessionScope.loginUser}">
            <div class="container" style="text-align: center;">
                <h2 style="color: #2ed573;">ログイン成功</h2>
                <p>ようこそ、<c:out value="${sessionScope.loginUser.name}" />さん</p>
                <a href="Main" class="btn btn-purple">メイン画面へ</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="container" style="text-align: center;">
                <h2 style="color: #ff4757;">ACCESS DENIED</h2>
                <p style="margin-bottom: 30px;">ログインに失敗しました。<br>パスワードが間違っています。</p>
                <a href="index.jsp" class="btn btn-purple" style="display: inline-block; width: auto;">ログイン画面へ戻る</a>
            </div>
        </c:otherwise>
    </c:choose>
</body>
</html>
