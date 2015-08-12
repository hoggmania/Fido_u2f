<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="default/head.jsp" flush="true">
  <jsp:param name="id" value="registration.jsp"/>
  <jsp:param name="title" value="Registration"/>
</jsp:include>
<body>
<jsp:include page="default/nav.jsp" flush="true"/>

${sessionScope.user}
<c:if test="${!sessionScope.user == null}">${user.username}</c:if>
<c:if test="${sessionScope.user == null}">

      ${form.errors}
<form method="post" action="registration">
  <label for="username">Username : </label>
  <input type="text" name="username" id="username" placeholder="username" />

  <label for="password">Password : </label>
  <input type="text" name="password" id="password" placeholder="******" />

  <label for="passwordConfirmation">Confirm password : </label>
  <input type="text" name="passwordConfirmation" id="passwordConfirmation" placeholder="******" />

  <input type="submit" value="Sign up !" />
</form>
</c:if>
</body>
</html>
