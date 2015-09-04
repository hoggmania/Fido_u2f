<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="u2fProtectedPage.jsp"/>
  <jsp:param name="title" value="U2F protected Page"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>


<div id="error">
  ${requestScope.errors["default"]}
</div>

${Messages.U2F_PROTECTED_PAGE}
</body>
</html>
