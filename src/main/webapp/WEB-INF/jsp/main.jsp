<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>DOCO-TUBU - Timeline</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>DOCO-TUBU</h1>
        
        <div class="header-info">
            <span>👤 <c:out value="${sessionScope.loginUser.name}" /> さんが接続中</span>
            <form action="Logout" method="post" style="margin: 0;">
                <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}' />">
                <button type="submit" class="btn btn-purple" style="padding: 5px 15px; width: auto; font-size: inherit;">切断 (LOGOUT)</button>
            </form>
        </div>
        
        <c:if test="${not empty requestScope.errorMsg}">
            <p style="color: #ff4757; text-align: center; text-shadow: 0 0 5px #ff4757;"><c:out value="${requestScope.errorMsg}" /></p>
        </c:if>
        
        <form action="searchMutter" method="get" autocomplete="off" style="display: flex; gap: 10px; margin-bottom: 10px;">
            <input type="text" name="keyword" value="<c:out value="${requestScope.keyword}" />" placeholder="キーワードで検索" style="margin-bottom: 0;">
            <input type="submit" value="検索" class="btn-cyan" style="width: 100px;">
        </form>
        <c:if test="${requestScope.keyword != null}">
            <p style="text-align: right; margin: 0 0 10px;"><a href="Main">検索を解除</a></p>
        </c:if>

        <form action="Main" method="post" autocomplete="off" style="display: flex; gap: 10px;">
            <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}' />">
            <input type="text" name="text" placeholder="いまどうしてる？" maxlength="255" style="margin-bottom: 0;">
            <input type="submit" value="送信" class="btn-cyan" style="width: 100px;">
        </form>
        
        <hr>
        
        <div class="timeline">
            <c:choose>
                <c:when test="${not empty requestScope.mutterList}">
                    <c:forEach var="mutter" items="${requestScope.mutterList}">
                        <%-- 削除・編集は本人の投稿にだけ表示する --%>
                        <c:set var="isOwn" value="${mutter.userId == sessionScope.loginUser.id}" />
                        <div class="mutter-card ${mutter.userName == 'AI太郎' ? 'ai-mutter' : ''}">
                            <div class="mutter-header">
                                <span class="mutter-author">
                                    ${mutter.userName == 'AI太郎' ? '🤖 ' : '👤 '}
                                    <c:out value="${mutter.userName}" />
                                </span>
                                <c:if test="${isOwn}">
                                    <form action="Delete" method="post" style="margin: 0;">
                                        <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}' />">
                                        <input type="hidden" name="id" value="${mutter.id}">
                                        <button type="submit" class="btn-delete" title="削除する">✖</button>
                                    </form>
                                </c:if>
                            </div>
                            <div class="mutter-text">
                                <c:out value="${mutter.text}" />
                            </div>
                            <%-- 編集済みの表記と編集リンク --%>
                            <c:if test="${mutter.edited or isOwn}">
                                <div class="mutter-footer">
                                    <span>
                                        <c:if test="${mutter.edited}">
                                            (編集済み - <fmt:formatDate value="${mutter.updatedAt}" pattern="yyyy/MM/dd HH:mm" />)
                                        </c:if>
                                    </span>
                                    <c:if test="${isOwn}">
                                        <a href="updateMutter?id=${mutter.id}" class="btn-edit">編集</a>
                                    </c:if>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p style="text-align: center; color: var(--text-secondary);">
                        ${requestScope.keyword != null ? '該当するつぶやきはありません。' : 'まだ通信ログはありません。'}
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- 自動更新用のJavaScript -->
    <script>
        // 3秒ごとに最新のタイムラインを裏側で取得して差し替える
        setInterval(() => {
            fetch(location.href) // 今表示しているURLに裏側でアクセス（検索中は検索結果を更新する）
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
