<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header>
<c:choose>


  <c:when test="${sessionScope.username == 'admin'}">
    Hell yeah ! You are back sir Admin
    <nav>
      <ul>
        <li><a href="${pageContext.servletContext.contextPath}/index">Index</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/adminManage">Manage</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/signUp">Create account</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/adminSetup">Setup</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/activity">Recent activity</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/log">Logs</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/disconnection">Log out</a></li>
      </ul>
    </nav>
  </c:when>
  <c:when test="${sessionScope.username != null}">
    Welcome back ${sessionScope.username}
    <nav>
      <ul>
        <li><a href="${pageContext.servletContext.contextPath}/index">Index</a></li>
        <c:if test="${!sessionScope.hasKey}"><li><a href="${pageContext.servletContext.contextPath}/u2fRegister">U2F registration</a></li></c:if>
        <c:if test="${sessionScope.hasKey && !sessionScope.u2fAuthenticated}"><li><a href="${pageContext.servletContext.contextPath}/u2fAuthenticate">U2F authentication</a></li></c:if>
        <c:if test="${!sessionScope.hasKey}"><li><a href="${pageContext.servletContext.contextPath}/ajaxU2FRegistration">Ajax U2F registration</a></li></c:if>
        <c:if test="${sessionScope.hasKey && !sessionScope.u2fAuthenticated}"><li><a href="${pageContext.servletContext.contextPath}/ajaxU2FAuthentication">Ajax U2F authentication</a></li></c:if>

        <li><a href="${pageContext.servletContext.contextPath}/manage">Manage account</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/passwordProtectedPage">Password protected page</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/u2fProtectedPage">U2F protected page</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/disconnection">Log out</a></li>
      </ul>
    </nav>
  </c:when>
  <c:otherwise>
    <nav>
      <ul>
        <li><a href="${pageContext.servletContext.contextPath}/index">Index</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/authentication">Authentication</a></li>
        <li><a href="${pageContext.servletContext.contextPath}/signUp">Registration</a></li>
      </ul>
    </nav>
  </c:otherwise>

</c:choose>
</header>
