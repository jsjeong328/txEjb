<%@ page import="caller.PropagationTesterCaller" %>
<%@ page import="constants.PropagationConstants" %>
<%--
  Author : LCC
--%>
<%@ page contentType = "text/html;charset=UTF-8" language = "java" %>
<html>
<head>
    <title>JDBC & Transaction Propagation Test Application</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<%
    if (request.getParameter("clear") != null && request.getParameter("clear").length() != 0) {
        out.print("<meta HTTP-EQUIV=\"Refresh\" content=\"0; URL=application.jsp\" property=\"\">");
        return;
    }
%>

<%
    // 결과를 담을 StringBuilder 생성
    StringBuilder resultHtml = new StringBuilder();

    // JDBC & Transaction 작업 수행
    byte result = new PropagationTesterCaller(request).callRequest();

    resultHtml.append("<div class=\"result-section\">");
    resultHtml.append("  <div class=\"result-header\"><h2>수행 결과</h2></div>");
    resultHtml.append("  <div class=\"result-card\">");

    switch (result) {
        case PropagationConstants.BEFORE_PROCESSING_REQUEST :
            resultHtml.append("<div class=\"alert alert-warning\">");
            resultHtml.append("  <div class=\"alert-title\">작업 미진행</div>");
            resultHtml.append("  <div class=\"alert-desc\">작업이 진행되지 않았습니다.</div>");
            resultHtml.append("</div>");
            break;
        case PropagationConstants.REQUEST_SUCCEEDED :
            resultHtml.append("<div class=\"alert alert-success\">");
            resultHtml.append("  <div class=\"alert-title\">요청 성공</div>");
            resultHtml.append("  <div class=\"alert-desc\">서버로의 요청이 성공적으로 완료되었습니다. 새로고침을 누르거나 다시 확인 버튼을 눌러 계속 요청을 보낼 수 있습니다.</div>");
            resultHtml.append("</div>");
            break;
        case PropagationConstants.REQUEST_FAILED :
            resultHtml.append("<div class=\"alert alert-danger\">");
            resultHtml.append("  <div class=\"alert-title\">요청 실패</div>");
            resultHtml.append("  <div class=\"alert-desc\">서버로 요청을 전송하였으나 오류가 발생하였습니다. 서버 로그에 출력된 오류를 확인해주세요.</div>");
            resultHtml.append("</div>");
            break;
        case PropagationConstants.CLEAR_RESOURCES_FAILED :
            resultHtml.append("<div class=\"alert alert-danger\">");
            resultHtml.append("  <div class=\"alert-title\">리소스 정리 에러</div>");
            resultHtml.append("  <div class=\"alert-desc\">요청 수행 후 리소스 정리 도중 에러가 발생하였습니다. 서버 로그에 출력된 오류를 확인해주세요.</div>");
            resultHtml.append("</div>");
            break;
        case PropagationConstants.NULL_OPERATION_REQUESTED :
            resultHtml.append("<div class=\"alert alert-neutral\">");
            resultHtml.append("  <div class=\"alert-title\">조건 부족 (데이터소스 / EJB 미지정)</div>");
            resultHtml.append("  <div class=\"alert-desc\">Caller가 데이터소스를 사용하지도 EJB 전파를 수행하지도 않습니다. 데이터소스 ID를 입력하거나 EJB 전파를 수행하도록 한 뒤 다시 요청을 시작하세요.</div>");
            resultHtml.append("</div>");
            break;
        case PropagationConstants.ADDITIONAL_PARAMETER_REQUIRED :
            resultHtml.append("<div class=\"alert alert-neutral\">");
            resultHtml.append("  <div class=\"alert-title\">추가 파라미터 필요</div>");
            resultHtml.append("  <div class=\"alert-desc\">EJB 전파에 필요한 JNDI Lookup Parameter가 입력되지 않았습니다. 호출할 EJB에 대한 JNDI Lookup Parameter를 입력한 뒤 다시 요청을 시작하세요.</div>");
            resultHtml.append("</div>");
            break;
    }
    resultHtml.append("  </div>");
    resultHtml.append("</div>");

    // 렌더링된 결과 HTML을 자바스크립트로 상단 슬롯에 주입
    String htmlOutput = resultHtml.toString().replace("\"", "\\\"").replace("\n", "");
%>

<jsp:include page = "application.jsp"/>

<script>
    document.getElementById('result-slot').innerHTML = "<%= htmlOutput %>";
</script>

</body>
</html>