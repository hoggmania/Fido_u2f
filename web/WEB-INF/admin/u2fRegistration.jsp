<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="u2fRegistration.jsp"/>
  <jsp:param name="title" value="U2F registration"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>


<c:if test="${firstToken}">
    You have to register at least one token to be connected as admin
</c:if>

<script>
    u2f.register()
</script>

</body>
</html>
