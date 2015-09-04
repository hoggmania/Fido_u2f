<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<html>
<head>
    <title>404 error</title>
</head>
<body>
<jsp:include page="default/nav.jsp" flush="true"/>

<body>

${Messages.ERROR_404}
</body>
</html>
