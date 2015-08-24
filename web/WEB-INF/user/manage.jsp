<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="passwordAuthentication.jsp"/>
  <jsp:param name="title" value="Password authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>
${requestScope.errors}
<c:forEach items="${requestScope.registrations}" var="registration">
  <tr>
    <td>
      <form method="post" action="manage">
        <input type="hidden" name="keyHandle" value="${registration["keyHandle"]}">
        <input type="submit" name="deleteToken" value="DT">
      </form>
    </td>
    <td></td>
    <td> ${registration["keyHandle"]} </td>
    <td> ${registration["counter"]} </td>
    <td> ${registration["hostname"]} </td>
    <td> ${registration["timestamp"]}</td>
  </tr>
</c:forEach>
</body>
</html>
