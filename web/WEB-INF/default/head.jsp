<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<head>
    <title>${param.title}</title>
    <link rel="stylesheet" href="/fido/public/css/style.css" />
    <script src="/fido/public/js/jquery-1.11.3.min.js"></script>
    <script>window.jQuery || document.write('')</script>
    <script src="/fido/public/js/u2f-api.js"></script>
</head>
<c:set var="browser" value="${header['User-Agent']}" scope="page"/>

<c:if test="${!fn:contains(browser,'Chrome')
|| ((fn:split(fn:substringAfter(fn:replace(fn:split(fn:substring(browser, fn:indexOf(browser, 'Chrome'), fn:length(browser)), ' ')[0],'/', '-'), 'Chrome-'), '.')[0]) < 42) }">
    <script> window.location.href = window.location.protocol+"//"+window.location.host+"/"+window.location.pathname.split('/')[1]+"/badConfig";</script>
</c:if>

