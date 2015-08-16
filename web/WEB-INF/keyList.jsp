<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="default/head.jsp" flush="true">
  <jsp:param name="id" value="keyList.jsp"/>
  <jsp:param name="title" value="Key list"/>
</jsp:include>
<body>
<jsp:include page="default/nav.jsp" flush="true"/>

${success}
<c:if test="${request != null}">
    ${request}
    <script language="JavaScript" >
        $(document).ready(function(){
            var request = JSON.parse('<c:out value="${request}" escapeXml="false" />');
            console.log(request);
            var clientData = request['registerRequests'];
            clientData['appId'] = clientData['appId'];
            var registrations = request['authenticateRequests'] ? request['authenticateRequests'] : [];
            console.log(registrations);
            console.log(clientData);
            u2f.register([clientData], [], function(response){
                var form = document.createElement("form");
                form.setAttribute("method", "post");
                form.setAttribute("action", "keyList");
                var field = document.createElement("input");
                field.setAttribute("type", "hidden");
                field.setAttribute("name", "response");
                field.setAttribute("value", JSON.stringify(response));
                form.appendChild(field);
                form.submit();
            },60);
        });

    </script>
</c:if>
${error}
<form method="post" action="keyList"><input type="submit" value="Add key"></form>


</body>
</html>
