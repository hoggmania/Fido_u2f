<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="u2fAuthentication.jsp"/>
  <jsp:param name="title" value="U2F authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

${requestScope.errors}

<form action="u2fAuthenticate" method="post">
  <input type="submit" name="authenticate" value="authenticate">
</form>

${requestScope.authenticationChallenge}
<c:if test="${requestScope.authenticationChallenge != null}">
  <script language="JavaScript" >
    $(document).ready(function(){

      var request = JSON.parse('<c:out value="${requestScope.authenticationChallenge}" escapeXml="false" />');
      console.log(request);
      var clientData = request['authenticateRequests'];

      console.log(clientData);
      u2f.sign(clientData, function(response){
        if(response.errorCode){
          console.log(response.errorCode);
        }
        else{
          console.log(response);
          var form = document.createElement("form");
          form.setAttribute("method", "post");
          form.setAttribute("action", "u2fAuthenticate");
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
</body>
</html>
