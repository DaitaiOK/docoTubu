<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>DOCO-TUBU - Timeline</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>DOCO-TUBU</h1>
        
        <div class="header-info">
            <span>👤 <c:out value="${sessionScope.loginUser.name}" /> さんが接続中</span>
            <a href="Logout" class="btn btn-purple" style="padding: 5px 15px; width: auto;">切断 (LOGOUT)</a>
        </div>
        
        <c:if test="${not empty requestScope.errorMsg}">
            <p style="color: #ff4757; text-align: center; text-shadow: 0 0 5px #ff4757;"><c:out value="${requestScope.errorMsg}" /></p>
        </c:if>
        
        <form action="Main" method="post" autocomplete="off" style="display: flex; gap: 10px;">
            <input type="text" name="text" placeholder="いまどうしてる？" style="margin-bottom: 0;">
            <input type="submit" value="送信" class="btn-cyan" style="width: 100px;">
        </form>
        
        <hr>
        
        <div class="timeline">
            <c:choose>
                <c:when test="${not empty requestScope.mutterList}">
                    <c:forEach var="mutter" items="${requestScope.mutterList}">
                        <div class="mutter-card ${mutter.userName == 'AI太郎' ? 'ai-mutter' : ''}">
                            <div class="mutter-header">
                                <span class="mutter-author">
                                    ${mutter.userName == 'AI太郎' ? '🤖 ' : '👤 '}
                                    <c:out value="${mutter.userName}" />
                                </span>
                                <a href="Delete?id=${mutter.id}" class="btn-delete" title="削除する">✖</a>
                            </div>
                            <div class="mutter-text">
                                <c:out value="${mutter.text}" />
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p style="text-align: center; color: var(--text-secondary);">まだ通信ログはありません。</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- 自動更新用のJavaScript -->
    <script>
        // 3秒ごとに最新のタイムラインを裏側で取得して差し替える
        setInterval(() => {
            fetch('Main') // メイン画面のURLに裏側でアクセス
                .then(response => response.text())
                .then(html => {
                    // 取得したHTMLの中から、タイムラインの部分だけを抽出
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const newTimeline = doc.querySelector('.timeline').innerHTML;
                    
                    // 現在の画面のタイムラインを新しいものに書き換える
                    const currentTimeline = document.querySelector('.timeline');
                    if (currentTimeline.innerHTML !== newTimeline) {
                        currentTimeline.innerHTML = newTimeline;
                    }
                })
                .catch(err => console.error('通信エラー:', err));
        }, 3000); // 3000ミリ秒（3秒）ごとに実行
    </script>
</body>
</html>
