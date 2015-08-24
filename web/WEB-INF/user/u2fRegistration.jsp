<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="keyList.jsp"/>
  <jsp:param name="title" value="u2fRegistration"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<form action="u2fRegister" method="post"><input type="submit" name="register" value="register"></form>
${requestScope.errors}
<c:if test="${sessionScope.registrationChallenge != null}">
    Put your key you bastard
  <script language="JavaScript" >

    $(document).ready(function(){
      var request = JSON.parse('<c:out value="${sessionScope.registrationChallenge}" escapeXml="false" />');
      console.log(request);
      var clientData = request['registerRequests'];
      var sigs = request['authenticateRequests'];
      console.log(clientData);
      u2f.register([clientData], sigs, function(response){
        if(response.errorCode){
            console.log(response.errorCode);

            /*var param = ${!empty(param.from) ? param.from : 'null'};
            window.location.href = param != null ? param : window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/index'*/
        }
        else{
            console.log(response);
            var form = document.createElement("form");
            form.setAttribute("method", "post");
            form.setAttribute("action", "u2fRegister");
            var hidden = document.createElement("input");
            hidden.setAttribute("type", "hidden");
            hidden.setAttribute("name", "from");
            hidden.setAttribute("value", ${param.from ? param.from : "null"});
            var field = document.createElement("input");
            field.setAttribute("type", "hidden");
            field.setAttribute("name", "response");
            field.setAttribute("value", JSON.stringify(response));
            form.appendChild(hidden);
            form.appendChild(field);
            form.submit();
        }

      },5);
    });

  </script>
</c:if>
</body>
</html>
