<%@ page import="caller.PropagationTesterCaller" %>
<%@ page import="constants.PropagationConstants" %>
<%--
  Author : LCC
--%>
<%@ page contentType = "text/html;charset=UTF-8" language = "java" %>
<html>
<head>
    <title>JDBC & Transaction Propagation Test Application</title>
</head>
<body>
<%
    if (request.getParameter("clear") != null && request.getParameter("clear").length() != 0) {
        out.print("<meta HTTP-EQUIV=\"Refresh\" content=\"0; URL=application.jsp\" property=\"\">");
        return;
    }
%>
<jsp:include page = "application.jsp"/>
<div style = "font-size : 32px; font-weight : bold; color : deeppink">
< 수행 결과 >
<br>
============================================
</div>
<br>
<%
    // JDBC & Transaction 작업을 위한 객체 생성하여 Request.
    byte result = new PropagationTesterCaller(request).callRequest();

    // JDBC & Transaction 작업 결과 브라우저에 출력.
    switch (result) {
        case PropagationConstants.BEFORE_PROCESSING_REQUEST :
            // 브라우저에 수행 실패 결과 출력.
            out.print("<div style = \"font-size : 20pt; color : red;\">작업이 진행되지 않았습니다.</div>");
            break;
        case PropagationConstants.REQUEST_SUCCEEDED :
            // 브라우저에 수행 성공 결과 출력.
            out.print("<div style = \"font-size : 20pt; color : forestgreen;\">서버로의 요청이 성공적으로 완료되었습니다.</div>");
            out.print("<div style = \"font-size : 20pt; color : forestgreen;\">새로고침을 누르거나 다시 확인 버튼을 눌러 계속 요청을 보낼 수 있습니다.</div>");
            break;
        case PropagationConstants.REQUEST_FAILED :
            // 브라우저에 수행 오류 결과 출력.
            out.print("<div style = \"font-size : 20pt; color : red;\">서버로 요청을 전송하였으나 오류가 발생하였습니다.</div>");
            out.print("<div style = \"font-size : 20pt; color : red;\">서버 로그에 출력된 오류를 확인해주세요.</div>");
            break;
        case PropagationConstants.CLEAR_RESOURCES_FAILED :
            // 브라우저에 수행 오류 결과 출력.
            out.print("<div style = \"font-size : 20pt; color : red;\">요청 수행 후 리소스 정리 도중 에러가 발생하였습니다.</div>");
            out.print("<div style = \"font-size : 20pt; color : red;\">서버 로그에 출력된 오류를 확인해주세요.</div>");
            break;
        case PropagationConstants.NULL_OPERATION_REQUESTED :
            // 브라우저에 수행 실패 결과 출력.
            out.print("<div style = \"font-size : 20pt; color : dimgray;\">Caller가 데이터소스를 사용하지도 EJB 전파를 수행하지도 않습니다.</div>");
            out.print("<div style = \"font-size : 20pt; color : dimgray;\">데이터소스 ID를 입력하거나 EJB 전파를 수행하도록 한 뒤 다시 요청을 시작하세요.</div>");
            break;
        case PropagationConstants.ADDITIONAL_PARAMETER_REQUIRED :
            // 브라우저에 수행 실패 결과 출력.
            out.print("<div style = \"font-size : 20pt; color : dimgray;\">EJB 전파에 필요한 JNDI Lookup Parameter가 입력되지 않았습니다.</div>");
            out.print("<div style = \"font-size : 20pt; color : dimgray;\">호출할 EJB에 대한 JNDI Lookup Parameter를 입력 한 뒤 다시 요청을 시작하세요.</div>");
    }
%>

</body>
</html>
