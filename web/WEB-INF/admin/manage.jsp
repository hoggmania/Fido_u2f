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
<c:if test="${requestScope.success}">
    <div id="info">${requestScope.message}</div>
</c:if>
<c:if test="${!requestScope.success}">
    <div id="error">${requestScope.errors}</div>
</c:if>
<c:choose>
    <c:when test="${!sessionScope.hasKey}">
        <div id="error">
            You have to register a token
        </div>
    </c:when>
    <c:otherwise>
        <table>
            <tr>
                <th> Actions </th>
                <th> Username </th>
                <th> Change password</th>
                <th> KeyHandle </th>
                <th> Public Key </th>
                <th> Counter </th>
                <th> Attestation Certificate </th>
                <th> Hostname</th>
                <th> TimeStamp </th>
            </tr>
            <c:forEach items="${requestScope.users}" var="user" >
                <tr <c:if test="${user['suspended']}">style="background-color: red" </c:if>>
                <td>
                    <form method="post" action="adminManage">
                        <input type="hidden" name="username" value="${user["username"]}">
                        <input type="submit" name="deleteUser" value="D">
                    </form>
                    <form method="post" action="adminManage">
                        <input type="hidden" name="username" value="${user["username"]}">
                        <input type="submit" name="suspendUser" value="S">
                    </form>
                    <form method="post" action="adminManage">
                        <input type="hidden" name="username" value="${user["username"]}">
                        <input type="submit" name="addToken" value="AT">
                    </form>
                    <form method="post" action="adminManage">
                        <input type="hidden" name="username" value="${user["username"]}">
                        <input type="submit" name="ajax" value="AAT">
                    </form>
                </td>
                <td> ${user['username']} </td>
                <td>
                    <form method="post" action="adminManage">
                        <input type="hidden" name="username" value="${user["username"]}">
                        <input type="password" name="password">
                        <input type="submit" name="change" value="c">
                    </form>
                </td>

                <c:forEach items="${user['registrations']}" var="registration">
                    <tr <c:if test="${registration['suspended']}">style="background-color: yellow" </c:if>>
                        <td>
                            <form method="post" action="adminManage">
                                <input type="hidden" name="username" value="${user["username"]}">
                                <input type="hidden" name="keyHandle" value="${registration["keyHandle"]}">
                                <input type="submit" name="deleteToken" value="DT">
                            </form>
                            <form method="post" action="adminManage">
                                <input type="hidden" name="username" value="${user["username"]}">
                                <input type="hidden" name="keyHandle" value="${registration["keyHandle"]}">
                                <input type="submit" name="suspendToken" value="ST">
                            </form>

                        </td>
                        <td></td>
                        <td></td>
                        <td> ${registration["keyHandle"]} </td>
                        <td> ${registration["publicKey"]}</td>
                        <td> ${registration["counter"]} </td>
                        <td> ${registration["certificate"]}</td>
                        <td> ${registration["hostname"]} </td>
                        <td> ${registration["timestamp"]}</td>
                    </tr>
                </c:forEach>
                </tr>
            </c:forEach>

        </table>
    </c:otherwise>
</c:choose>
${requestScope.errors}


</body>
</html>
