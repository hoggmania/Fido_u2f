<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
    <jsp:param name="id" value="passwordAuthentication.jsp"/>
    <jsp:param name="title" value="Password authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

${requestScope.errors}
<c:if test="${form.errors['default'] != null}">
    <div id="defaultError">
            ${form.errors["default"]}
    </div>
</c:if>

<form method="post" action="authentication">
  <label for="username">Username : </label>
  <input type="text" name="username" id="username" placeholder="username" value="${username}" />
  ${errors["username"]}
  <label for="password">Password : </label>
  <input type="text" name="password" id="password" placeholder="******" />
  ${errors["password"]}
  <input type="submit" value="Sign in !" />
</form>

<c:if test="${!empty(request)}">
  <script language="JavaScript" >
    $(document).ready(function(){
      var request = JSON.parse('<c:out value="${request}" escapeXml="false" />');
      console.log(request);
      var clientData = request['authenticateRequests'];

      console.log(clientData);
      u2f.sign(clientData, function(response){
        if(response.errorCode){
          console.log(response.errorCode)
        }
        else{
          console.log(response);
          var form = document.createElement("form");
          form.setAttribute("method", "post");
          form.setAttribute("action", "authentication");
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
