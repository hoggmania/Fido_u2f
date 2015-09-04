<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="u2fRegistration.jsp"/>
  <jsp:param name="title" value="U2F registration"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<div id="error">
    ${requestScope.errors["default"]}
    ${requestScope.errors["option"]}
</div>
<div id="info">

</div>
<div id="registration">

</div>
<c:choose>
    <c:when test="${requestScope.success}">
        <c:if test="${!empty(requestScope.from)}">
            <script>
                window.location.href = "${requestScope.from}";
            </script>
        </c:if>
        <script>
            window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/adminManage';
        </script>

    </c:when>
    <c:when test="${sessionScope.registrationChallenge == null || !empty(requestScope.errors)}">
        <script>
            $("#registration").html('<form method="post" action="adminU2fRegister"><input type="hidden" name="username" value="${requestScope.username}"><input type="submit" name="register" value="Register"></form>');
        </script>
    </c:when>
    <c:otherwise>

        <c:if test="${sessionScope.registrationChallenge != null}">

            <script language="JavaScript" >
                $(document).ready(function(){
                    var request = JSON.parse('<c:out value="${sessionScope.registrationChallenge}" escapeXml="false" />');
                    console.log(request);
                    var clientData = request['registerRequests'];
                    var sigs = request['authenticateRequests'];
                    $("#info").html("${Messages.PUT_TOKEN}");
                    $("#registration").html('');
                    u2f.register([clientData], sigs, function(response){
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
                            $("#registration").html('<form method="post" action="adminU2fRegister"><input type="hidden" name="username" value="${requestScope.username}"><input type="submit" name="register" value="Retry"></form>');
                        }
                        else{


                            var form = document.createElement("form");
                            form.setAttribute("method", "post");
                            form.setAttribute("action", "adminU2fRegister");
                            var user = document.createElement("input");
                            user.setAttribute("type", "hidden");
                            user.setAttribute("name", "username");
                            user.setAttribute("value", "${requestScope.username}");
                            var field = document.createElement("input");
                            field.setAttribute("type", "hidden");
                            field.setAttribute("name", "response");
                            field.setAttribute("value", JSON.stringify(response));
                            form.appendChild(field);
                            form.appendChild(user);
                            form.submit();
                        }

                    },60);
                });

            </script>
        </c:if>
    </c:otherwise>
</c:choose>
</body>
</html>
