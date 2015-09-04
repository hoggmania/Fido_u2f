<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="keyList.jsp"/>
  <jsp:param name="title" value="Key list"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<table>
   <tr>
       <th> KeyHandle </th>
       <th> Public Key </th>
       <th> Counter </th>
       <th> Attestation Certificate </th>
       <th> TimeStamp </th>
       <th> Hostname </th>
   </tr>
<c:forEach items="${requestScope.registrations}" var="registration">
    <tr>
        <td> ${registration["keyHandle"]} </td>
        <td> ${registration["publicKey"]}</td>
        <td> ${registration["counter"]} </td>
        <td> ${registration["certificate"]}</td>
        <td> ${registration["hostname"]} </td>
        <td> ${registration["timestamp"]}</td>
    </tr>
</c:forEach>
</table>
${requestScope.success}
<c:if test="${requestScope.request != null}">
    <script language="JavaScript" >
        $(document).ready(function(){
            var request = JSON.parse('<c:out value="${requestScope.request}" escapeXml="false" />');
            console.log(request);
            var clientData = request['registerRequests'];


            var registrations = request['authenticateRequests'] ? request['authenticateRequests'] : [];


            console.log(registrations);
            console.log(clientData);
            u2f.register([clientData], registrations, function(response){
                if(response.errorCode){
                    console.log(response.errorCode)
                }
                else{
                    console.log(response);
                    var form = document.createElement("form");
                    form.setAttribute("method", "post");
                    form.setAttribute("action", "keyList");
                    var field = document.createElement("input");
                    field.setAttribute("type", "hidden");
                    field.setAttribute("name", "response");
                    field.setAttribute("value", JSON.stringify(response));
                    form.appendChild(field);
                    form.submit();
                }

            },60);
        });

    </script>
</c:if>

${requestScope.error}
<form method="post" action="keyList"><input type="submit" value="Add key"></form>


</body>
</html>
