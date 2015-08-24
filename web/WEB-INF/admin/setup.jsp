<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="setup.jsp"/>
  <jsp:param name="title" value="Setup"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

${requestScope.errors}

${requestScope.options["usersSeeDetails"]}
${requestScope.options["usersSeeDetails"] == false}

<form action="adminSetup", method="post">
    onlyNeowave
    <input name="onlyNeowave" type="radio" value="true" <c:if test="${requestScope.options[\"onlyNeowave\"]}">checked</c:if>>
    <input name="onlyNeowave" type="radio" value="false" <c:if test="${!requestScope.options[\"onlyNeowave\"]}">checked</c:if>>
    <br><br>adminReplaceUsersTokens
    <input name="adminReplaceUsersTokens" type="radio" value="true" <c:if test="${requestScope.options[\"adminReplaceUsersTokens\"]}">checked</c:if>>
    <input name="adminReplaceUsersTokens" type="radio" value="false" <c:if test="${!requestScope.options[\"adminReplaceUsersTokens\"]}">checked</c:if>>
    <br><br>userCreateAccount
    <input name="userCreateAccount" type="radio" value="true" <c:if test="${requestScope.options[\"userCreateAccount\"]}">checked</c:if>>
    <input name="userCreateAccount" type="radio" value="false" <c:if test="${!requestScope.options[\"userCreateAccount\"]}">checked</c:if>>
    <br><br>usersRegisterTheirOwnFirstToken
    <input name="usersRegisterTheirOwnFirstToken" type="radio" value="true" <c:if test="${requestScope.options[\"usersRegisterTheirOwnFirstToken\"]}">checked</c:if>>
    <input name="usersRegisterTheirOwnFirstToken" type="radio" value="false" <c:if test="${!requestScope.options[\"usersRegisterTheirOwnFirstToken\"]}">checked</c:if>>
    <br><br>usersAddNewTokens
    <input name="usersAddNewTokens" type="radio" value="true" <c:if test="${requestScope.options[\"usersAddNewTokens\"]}">checked</c:if>>
    <input name="usersAddNewTokens" type="radio" value="false" <c:if test="${!requestScope.options[\"usersAddNewTokens\"]}">checked</c:if>>
    <br><br>usersRemoveLastToken
    <input name="usersRemoveLastToken" type="radio" value="true" <c:if test="${requestScope.options[\"usersRemoveLastToken\"]}">checked</c:if>>
    <input name="usersRemoveLastToken" type="radio" value="false" <c:if test="${!requestScope.options[\"usersRemoveLastToken\"]}">checked</c:if>>
    <br><br>usersSeeDetails
    <input name="usersSeeDetails" type="radio" value="true" <c:if test="${requestScope.options[\"usersSeeDetails\"]}">checked</c:if>>
    <input name="usersSeeDetails" type="radio" value="false" <c:if test="${!requestScope.options[\"usersSeeDetails\"]}">checked</c:if>>

    <input type="submit" value="change" name="action">
</form>

</body>
</html>
