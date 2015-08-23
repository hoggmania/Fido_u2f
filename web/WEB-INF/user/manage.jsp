<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Elekhyr
  Date: 24/08/2015
  Time: 00:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Manage</title>
</head>
<body>
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
