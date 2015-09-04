<%@ taglib uri="http://jakarta.apache.org/taglibs/unstandard-1.0" prefix="un" %>
<un:useConstants className="fr.neowave.messages.Messages" var="Messages" />
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<html>
<jsp:include page="../default/head.jsp" flush="true">
  <jsp:param name="id" value="log.jsp"/>
  <jsp:param name="title" value="Logs"/>
</jsp:include>
<body>
<jsp:include page="../default/nav.jsp" flush="true"/>


<table style="margin-bottom: 10px;" id="logs">
    <tr>
        <th> Session server id</th>
        <th> Message </th>
        <th> Context </th>
        <th> Username </th>
        <th> Browser name </th>
        <th> Browser version </th>
        <th> Os name </th>
        <th> User agent </th>
        <th> Ip </th>
        <th> Reverse name </th>
        <th> Request parameters </th>
        <th> Request attributes </th>
        <th> Request errors </th>
        <th> Session attributes </th>
        <th> Date time start </th>
        <th> Date time end </th>
        <th> End type </th>
    </tr>
    <c:forEach items="${requestScope.logs}" var="log">


        <tr>
            <td>${log["serverSessionId"]}</td>
            <td>${log["message"]}</td>
            <td>${log["context"]}</td>
            <td>${log["username"]}</td>
            <td>${log["browserName"]}</td>
            <td>${log["browserVersion"]}</td>
            <td>${log["osName"]}</td>
            <td>${log["osVersion"]}</td>
            <td>${log["ip"]}</td>
            <td>${log["reverseName"]}</td>
            <td>${log["requestParameters"]}</td>
            <td>${log["requestAttributes"]}</td>
            <td>${log["requestErrors"]}</td>
            <td>${log["sessionAttributes"]}</td>
            <td>${log["dateTimeStart"]}</td>
            <td>${log["dateTimeEnd"]}</td>
            <td>${log["endType"]}</td>
        </tr>



    </c:forEach>
</table>

</body>
</html>
