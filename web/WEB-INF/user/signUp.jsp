<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="signUp.jsp"/>
  <jsp:param name="title" value="Sign up"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<c:choose>
    <c:when test="${!empty(requestScope.errors[\"option\"])}">
        <div id="error">
                ${requestScope.errors["option"]}
        </div>
    </c:when>
    <c:otherwise>
        <div id="error">
          ${requestScope.errors["default"]}
        </div>

        <form method="post" action="signUp">
          <label for="username">Username : </label>
          <input type="text" name="username" id="username" placeholder="username" value="${requestScope.username}" required pattern="[A-Za-z0-9_@.]{4,16}" title="A-Z a-z 0-9 _@. {4,16}"/>
          ${requestScope.errors["username"]}
          <label for="password">Password : </label>
          <input type="password" name="password" id="password" placeholder="******" required pattern="[A-Za-z0-9]{4,16}" title="A-Z a-z 0-9 {4,16}"/>
          ${requestScope.errors["password"]}
          <label for="passwordConfirmation">Confirm password : </label>
          <input type="password" name="passwordConfirmation" id="passwordConfirmation" placeholder="******" required pattern="[A-Za-z0-9]{4,16}" title="A-Z a-z 0-9 {4,16}"/>

          <input type="submit" value="Sign up !" />
        </form>
    </c:otherwise>
</c:choose>
</body>
</html>
