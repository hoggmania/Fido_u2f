<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="manage.jsp"/>
  <jsp:param name="title" value="Manage"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<div id="error">
    ${requestScope.errors["default"]}
    ${requestScope.errors["option"]}
</div>
<c:choose>
    <c:when test="${sessionScope.hasKey && sessionScope.u2fAuthenticated}">
        <c:if test="${requestScope.success}">
            ${Messages.USER_TOKEN_DELETED}
        </c:if>
        <table>
            <tr>
                <th> Action </th>
                <th> keyHandle </th>
                <th> counter </th>
                <th> hostname </th>
                <th> timestamp</th>
            </tr>
            <c:forEach items="${requestScope.registrations}" var="registration">
                <tr>
                    <td>
                        <form method="post" action="manage">
                            <input type="hidden" name="keyHandle" value="${registration["keyHandle"]}">
                            <input type="submit" name="deleteToken" value="DT">
                        </form>
                    </td>
                    <td> ${registration["keyHandle"]} </td>
                    <td> ${registration["counter"]} </td>
                    <td> ${registration["hostname"]} </td>
                    <td> ${registration["timestamp"]}</td>
                </tr>
            </c:forEach>
            <tr>
                <td>
                    <form method="post" action="u2fRegister">
                        <input type="submit" name="addToken" value="AT">
                    </form>
                    <form method="get" action="ajaxU2FRegistration">
                        <input type="submit" name="ajaxAddToken" value="AAT">
                    </form>
                </td>
            </tr>
        </table>
    </c:when>
    <c:otherwise>
        <script> window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/u2fAuthenticate?from'+window.location.href;</script>
    </c:otherwise>
</c:choose>
</body>
</html>
