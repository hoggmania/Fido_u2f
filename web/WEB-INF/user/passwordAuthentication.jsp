<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
    <jsp:param name="id" value="passwordAuthentication.jsp"/>
    <jsp:param name="title" value="Password authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>


<c:choose>
    <c:when test="${requestScope.success}">
        <c:if test="${!empty(requestScope.from)}">
            <script>
                window.location.href = "${requestScope.from}";
            </script>
        </c:if>
        <script>
            window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/index';
        </script>
    </c:when>
    <c:when test="${!empty(sessionScope.username)}">
        ${Messages.ALREADY_AUTHENTICATED}
    </c:when>
    <c:otherwise>
        <div id="error">
                ${requestScope.errors["default"]}
        </div>
        <div id="from">
            ${requestScope.from}
        </div>
        <form method="post" action="authentication">
            <label for="username">Username : </label>
            <input type="text" name="username" id="username" placeholder="username" value="${requestScope.username}" required pattern="[A-Za-z0-9_@.]{4,16}" title="A-Z a-z 0-9 _@. {4,16}"/>
                ${requestScope.errors["username"]}
            <label for="password">Password : </label>
            <input type="password" name="password" id="password" placeholder="******" required pattern="[A-Za-z0-9]{4,16}" title="A-Z a-z 0-9 {4,16}"/>
                ${requestScope.errors["password"]}
            <input type="hidden" name="from" value="${param.from}">
            <input type="submit" value="Sign in !" />
        </form>
    </c:otherwise>
</c:choose>



</body>
</html>
