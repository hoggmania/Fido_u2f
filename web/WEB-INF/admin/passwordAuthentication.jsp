<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="paswwordAuthentication.jsp"/>
  <jsp:param name="title" value="Password authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<c:if test="${form.errors['default'] != null}">
    <div id="defaultError">
            ${form.errors["default"]}
    </div>
</c:if>

<form method="post" action="AdminAuthenticate">
    <label for="username">Username : </label>
    <input type="text" name="username" id="username" placeholder="username" value="${username}" />
    ${errors["username"]}
    <label for="password">Password : </label>
    <input type="text" name="password" id="password" placeholder="******" />
    ${errors["password"]}
    <input type="submit" value="Sign in !" />
</form>

</body>
</html>
