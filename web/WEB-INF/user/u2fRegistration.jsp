<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="keyList.jsp"/>
  <jsp:param name="title" value="u2fRegistration"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<div id="info"></div>
<div id="registration"></div>
<div id="details">

</div>
<c:choose>
    <c:when test="${requestScope.success}">
        <c:choose>
            <c:when test="${sessionScope.details}">

                <script>
                    $("#details").append("</ul><button id='button'>Continue</button>");
                    for(var k in JSON.parse('${requestScope.message}')){
                        $("#details").append("<li>"+k+": "+JSON.parse('${requestScope.message}')[k]+"</li>")
                    }
                    $('#details #button').on('click', function(){
                        <c:if test="${!empty(requestScope.from)}">
                            window.location.href = "${requestScope.from}";
                        </c:if>
                        window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/manage';
                    });

                </script>

            </c:when>
            <c:otherwise>
                <script>window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/manage';</script>
            </c:otherwise>

        </c:choose>
    </c:when>
    <c:when test="${!empty(requestScope.errors['option'])}">
        <div id="error">
                ${requestScope.errors["option"]}
        </div>
        <script> $('#registration').html('<form action="u2fRegister" method="post"><input type="hidden" name="from" value="${param.from}"><input type="submit" name="register" value="register"></form>') </script>
    </c:when>
    <c:otherwise>
        <div id="error">
                ${requestScope.errors["default"]}
        </div>
        <div id="from">
                ${sessionScope.from}
        </div>
        <c:if test="${requestScope.registrationChallenge == null || !empty(requestScope.errors)}">
            <script> $('#registration').html('<form action="u2fRegister" method="post"><input type="hidden" name="from" value="${param.from}"><input type="submit" name="register" value="register"></form>') </script>

        </c:if>
        <c:if test="${requestScope.registrationChallenge != null}">

            <script language="JavaScript" >

                $(document).ready(function(){
                    var request = JSON.parse('<c:out value="${sessionScope.registrationChallenge}" escapeXml="false" />');
                    <c:if test="${sessionScope.details}">
                    $("#details").append("<strong>Register request : </strong> <ul>");
                    for(k in request.registerRequests){
                        $("#details").append("<li>"+k+": "+request.registerRequests[k]+"</li>")
                    }
                    $("#details").append("</ul><strong>Registered tokens : </strong> <ul>");
                    for(i in request.authenticateRequests){
                        $("#details").append("<ul>"+i+": ");
                        for(k in request.authenticateRequests[i]){
                            $("#details").append("<li>"+k+": "+request.authenticateRequests[i][k]+"</li>")
                        }
                        $("#details").append("</ul>");
                    }
                    $("#details").append("</ul>");
                    </c:if>

                    var clientData = request['registerRequests'];
                    var sigs = request['authenticateRequests'];
                    $('#info').html("${Messages.PUT_TOKEN}");
                    u2f.register([clientData], sigs, function(response){
                        $('#info').html("");
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
                    $('#registration').html('<form action="u2fRegister" method="post"><input type="hidden" name="from" value="${param.from}"><input type="submit" name="register" value="register"></form>')

                    $("#error").append(error);

                    }
                    else{
                        var form = document.createElement("form");
                        form.setAttribute("method", "post");
                        form.setAttribute("id", "registration");
                        form.setAttribute("action", "u2fRegister");
                        var hidden = document.createElement("input");
                        hidden.setAttribute("type", "hidden");
                        hidden.setAttribute("name", "from");
                        hidden.setAttribute("value", "${requestScope.from}");
                        var field = document.createElement("input");
                        field.setAttribute("type", "hidden");
                        field.setAttribute("name", "response");
                        field.setAttribute("value", JSON.stringify(response));
                        form.appendChild(hidden);
                        form.appendChild(field);
                        <c:choose>
                            <c:when test="${sessionScope.details}">
                                var strtr = function (str, from, to) {
                                    var out = "", i, m, p ;
                                    for (i = 0, m = str.length; i < m; i++) {
                                        p = from.indexOf(str.charAt(i));
                                        if (p >= 0) {
                                            out = out + to.charAt(p);
                                        }
                                        else {
                                            out += str.charAt(i);
                                        }
                                    }
                                    return out;
                                };

                                $("#details").append("<strong>Token response : </strong> <ul>");
                                for(var k in response){
                                    $("#details").append("<li>"+k+": "+response[k]+"</li>")
                                }
                                $("#details").append("</ul> " +
                                        "Decoded client data : <ul>");
                                console.log(atob(strtr(response.registrationData, '-_', '+/')));
                                var clientData = JSON.parse(atob(strtr(response.clientData, '-_', '+/')));
                                for(var i in clientData){
                                    $("#details").append("<li>"+i+": "+clientData[i]+"</li>")
                                }

                                $("#details").append("</ul><button id='button'>Continue</button>");

                                $('#details #button').on('click', function(){
                                    form.submit()
                                });
                            </c:when>
                            <c:otherwise>
                                form.submit();
                            </c:otherwise>
                        </c:choose>
                    }

                },${sessionScope.tokenTimeout});
            });

            </script>
        </c:if>
    </c:otherwise>
</c:choose>

</body>
</html>
