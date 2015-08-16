<%--@elvariable id="username" type="fr.neowave.beans.User"--%>
<%--@elvariable id="form" type="fr.neowave.forms.FormResponse"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="default/head.jsp" flush="true">
  <jsp:param name="id" value="authentication.jsp"/>
  <jsp:param name="title" value="Authentication"/>
</jsp:include>
<body>
<jsp:include page="default/nav.jsp" flush="true"/>

${request}
${form.errors["default"]}
<form method="post" action="authentication">
  <label for="username">Username : </label>
  <input type="text" name="username" id="username" placeholder="username" value="${username}" />
  ${form.errors["username"]}
  <label for="password">Password : </label>
  <input type="text" name="password" id="password" placeholder="******" />
  ${form.errors["password"]}
  <input type="submit" value="Sign in !" />
</form>

</body>
</html>
