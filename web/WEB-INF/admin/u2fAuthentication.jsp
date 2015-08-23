<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="u2fAuthentication.jsp"/>
  <jsp:param name="title" value="U2F authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>


<c:if test="${sessionScope.authenticationChallenge != null}">
  Put your key you bastard
  <script language="JavaScript" >
    $(document).ready(function(){

      var request = JSON.parse('<c:out value="${sessionScope.authenticationChallenge}" escapeXml="false" />');
      console.log(request);
      var clientData = request['authenticateRequests'];

      console.log(clientData);
      u2f.sign(clientData, function(response){
        if(response.errorCode){
            var pathArray = window.location.pathname.split('/');
            window.location.href = window.location.protocol+"//"+window.location.host+'/'+pathArray[1]+"/adminAuthentication?error="+response.errorCode;
        }
        else{
          console.log(response);
          var form = document.createElement("form");
          form.setAttribute("method", "post");
          form.setAttribute("action", "adminU2fAuthenticate");
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
