<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header>
<c:choose>

  <c:when test="${sessionScope.username == 'admin'}">
    <nav>
      <ul>
        <li><a href="${pageContext.servletContext.contextPath}/index">Index</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/options">Options</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/keyList">Key list</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/protectedPage">Protected page</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/UsersManager">Users manager</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/disconnection">Log out</a></li>
      </ul>
    </nav>
  </c:when>
  <c:when test="${sessionScope.username != null}">

    <nav>
      <ul>
        <li><a href="${pageContext.servletContext.contextPath}/index">Index</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/keyList">Key list</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/protectedPage">Protected page</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/disconnection">Log out</a></li>
      </ul>
    </nav>
  </c:when>
  <c:otherwise>
    <nav>
      <ul>
        <li><a href="${pageContext.servletContext.contextPath}/index">Index</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/authentication">Authentication</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/registration">Registration</a></li>
      </ul>
    </nav>
  </c:otherwise>

</c:choose>
</header>
