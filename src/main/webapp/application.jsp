<%--======================================================== 설정 부분 ========================================================--%>

<%--화면에 옵션으로 출력되는 데이터소스 ID 목록을 변경할 수 있습니다.--%>
<datalist id = "DataSource">
  <option value = "application.jsp에서 목록을 수정할 수 있습니다.">application.jsp에서 목록을 수정할 수 있습니다.</option>
  <option value = "tibero_XA1">tibero_XA1</option>
  <option value = "tibero_XA2">tibero_XA2</option>
  <option value = "tibero_XA3">tibero_XA3</option>
  <option value = "cluster_tibero_XA">cluster_tibero_XA</option>
</datalist>

<%--화면에 옵션으로 출력되는 데이터소스 Query 목록을 변경할 수 있습니다.--%>
<datalist id = "Query">
  <option value = "application.jsp에서 목록을 수정할 수 있습니다.">application.jsp에서 목록을 수정할 수 있습니다.</option>
  <option value = "INSERT INTO INTEGER_TABLE VALUES (1)">INSERT INTO INTEGER_TABLE VALUES (1)</option>
  <option value = "SELECT * FROM INTEGER_TABLE">SELECT * FROM INTEGER_TABLE</option>
  <option value = "DELETE FROM INTEGER_TABLE">DELETE FROM INTEGER_TABLE</option>
</datalist>

<%--화면에 옵션으로 출력되는 JNDI Provider URL 목록을 변경할 수 있습니다.--%>
<datalist id = "JNDI_Provider_URL">
  <option value = "application.jsp에서 목록을 수정할 수 있습니다.">application.jsp에서 목록을 수정할 수 있습니다.</option>
  <option value = "<IP>:<Port>">JEUS간 RMI 호출</option>
  <option value = "t3://<IP>:<Port>">Weblogic간 RMI 호출</option>
  <option value = "corbaname:iiop:1.2@<IP>:<Port>">JEUS IIOP 호출</option>
  <option value = "corbaname:iiop:1.2@<IP>:<Port>">Weblogic IIOP 호출</option>
  <option value = "corbaname:iiop:1.2@<IP>:<Port>">WebSphere IIOP 호출</option>
  <option value = "iiop://<IP>:<Port>">기타 IIOP 호출 형식</option>
  <option value = "corbaloc:iiop:1.2@<IP>:<Port>">기타 IIOP 호출 형식</option>
  <option value = "iiopname://<IP>:<Port>">기타 IIOP 호출 형식</option>
</datalist>

<%--화면에 옵션으로 출력되는 JNDI Lookup Parameter 목록을 변경할 수 있습니다.--%>
<datalist id = "JNDI_Lookup_Parameter">
  <option value = "application.jsp에서 목록을 수정할 수 있습니다.">application.jsp에서 목록을 수정할 수 있습니다.</option>
  <option value = "ejb1">JEUS 및 Weblogic JNDI Name</option>
  <option value = "cell/nodes/<NODE_NAME>/servers/<SERVER_NAME>/ejb1">WebSphere JNDI Name</option>
  <option value = "corbaname:iiop:1.2@<IP>:<Port>#ejb1">기타 JNDI Name 형식</option>
  <option value = "corbaname:iiop:1.2@<IP>:<Port>#cell/nodes/<NODE_NAME>/servers/<SERVER_NAME>/ejb1">기타 JNDI Name 형식</option>
</datalist>

<%--======================================================== 설정 종료 ========================================================--%>

<%@ page import="constants.PropagationConstants" %>
<%@ page import="java.util.logging.Level" %>
<%--
  Author : LCC
--%>
<%@ page contentType = "text/html;charset=UTF-8" language = "java" %>
<html>
  <head>
    <title>JDBC & Transaction Propagation Test Application</title>
  </head>
  <body>
  <div style = "font-size : 32pt; font-weight : bold; color : royalblue; text-align: center; margin-right: 50px;">JDBC & Transaction Propagation Test Framework</div>
  <div style = "font-size : 12pt; font-weight : bold; color : black; text-align: right; margin-right : 54px;">v1.0</div>
  <div style = "font-size : 20pt; font-weight : bold; color : darkblue; text-align: right; margin-right : 50px;">티맥스소프트 WAS실</div>
  <div style = "font-size : 20pt; font-weight : bold; color : transparent; text-align: right; margin-right : 50px;">만든 사람 : 이청천</div>

  <form method = "GET" action = "request.jsp">

    <table border = "1" cellspacing = "0" style = "float: left; margin-right: 10px; margin-bottom: 10px;">
      <tr>
        <th style = "width : 250px; height : 50px; font-size : 16pt; font-weight : bold; background-color : orange;">
          Caller 설정 항목
        </th>
        <th style = "width : 350px; height : 50px; font-size : 16pt; font-weight : bold; background-color : orange;">
          Caller 설정 값
        </th>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="Container Managed Transaction으로 동작하는 트랜잭션의 완료 방식을 결정합니다.">
        <td align = "center" style = "background-color : lightcyan;">
          글로벌 트랜잭션의 판단
        </td>
        <td align = "center">
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION %>"
                  value = <%= PropagationConstants.NO_TX %>
                  <% if (request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION) == null ||
                  (request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION) != null &&
                  request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION).equalsIgnoreCase(PropagationConstants.NO_TX)))
                  { out.print("checked"); } %>>사용 안함
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION %>"
                  value = <%= PropagationConstants.COMMIT %>
                  <% if (request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION) != null &&
                  request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION).equalsIgnoreCase(PropagationConstants.COMMIT))
                  { out.print("checked"); } %>>Commit
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION %>"
                  value = <%= PropagationConstants.ROLLBACK %>
                  <% if (request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION) != null &&
                  request.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION).equalsIgnoreCase(PropagationConstants.ROLLBACK))
                  { out.print("checked"); } %>>Rollback
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION %>">데이터소스1 ID</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION %>"
                  type = "text" style = "width : 340px; border : 0px; font-size : 10pt;" list="DataSource"
                  name = "<%= PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION %>">데이터소스1 Query</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION %>"
                  type = "text" style = "width : 340px; border : 0px; font-size : 10pt;" list="Query"
                  name = "<%= PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION %>">데이터소스2 ID</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="DataSource"
                  name = "<%= PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION %>">데이터소스2 Query</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="Query"
                  name = "<%= PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="한 번에 맺을 Connection의 수 이며, 이 값이 JDBC Connection Pool의 Size 보다 클 경우 Timeout이 발생할 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION %>">동시에 사용할 Connection 수</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION %>" type = "number" min = "1" max = "100" step = "1"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION)); } else { out.print(1); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="글로벌 트랜잭션을 사용하는 경우 Commit 또는 Rollback 직전에 Sleep을 하고, 트랜잭션을 사용하지 않는 경우 작업 완료 직전에 Sleep을 합니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_SLEEP_TIME_OPTION %>">트랜잭션 완료 전 Sleep 시간 (초)</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_SLEEP_TIME_OPTION %>" type = "number" min = "0" max = "60" step = "0.01"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_SLEEP_TIME_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_SLEEP_TIME_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_SLEEP_TIME_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.CALLER_SLEEP_TIME_OPTION)); } else { out.print(0); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="하나의 동일한 스레드 내에서 전체 작업을 반복하는 횟수를 의미합니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION %>">전체 트랜잭션 작업 반복 횟수</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION %>" type = "number" min = "1" max = "100" step = "1"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION)); } else { out.print(1); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="JDBC Connection Resource 사용 완료 후 고의로 닫지 않을 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          Connection Leak 발생 여부
        </td>
        <td align = "center">
          <input type = "checkbox" style = "width : 30px; height : 30px;"
                  name = "<%= PropagationConstants.CALLER_CONNECTION_LEAK_OPTION %>"
                  value = "true" <% if (request.getParameter(PropagationConstants.CALLER_CONNECTION_LEAK_OPTION) != null)
                  { out.print("checked"); } %>>
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="어플리케이션 내에서 출력하는 Log에 대한 Logging 레벨 수준입니다.">
        <td align = "center" style = "background-color : lightcyan;">
          Logging 레벨 수준
        </td>
        <td align = "center">
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_LOGGING_LEVEL_OPTION %>"
                  value = <%= Level.FINE %> <% if (request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION).equalsIgnoreCase(Level.FINE.toString()))
                  { out.print("checked"); } %>>FINE
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_LOGGING_LEVEL_OPTION %>"
                  value = <%= Level.INFO %> <% if (request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) == null ||
                  (request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION).equalsIgnoreCase(Level.INFO.toString())))
                  { out.print("checked"); } %>>INFO
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_LOGGING_LEVEL_OPTION %>"
                  value = <%= Level.WARNING %> <% if (request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION).equalsIgnoreCase(Level.WARNING.toString()))
                  { out.print("checked"); } %>>WARNING
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_LOGGING_LEVEL_OPTION %>"
                  value = <%= Level.OFF %> <% if (request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) != null &&
                   request.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION).equalsIgnoreCase(Level.OFF.toString()))
                   { out.print("checked"); } %>>OFF
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="어플리케이션 내에서 출력하는 Log에 색깔 강조 효과를 적용할 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          Log 색깔 강조 효과
        </td>
        <td align = "center">
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION %>"
                  value = "true" <% if (request.getParameter(PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION) == null ||
                  (request.getParameter(PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION) != null &&
                  !request.getParameter(PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION).equalsIgnoreCase("false")))
                  { out.print("checked"); } %>>사용
          <input type = "radio" style = "width : 30px; height : 15px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION %>"
                  value = "false" <% if (request.getParameter(PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION) != null &&
                  request.getParameter(PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION).equalsIgnoreCase("false"))
                  { out.print("checked"); } %>>미사용
        </td>
      </tr>
    </table>

    <table border = "1" cellspacing = "0" style = "float: left; margin-right: 10px; margin-bottom: 10px;">
      <tr>
        <th style = "width : 250px; height : 50px; font-size : 16pt; font-weight : bold; background-color : orange;">
          EJB1 설정 항목
        </th>
        <th style = "width : 350px; height : 50px; font-size : 16pt; font-weight : bold; background-color : orange;">
          EJB1 설정 값
        </th>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="체크박스를 체크한 경우에만 EJB 호출을 수행하며, 이 때에만 EJB 전파 설정 값들이 유효하게 됩니다.">
        <td align = "center" style = "background-color : lightcyan;">
          EJB 전파 사용 여부
        </td>
        <td align = "center">
          <input type = "checkbox" style = "width : 30px; height : 30px;"
                  name = "<%=PropagationConstants.EJB1_USING_EJB_OPTION%>"
                  value = "true" <% if (request.getParameter(PropagationConstants.EJB1_USING_EJB_OPTION) != null)
                  { out.print("checked"); } %>>
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="
JNDI InitialContext 초기화에 사용되는 Provider URL이며, 형식은 다음과 같습니다.

< RMI 호출 >
JEUS : <IP>:<Port> (Port는 JEUS의 Base Port)
Weblogic : t3://<IP>:<Port> (Port는 Weblogic의 Listen Port)
WebSphere : IIOP 호출만 사용

< IIOP 호출 >
JEUS / Weblogic / WebSphere : corbaname:iiop:1.2@<IP>:<Port>
(JEUS의 경우 Port는 Base Port + 4, Weblogic의 경우 Listen Port, WebSphere의 경우 Bootstrap Address 또는 ORB Listen Address Port)
      ">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION %>">JNDI Provider URL</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="JNDI_Provider_URL" placeholder="다른 서버의 EJB를 호출하는 경우 입력"
                  name = "<%= PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="
JNDI InitialContext를 이용하여 Lookup하고자 하는 객체의 JNDI Name 입니다.
EJB의 Deployment Descriptor에 설정된 기본 JNDI Name은 'ejb1'이며,
JEUS와 Weblogic의 경우 'ejb1'가 그대로 JNDI Name이 되고, WebSphere의 경우 Deploy 단계에서 별도로 설정하게 됩니다.
형식은 다음과 같습니다.

JEUS / Weblogic : ejb1
WebSphere : cell/nodes/<NODE_NAME>/servers/<SERVER_NAME>/<JNDI_NAME> (JNDI_NAME은 Deploy 단계에서 설정한 값)
      ">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION %>">JNDI Lookup Parameter</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="JNDI_Lookup_Parameter" placeholder="EJB 전파를 사용하는 경우 필수 입력"
                  name = "<%= PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="EJB1을 호출하는 주체를 누구로 할 것인지 선택합니다.">
        <td align = "center" style = "background-color : lightcyan;">
          EJB Client
        </td>
        <td align = "center">
          <select style="width : 350px; border : 0px; font-size : 10pt;"
                   name = "<%= PropagationConstants.EJB1_EJB_CLIENT_OPTION %>"
                   value = "<% if (request.getParameter(PropagationConstants.EJB1_EJB_CLIENT_OPTION) != null)
                   { out.print(request.getParameter(PropagationConstants.EJB1_EJB_CLIENT_OPTION)); } %>">
            <option value="<%= PropagationConstants.CALLER %>"
                     <% if (request.getParameter(PropagationConstants.EJB1_EJB_CLIENT_OPTION) != null &&
                     request.getParameter(PropagationConstants.EJB1_EJB_CLIENT_OPTION).equalsIgnoreCase(PropagationConstants.CALLER))
                     { out.print("selected"); } %>>Caller</option>
          </select>
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION %>">데이터소스1 ID</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="DataSource"
                  name = "<%= PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION %>">데이터소스1 Query</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="Query"
                  name = "<%= PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION %>">데이터소스2 ID</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="DataSource"
                  name = "<%= PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION %>">데이터소스2 Query</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="Query"
                  name = "<%= PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="한 번에 맺을 Connection의 수 이며, 이 값이 JDBC Connection Pool의 Size 보다 클 경우 Timeout이 발생할 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION %>">동시에 사용할 Connection 수</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION %>" type = "number" min = "1" max = "100" step = "1"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION) != null &&
                  request.getParameter(PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION)); } else { out.print(1); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="다른 EJB 호출을 제외한 자기 자신의 작업을 완료한 직후 Sleep을 합니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB1_SLEEP_TIME_OPTION %>">Response 직전 Sleep 시간 (초)</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB1_SLEEP_TIME_OPTION %>" type = "number" min = "0" max = "60" step = "0.01"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.EJB1_SLEEP_TIME_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB1_SLEEP_TIME_OPTION) != null &&
                  request.getParameter(PropagationConstants.EJB1_SLEEP_TIME_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.EJB1_SLEEP_TIME_OPTION)); } else { out.print(0); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="JDBC Connection Resource 사용 완료 후 고의로 닫지 않을 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          Connection Leak 발생 여부
        </td>
        <td align = "center">
          <input type = "checkbox" style = "width : 30px; height : 30px;"
                  name = "<%= PropagationConstants.EJB1_CONNECTION_LEAK_OPTION %>"
                  value = "true" <% if (request.getParameter(PropagationConstants.EJB1_CONNECTION_LEAK_OPTION) != null)
                  { out.print("checked"); } %>>
        </td>
      </tr>
    </table>

    <table border = "1" cellspacing = "0" style = "margin-top: 10px;">
      <tr>
        <th style = "width : 250px; height : 50px; font-size : 16pt; font-weight : bold; background-color : orange;">
          EJB2 설정 항목
        </th>
        <th style = "width : 350px; height : 50px; font-size : 16pt; font-weight : bold; background-color : orange;">
          EJB2 설정 값
        </th>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="체크박스를 체크한 경우에만 EJB 호출을 수행하며, 이 때에만 EJB 전파 설정 값들이 유효하게 됩니다.">
        <td align = "center" style = "background-color : lightcyan;">
          EJB 전파 사용 여부
        </td>
        <td align = "center">
          <input type = "checkbox" style = "width : 30px; height : 30px;"
                  name = "<%=PropagationConstants.EJB2_USING_EJB_OPTION%>"
                  value = "true" <% if (request.getParameter(PropagationConstants.EJB2_USING_EJB_OPTION) != null)
                  { out.print("checked"); } %>>
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="
JNDI InitialContext 초기화에 사용되는 Provider URL이며, 형식은 다음과 같습니다.

< RMI 호출 >
JEUS : <IP>:<Port> (Port는 JEUS의 Base Port)
Weblogic : t3://<IP>:<Port> (Port는 Weblogic의 Listen Port)
WebSphere : IIOP 호출만 사용

< IIOP 호출 >
JEUS / Weblogic / WebSphere : corbaname:iiop:1.2@<IP>:<Port>
(JEUS의 경우 Port는 Base Port + 4, Weblogic의 경우 Listen Port, WebSphere의 경우 Bootstrap Address 또는 ORB Listen Address Port)
      ">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION %>">JNDI Provider URL</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="JNDI_Provider_URL" placeholder="다른 서버의 EJB를 호출하는 경우 입력"
                  name = "<%= PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="
JNDI InitialContext를 이용하여 Lookup하고자 하는 객체의 JNDI Name 입니다.
EJB의 Deployment Descriptor에 설정된 기본 JNDI Name은 'ejb1'이며,
JEUS와 Weblogic의 경우 'ejb1'가 그대로 JNDI Name이 되고, WebSphere의 경우 Deploy 단계에서 별도로 설정하게 됩니다.
형식은 다음과 같습니다.

JEUS / Weblogic : ejb1
WebSphere : cell/nodes/<NODE_NAME>/servers/<SERVER_NAME>/<JNDI_NAME> (JNDI_NAME은 Deploy 단계에서 설정한 값)
      ">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION %>">JNDI Lookup Parameter</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="JNDI_Lookup_Parameter" placeholder="EJB 전파를 사용하는 경우 필수 입력"
                  name = "<%= PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="
EJB2를 호출하는 주체를 누구로 할 것인지 선택합니다.
만약 EJB1을 선택하였는데 EJB1이 먼저 호출되지 않는 상황이라면 EJB2로의 요청은 무시됩니다.
      ">
        <td align = "center" style = "background-color : lightcyan;">
          EJB Client
        </td>
        <td align = "center">
          <select style="width : 350px; border : 0px; font-size : 10pt;"
                   name = "<%= PropagationConstants.EJB2_EJB_CLIENT_OPTION %>"
                   value = "<% if (request.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION) != null)
                   { out.print(request.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION)); } %>">
            <option value="<%= PropagationConstants.CALLER %>"
                     <% if (request.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION) != null &&
                     request.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION).equalsIgnoreCase(PropagationConstants.CALLER))
                     { out.print("selected"); } %>>Caller</option>
            <option value="<%= PropagationConstants.EJB1 %>"
                     <% if (request.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION) != null &&
                     request.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION).equalsIgnoreCase(PropagationConstants.EJB1))
                     { out.print("selected"); } %>>EJB1</option>
          </select>
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION %>">데이터소스1 ID</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="DataSource"
                  name = "<%= PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION %>">데이터소스1 Query</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="Query"
                  name = "<%= PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION %>">데이터소스2 ID</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="DataSource"
                  name = "<%= PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION %>">데이터소스2 Query</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION %>" type = "text"
                  style = "width : 340px; border : 0px; font-size : 10pt;" list="Query"
                  name = "<%= PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION) != null)
                  { out.print(request.getParameter(PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION)); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="한 번에 맺을 Connection의 수 이며, 이 값이 JDBC Connection Pool의 Size 보다 클 경우 Timeout이 발생할 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION %>">동시에 사용할 Connection 수</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION %>" type = "number" min = "1" max = "100" step = "1"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION) != null &&
                  request.getParameter(PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION)); } else { out.print(1); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="다른 EJB 호출을 제외한 자기 자신의 작업을 완료한 직후 Sleep을 합니다.">
        <td align = "center" style = "background-color : lightcyan;">
          <label for="<%= PropagationConstants.EJB2_SLEEP_TIME_OPTION %>">Response 직전 Sleep 시간 (초)</label>
        </td>
        <td align = "center">
          <input id="<%= PropagationConstants.EJB2_SLEEP_TIME_OPTION %>" type = "number" min = "0" max = "60" step = "0.01"
                  style = "width : 340px; border : 0px; font-size : 10pt;"
                  name = "<%= PropagationConstants.EJB2_SLEEP_TIME_OPTION %>"
                  value = "<% if (request.getParameter(PropagationConstants.EJB2_SLEEP_TIME_OPTION) != null &&
                  request.getParameter(PropagationConstants.EJB2_SLEEP_TIME_OPTION).length() != 0)
                  { out.print(request.getParameter(PropagationConstants.EJB2_SLEEP_TIME_OPTION)); } else { out.print(0); } %>">
        </td>
      </tr>

      <tr style = "height : 40px; font-size : 12pt;" title="JDBC Connection Resource 사용 완료 후 고의로 닫지 않을 수 있습니다.">
        <td align = "center" style = "background-color : lightcyan;">
          Connection Leak 발생 여부
        </td>
        <td align = "center">
          <input type = "checkbox" style = "width : 30px; height : 30px;"
                  name = "<%= PropagationConstants.EJB2_CONNECTION_LEAK_OPTION %>"
                  value = "true" <% if (request.getParameter(PropagationConstants.EJB2_CONNECTION_LEAK_OPTION) != null)
                  { out.print("checked"); } %>>
        </td>
      </tr>
    </table>

    <br>
    <div align="center">
      <input type = "submit" style = "margin-right : 50px; width : 200px; height : 50px; font-size : 16pt; font-weight : bold;" name = "confirm" value = "확인">
      <input type = "reset" style = "margin-right : 50px; width : 200px; height : 50px; font-size : 16pt; font-weight : bold;" name = "restore" value = "마지막 요청 보기">
      <input type = "submit" style = "margin-right : 50px; width : 200px; height : 50px; font-size : 16pt; font-weight : bold;" name = "clear" value = "모두 지우기">
    </div>
  </form>
  </body>
</html>
