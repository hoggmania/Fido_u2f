<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="manage.jsp"/>
  <jsp:param name="title" value="Manage"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>

<c:if test="${param.id == null}">
    <script>window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/adminManage';</script>
</c:if>

<div id="error"></div>
<div id="success"></div>

<div id="registration">
    <form id="registrationForm" method="post">
        <input type="submit" name="register" value="Register">
    </form>
</div>
<script>
    $(document).ready(function(){
        register();
        function register(){
            $('#registrationForm').on("submit", function(e){
                e.stopImmediatePropagation();
                e.preventDefault();


                $('#error').html('');
                $("#registration").html("");
                $.ajax({
                    method:'post',
                    action: '/ajaxAdminRegistration',
                    data: "username="+atob("${param.id}"),
                    type : 'json',
                    success : function(data){
                        console.log(data);
                        var json = JSON.parse(data);

                        if(json['error'] != null){
                            $('#error').html(json['message']);
                            $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                            register();

                        }else{

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
                                        action: '/ajaxAdminRegistration',
                                        data: "response="+JSON.stringify(response),
                                        type: 'json',
                                        success: function(data){
                                            var json = JSON.parse(data);
                                            if(!json['success']){


                                                $('#error').html(json['message']);
                                                $("#registration").html('<form id="registrationForm" method="post"><input type="submit" value="Retry"></form>');
                                                register();

                                            }else {
                                                window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+'/manage';

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

</body>
</html>
