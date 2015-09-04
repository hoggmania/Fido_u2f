<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="u2fAuthentication.jsp"/>
  <jsp:param name="title" value="U2F authentication"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>


<div id="info">
</div>

<div id="error">
    ${requestScope.errors["default"]}
</div>

<div id="authentication"></div>
<c:if test="${sessionScope.authenticationChallenge == null || !empty(requestScope.errors)}">

    <div id="from">
            ${sessionScope.from}
    </div>
    <script>$('#authentication').html('<form method="post" action="adminU2fAuthenticate"><input type="submit" name="authenticate" value="Authenticate"></form>')</script>


</c:if>

<c:if test="${sessionScope.authenticationChallenge != null && empty(requestScope.errors)}">

  <script language="JavaScript" >
    $(document).ready(function(){

        var request = JSON.parse('<c:out value="${sessionScope.authenticationChallenge}" escapeXml="false" />');
        var timeout = "${sessionScope.tokenTimeout}".length < 1 ? 60 : "${sessionScope.tokenTimeout}";
        $("#info").html("${Messages.PUT_TOKEN}");
        var clientData = request['authenticateRequests'];
          u2f.sign(clientData, function(response){
              $("#info").html("");
            if(response.errorCode){
              var error;
              switch (response.errorCode) {
                case 1:
                  error = "${Messages.OTHER_ERROR}";
                  break;
                case 2:
                  error = "${Messages.BAD_REQUEST}";
                  break;
                case 3:
                  error = "${Messages.CONFIGURATION_UNSUPPORTED}";
                  break;
                case 4:
                  error = "${Messages.DEVICE_INELIGIBLE}";
                  break;
                case 5:
                  error = "${Messages.TIMEOUT}";
                  break;
                default :
                  error = "${Messages.UNKNOWN_ERROR}";
                  break;
              }
              $("#error").append(error);
              $("#authentication").append('<form method="post" action="adminU2fAuthenticate"><input type="submit" name="authenticate" value="Authenticate"></form>');
            }
            else{

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

          }, timeout);
        });

  </script>
</c:if>

</body>
</html>
