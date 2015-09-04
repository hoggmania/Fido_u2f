<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="paswwordAuthentication.jsp"/>
  <jsp:param name="title" value="Password authentication"/>
</jsp:include>
<body>

<div id="error">
    ${requestScope.errors["default"]}
    ${param.from}
</div>


<form method="post" action="adminAuthentication">
    <label for="username">Username : </label>
    <input type="text" name="username" id="username" placeholder="username" value="${requestScope.username}" required pattern="[A-Za-z0-9_@.]{4,16}" title="A-Z a-z 0-9 _@. {4,16}" />
    ${requestScope.errors["username"]} <br>
    <label for="password">Password : </label>
    <input type="password" name="password" id="password" placeholder="******" required pattern="[A-Za-z0-9]{4,16}" title="A-Z a-z 0-9 {4,16}"/>
    ${requestScope.errors["password"]} <br>
    <input type="submit" value="Sign in !" />
</form>

</body>
</html>
