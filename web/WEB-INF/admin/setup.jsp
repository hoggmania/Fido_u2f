<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="setup.jsp"/>
  <jsp:param name="title" value="Setup"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<div id="error">
    ${requestScope.errors["default"]}
</div>



<form action="adminSetup" method="post">

    <label for="onlyNeowave">
        ${Messages.ONLY_NEOWAVE}
        ${Messages.YES}<input id="onlyNeowave" name="onlyNeowave" type="radio" value="true" <c:if test="${requestScope.options[\"onlyNeowave\"]}">checked</c:if>>
        ${Messages.NO}<input id="onlyNeowave" name="onlyNeowave" type="radio" value="false" <c:if test="${!requestScope.options[\"onlyNeowave\"]}">checked</c:if>>
    </label>

    <label for="adminReplaceUsersTokens">
        ${Messages.ADMIN_REPLACE_USER_TOKEN}
        ${Messages.YES}<input id="adminReplaceUsersTokens" name="adminReplaceUsersTokens" type="radio" value="true" <c:if test="${requestScope.options[\"adminReplaceUsersTokens\"]}">checked</c:if>>
        ${Messages.NO}<input id="adminReplaceUsersTokens" name="adminReplaceUsersTokens" type="radio" value="false" <c:if test="${!requestScope.options[\"adminReplaceUsersTokens\"]}">checked</c:if>>
    </label>

    <label for="userCreateAccount">
        ${Messages.USER_CREATE_ACCOUNT}
        ${Messages.YES}<input id="userCreateAccount" name="userCreateAccount" type="radio" value="true" <c:if test="${requestScope.options[\"userCreateAccount\"]}">checked</c:if>>
        ${Messages.NO}<input id="userCreateAccount" name="userCreateAccount" type="radio" value="false" <c:if test="${!requestScope.options[\"userCreateAccount\"]}">checked</c:if>>
    </label>

    <label for="usersRegisterTheirOwnFirstToken">
        ${Messages.USER_REGISTER_THEIR_OWN_FIRST_TOKEN}
        ${Messages.YES}<input id="usersRegisterTheirOwnFirstToken" name="usersRegisterTheirOwnFirstToken" type="radio" value="true" <c:if test="${requestScope.options[\"usersRegisterTheirOwnFirstToken\"]}">checked</c:if>>
        ${Messages.NO}<input id="usersRegisterTheirOwnFirstToken" name="usersRegisterTheirOwnFirstToken" type="radio" value="false" <c:if test="${!requestScope.options[\"usersRegisterTheirOwnFirstToken\"]}">checked</c:if>>
    </label>

    <label for="usersAddNewTokens">
        ${Messages.USER_ADD_NEW_TOKEN}
        ${Messages.YES}<input id="usersAddNewTokens" name="usersAddNewTokens" type="radio" value="true" <c:if test="${requestScope.options[\"usersAddNewTokens\"]}">checked</c:if>>
        ${Messages.NO}<input id="usersAddNewTokens" name="usersAddNewTokens" type="radio" value="false" <c:if test="${!requestScope.options[\"usersAddNewTokens\"]}">checked</c:if>>
    </label>

    <label for="usersRemoveLastToken">
        ${Messages.USER_REMOVE_LAST_TOKEN}
        ${Messages.YES}<input id="usersRemoveLastToken" name="usersRemoveLastToken" type="radio" value="true" <c:if test="${requestScope.options[\"usersRemoveLastToken\"]}">checked</c:if>>
        ${Messages.NO}<input id="usersRemoveLastToken" name="usersRemoveLastToken" type="radio" value="false" <c:if test="${!requestScope.options[\"usersRemoveLastToken\"]}">checked</c:if>>
    </label>

    <label for="usersSeeDetails">
        ${Messages.USER_SEE_DETAILS}
        ${Messages.YES}<input id="usersSeeDetails" name="usersSeeDetails" type="radio" value="true" <c:if test="${requestScope.options[\"usersSeeDetails\"]}">checked</c:if>>
        ${Messages.NO}<input id="usersSeeDetails" name="usersSeeDetails" type="radio" value="false" <c:if test="${!requestScope.options[\"usersSeeDetails\"]}">checked</c:if>>
    </label>

    <label for="sessionInactiveExpirationTime">
        <input id="sessionInactiveExpirationTime" name="sessionInactiveExpirationTime" type="number" value="${requestScope.options['sessionInactiveExpirationTime']}">
    </label>

    <label for="requestValidityTime">
        <input id="requestValidityTime" name="requestValidityTime" type="number" value="${requestScope.options['requestValidityTime']}">
    </label>

    <label for="delayToPutToken">
        <input id="delayToPutToken" name="delayToPutToken" type="number" value="${requestScope.options['delayToPutToken']}">
    </label>



    <input type="submit" value="change" name="action">
</form>


<form method="post" action="adminSetup">
    Reset options ?
    <input type="submit" value="resetOptions" name="action">
</form>

<form method="post" action="adminSetup">
    Delete all
    <input type="submit" value="deleteAll" name="action">
</form>

</body>
</html>
