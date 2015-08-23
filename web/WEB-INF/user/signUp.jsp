<%--@elvariable id="username" type="fr.neowave.beans.User"--%>
<%--@elvariable id="form" type="fr.neowave.forms.old.FormResponse"--%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="signUp.jsp"/>
  <jsp:param name="title" value="Sign up"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<c:if test="${errors['default'] != null}">
  <div id="defaultError">
          ${errors["default"]}
  </div>
</c:if>

<form method="post" action="signUp">
  <label for="username">Username : </label>
  <input type="text" name="username" id="username" placeholder="username" value="${username}" />
  ${errors["username"]}
  <label for="password">Password : </label>
  <input type="text" name="password" id="password" placeholder="******" />
  ${errors["password"]}
  <label for="passwordConfirmation">Confirm password : </label>
  <input type="text" name="passwordConfirmation" id="passwordConfirmation" placeholder="******" />

  <input type="submit" value="Sign up !" />
</form>

</body>
</html>
