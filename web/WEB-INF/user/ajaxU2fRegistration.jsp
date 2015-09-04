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

<div id="registration">
    <form id="registrationForm" method="post">
        <input type="submit" value="register">
    </form>
</div>
<script>
  $(document).ready(function(){
      register();
      function register(){
          $('#registrationForm').on("submit", function(e){
              $('#details').html("");
              e.stopImmediatePropagation();
              e.preventDefault();


              $('#error').html('');
              $("#registration").html("");
              $.ajax({
                  method:'post',
                  action: '/ajaxU2FRegistration',
                  type : 'json',
                  success : function(data){
                      console.log(data);
                      var json = JSON.parse(data);

                      if(json['error'] != null){
                          $('#error').html(json['message']);
                          $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                          register();

                      }else{
                          <c:if test="${sessionScope.details}">
                          var request = json['message']
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
                          $('#success').html("${Messages.PUT_TOKEN}");
                          u2f.register([json['message']['registerRequests']], json['message']['authenticateRequests'], function(response){
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

                                  $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                                  $("#error").append(error);
                                  register();
                              }
                              else{
                                  $.ajax({
                                      method:'post',
                                      action: '/ajaxU2FRegistration',
                                      data: "response="+JSON.stringify(response),
                                      type: 'json',
                                      success: function(data){
                                          console.log(data);
                                          var json = JSON.parse(data);
                                          console.log(json['success']);
                                          if(!json['success']){


                                              $('#error').html(json['message']);
                                              $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                                              register();

                                          }else {

                                              $('#success').html(json['message']);
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

                                              var clientData = JSON.parse(atob(strtr(response.clientData, '-_', '+/')));
                                              for(var i in clientData){
                                                  $("#details").append("<li>"+i+": "+clientData[i]+"</li>")
                                              }
                                              $("#details").append("</ul> " +
                                                      "Decoded registration data : <ul>");
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
                                          $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                                          $('#error').html(error);
                                          register();
                                      }
                                  })
                              }
                          }, ${sessionScope.tokenTimeout});
                      }
                  },
                  error : function(error){
                      $('#error').html(error);
                      $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                      register();
                  }
              });
              return false;
          })
      }
    });
</script>
<div id="details">

</div>
</body>
</html>
