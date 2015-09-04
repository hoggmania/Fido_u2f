<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="ajax.jsp"/>
  <jsp:param name="title" value="ajax"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<div id="error"></div>
<div id="success"></div>

<div id="authentication">
  <c:if test="${sessionScope.hasKey == true && sessionScope.u2fAuthenticated == false}">
    <form id="authenticationForm" method="post">
      <input type="submit" value="authentication">
    </form>
  </c:if>
</div>
<script>
  $(document).ready(function(){
    <c:if test="${sessionScope.hasKey == false || sessionScope.u2fAuthenticated == false}">

      authenticationForm();
    function authenticationForm(){
      $('#details').html("");
        $('#authenticationForm').on("submit", function(e){
            e.stopImmediatePropagation();
            $('#error').html('');

          e.preventDefault();
          $(this).slideUp().html("").show();
          $.ajax({
            method:'post',
            action: '/ajaxU2FAuthentication',
            type : 'json',
            success : function(data){
              var json = JSON.parse(data);

              if(json['error'] != null){
                $('#error').append(json['message']);
                $("#authentication").html('<form id="authenticationForm" method="post"><input type="submit" value="authentication"></form>');
                  authenticationForm();
              }else{
                $('#success').html("${Messages.PUT_TOKEN}");
                var request = json["message"];

                <c:if test="${sessionScope.details}">
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
                u2f.sign(json['message']['authenticateRequests'], function(response){
                    $('#success').html("");
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
                    $("#authentication").html('<form id="authenticationForm" method="post"><input type="submit" value="authentication"></form>');
                    $("#error").html(error);
                      authenticationForm();
                  }
                  else{
                    $.ajax({
                      method:'post',
                      action: '/ajaxU2FAuthentication',
                      data: "response="+JSON.stringify(response),
                      type: 'json',
                      success: function(data){
                        var json = JSON.parse(data);

                        if(!json['success']){
                          $('#error').html(json['message']);
                          $("#authentication").html('<form id="authenticationForm" method="post"><input type="submit" value="authentication"></form>');
                            authenticationForm();
                        }else {
                          $('#success').html(json['message']);
                          <c:choose>
                          <c:when test="${sessionScope.details}">

                          $("#details").append("<strong>Token response : </strong> <ul>");
                          for(var k in response){
                            $("#details").append("<li>"+k+": "+response[k]+"</li>")
                          }
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

                          var clientData = JSON.parse(atob(strtr(response.clientData, '-_', '+/')));
                          for(var i in clientData){
                            $("#details").append("<li>"+i+": "+clientData[i]+"</li>")
                          }
                          $("#details").append("</ul>");


                          $("#details").append("<strong>Decoded signature data</strong> <ul>");

                          for(var m in json['message']){
                            $("#details").append("<li>"+m+": "+json['message'][m]+"</li>")
                          }

                          $("#details").append("</ul><button id='button'>Continue</button>");
                          $('#details #button').on('click', function(){
                            window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/manage';
                          });
                          </c:when>
                          <c:otherwise>
                            window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/manage';
                          </c:otherwise>
                          </c:choose>
                        }

                      },
                      error: function(error){
                        $("#authentication").html('<form id="authenticationForm" method="post"><input type="submit" value="authentication"></form>');
                          authenticationForm();
                        $('#error').html(error);
                      }
                    })
                  }

                }, ${sessionScope.tokenTimeout});
              }

            },
            error : function(error){
              $("#authentication").html('<form id="authenticationForm" method="post"><input type="submit" value="authentication"></form>');
                authenticationForm();
              $('#error').html(error);
            }
          });
          return false;
        });
    }
    </c:if>
  });
</script>
<div id="details">
</div>
</body>
</html>

