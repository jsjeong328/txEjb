package caller;

import constants.PropagationConstants;
import helper.PropagationHelper;
import logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;

/**
 * Created by LCC.
 */
public class PropagationTesterCaller {
    // JDBC & Transaction propagation 작업 수행을 도와줄 helper 선언.
    PropagationHelper helper;

    // 각 EJB 에 대해 요청된 HttpServletRequest parameter 들을 담을 HashMap 객체 선언.
    private HashMap<String, Object> ejb1HttpServletRequestParameterMap = new HashMap<String, Object>();
    private HashMap<String, Object> ejb2HttpServletRequestParameterMap = new HashMap<String, Object>();

    // Logger 선언.
    private Logger logger;

    // JDBC & Transaction propagation 작업을 위한 필드 선언.
    private String callerGlobalTransactionDecision; // 글로벌 트랜잭션의 결정.
    private String callerDataSource1ID; // 데이터소스1 ID.
    private String callerDataSource1Query; // 데이터소스1 query.
    private String callerDataSource2ID; // 데이터소스2 ID.
    private String callerDataSource2Query; // 데이터소스2 query.
    private int callerNumberOfConnections; // 한 번에 연결할 Connection 수.
    private float callerSleepTime; // Commit 직전 sleep 시간 (초).
    private int callerNumberOfRequests; // 전체 트랜잭션 작업 반복 횟수.
    private boolean callerConnectionLeak; // Connection leak 발생 여부.

    // EJB propagation 작업 여부.
    boolean doCallerToEJB1Propagation; // EJB1에 대해 Caller 가 수행하여야 할 Propagation 작업이 있는지 여부.
    String ejb1JNDIProviderURL; // EJB1을 호출하기 위한 provider url.
    String ejb1JNDILookupParameter; // EJB1의 JNDI lookup name.
    boolean doCallerToEJB2Propagation; //  EJB2에 대해 Caller 가 수행하여야 할 Propagation 작업이 있는지 여부.
    String ejb2JNDIProviderURL; // EJB2를 호출하기 위한 provider url.
    String ejb2JNDILookupParameter; // EJB2의 JNDI lookup name.
    boolean doEJB1ToEJB2Propagation; // EJB2에 대해 EJB1 이 수행하여야 할 Propagation 작업이 있는지 여부.

    // Return 값.
    byte result = PropagationConstants.BEFORE_PROCESSING_REQUEST;

    /**
     * JDBC & Transaction propagation 작업을 위한 객체 생성 및 초기화.
     * @param httpServletRequest : 웹 요청으로 전달받은 HttpServletRequest 객체.
     */
    public PropagationTesterCaller(HttpServletRequest httpServletRequest) {
        // Caller 의 작업에 필요한 field 값 초기화.
        initializeCallerField(httpServletRequest);

        // EJB1의 작업에 필요한 field 값 초기화.
        initializeEJB1Field(httpServletRequest);

        // EJB2의 작업에 필요한 field 값 초기화.
        initializeEJB2Field(httpServletRequest);
    }

    /**
     * Caller 의 작업에 필요한 field 값 초기화.
     * @param httpServletRequest : 웹 요청으로 전달받은 HttpServletRequest 객체.
     */
    public void initializeCallerField(HttpServletRequest httpServletRequest) {
        // Parameter 에 전달된 값 parsing.
        this.callerGlobalTransactionDecision = httpServletRequest.getParameter(PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION) != null? httpServletRequest.getParameter(
            PropagationConstants.CALLER_GLOBAL_TRANSACTION_DECISION) : PropagationConstants.NO_TX;
        this.callerDataSource1ID = (httpServletRequest.getParameter(PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_1_ID_OPTION) : null;
        this.callerDataSource1Query = (httpServletRequest.getParameter(PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_1_QUERY_OPTION) : null;
        this.callerDataSource2ID = (httpServletRequest.getParameter(PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_2_ID_OPTION) : null;
        this.callerDataSource2Query = (httpServletRequest.getParameter(PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.CALLER_DATA_SOURCE_2_QUERY_OPTION) : null;
        this.callerNumberOfConnections = (httpServletRequest.getParameter(PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION).length() != 0)? Integer
            .parseInt(httpServletRequest.getParameter(PropagationConstants.CALLER_NUMBER_OF_CONNECTIONS_OPTION)) : 1;
        this.callerSleepTime = (httpServletRequest.getParameter(PropagationConstants.CALLER_SLEEP_TIME_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_SLEEP_TIME_OPTION).length() != 0)? Float
            .parseFloat(httpServletRequest.getParameter(PropagationConstants.CALLER_SLEEP_TIME_OPTION)) : 0;
        this.callerNumberOfRequests = (httpServletRequest.getParameter(PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION).length() != 0)? Integer
            .parseInt(httpServletRequest.getParameter(PropagationConstants.CALLER_NUMBER_OF_REQUESTS_OPTION)) : 1;
        this.callerConnectionLeak = httpServletRequest.getParameter(PropagationConstants.CALLER_CONNECTION_LEAK_OPTION) != null;

        // Logger 에 설정해 둘 logging level 과 log color emphasis 사용 여부를 얻어 Logger 객체 생성.
        String callerLoggerLevel = httpServletRequest.getParameter(PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) != null? httpServletRequest.getParameter(
            PropagationConstants.CALLER_LOGGING_LEVEL_OPTION) : Level.INFO.getName();
        boolean callerLogColorEmphasis = Boolean
            .parseBoolean(httpServletRequest.getParameter(PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION)) || httpServletRequest.getParameter(
            PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION) == null || !httpServletRequest.getParameter(
            PropagationConstants.CALLER_LOG_COLOR_EMPHASIS_OPTION).equalsIgnoreCase("false");
        this.logger = new Logger(callerLoggerLevel, callerLogColorEmphasis);

        // JDBC & Transaction propagation 작업 수행을 도와줄 helper 객체 생성.
        this.helper = new PropagationHelper(logger);
    }

    /**
     * EJB1의 작업에 필요한 field 값 초기화.
     * @param httpServletRequest : 웹 요청으로 전달받은 HttpServletRequest 객체.
     */
    public void initializeEJB1Field(HttpServletRequest httpServletRequest) {
        // EJB1에 대한 propagation 요청 정보.
        boolean ejb1UsingPropagation = httpServletRequest.getParameter(PropagationConstants.EJB1_USING_EJB_OPTION) != null; // EJB1에 대한 Propagation 사용 여부.
        String ejb1JNDIProviderURL = (httpServletRequest.getParameter(PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_JNDI_PROVIDER_URL_OPTION) : null; // EJB1을 호출하기 위한 provider url.
        String ejb1JNDILookupParameter = (httpServletRequest.getParameter(PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_JNDI_LOOKUP_PARAMETER_OPTION) : null; // EJB1의 JNDI lookup name.
        String ejb1EJBClient = (httpServletRequest.getParameter(PropagationConstants.EJB1_EJB_CLIENT_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_EJB_CLIENT_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_EJB_CLIENT_OPTION) : PropagationConstants.CALLER; // EJB1을 호출하는 클라이언트.
        String ejb1DataSource1ID = (httpServletRequest.getParameter(PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_1_ID_OPTION) : null; // EJB1이 사용하는 데이터소스1의 ID.
        String ejb1DataSource1Query = (httpServletRequest.getParameter(PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_1_QUERY_OPTION) : null; // EJB1이 사용하는 데이터소스1의 query.
        String ejb1DataSource2ID = (httpServletRequest.getParameter(PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_2_ID_OPTION) : null; // EJB1이 사용하는 데이터소스2의 ID.
        String ejb1DataSource2Query = (httpServletRequest.getParameter(PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB1_DATA_SOURCE_2_QUERY_OPTION) : null; // EJB1이 사용하는 데이터소스2의 query.
        int ejb1NumberOfConnections = (httpServletRequest.getParameter(PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION).length() != 0)? Integer
            .parseInt(httpServletRequest.getParameter(PropagationConstants.EJB1_NUMBER_OF_CONNECTIONS_OPTION)) : 1; // EJB1이 한 번에 연결할 Connection 수.
        float ejb1SleepTime = (httpServletRequest.getParameter(PropagationConstants.EJB1_SLEEP_TIME_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB1_SLEEP_TIME_OPTION).length() != 0)? Float
            .parseFloat(httpServletRequest.getParameter(PropagationConstants.EJB1_SLEEP_TIME_OPTION)) : 0; // EJB1이 작업을 마무리하기 직전 sleep 시간 (초).
        boolean ejb1ConnectionLeak = httpServletRequest.getParameter(PropagationConstants.EJB1_CONNECTION_LEAK_OPTION) != null; // EJB1의 Connection leak 발생 여부.

        // Propagation 을 받은 EJB1 이 요청받은 작업을 수행하기 위해 필요로 하는 HttpServletRequest 의 parameter 값들을 HashMap 에 저장.
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_USING_TRANSACTION, callerGlobalTransactionDecision);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_CALLEE_NAME, PropagationConstants.EJB1);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_EJB_PROPAGATION_METHOD, helper.getEJBPropagationMethod(ejb1JNDIProviderURL));
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_EJB_CLIENT, ejb1EJBClient);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_1_ID, ejb1DataSource1ID);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_1_QUERY, ejb1DataSource1Query);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_2_ID, ejb1DataSource2ID);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_2_QUERY, ejb1DataSource2Query);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_NUMBER_OF_CONNECTIONS, ejb1NumberOfConnections);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_SLEEP_TIME, ejb1SleepTime);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_CONNECTION_LEAK, ejb1ConnectionLeak);
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_LOGGING_LEVEL, logger.getLoggerLevelName());
        ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB_LOG_COLOR_EMPHASIS, logger.getUseLogColorEmphasis());

        // 수행하여야 할 propagation 작업이 있는지 체크.
        this.doCallerToEJB1Propagation = ejb1UsingPropagation && ejb1EJBClient != null && ejb1EJBClient.equalsIgnoreCase(
            PropagationConstants.CALLER);
        this.ejb1JNDIProviderURL = ejb1JNDIProviderURL;
        this.ejb1JNDILookupParameter = ejb1JNDILookupParameter;
    }

    /**
     * EJB2의 작업에 필요한 field 값 초기화.
     * @param httpServletRequest : 웹 요청으로 전달받은 HttpServletRequest 객체.
     */
    public void initializeEJB2Field(HttpServletRequest httpServletRequest) {
        // EJB2에 대한 propagation 요청 정보.
        boolean ejb2UsingPropagation = httpServletRequest.getParameter(PropagationConstants.EJB2_USING_EJB_OPTION) != null; // EJB2에 대한 Propagation 사용 여부.
        String ejb2JNDIProviderURL = (httpServletRequest.getParameter(PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION) : null; // EJB2를 호출하기 위한 provider url.
        String ejb2JNDILookupParameter = (httpServletRequest.getParameter(PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION) : null; // EJB2의 JNDI lookup name.
        String ejb2EJBClient = (httpServletRequest.getParameter(PropagationConstants.EJB2_EJB_CLIENT_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_EJB_CLIENT_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_EJB_CLIENT_OPTION) : PropagationConstants.CALLER; // EJB2를 호출하는 클라이언트.
        String ejb2DataSource1ID = (httpServletRequest.getParameter(PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_1_ID_OPTION) : null; // EJB2가 사용하는 데이터소스1의 ID.
        String ejb2DataSource1Query = (httpServletRequest.getParameter(PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_1_QUERY_OPTION) : null; // EJB2가 사용하는 데이터소스1의 query.
        String ejb2DataSource2ID = (httpServletRequest.getParameter(PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_2_ID_OPTION) : null; // EJB2가 사용하는 데이터소스2의 ID.
        String ejb2DataSource2Query = (httpServletRequest.getParameter(PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION).length() != 0)? httpServletRequest.getParameter(
            PropagationConstants.EJB2_DATA_SOURCE_2_QUERY_OPTION) : null; // EJB2가 사용하는 데이터소스2의 query.
        int ejb2NumberOfConnections = (httpServletRequest.getParameter(PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION).length() != 0)? Integer
            .parseInt(httpServletRequest.getParameter(PropagationConstants.EJB2_NUMBER_OF_CONNECTIONS_OPTION)) : 1; // EJB2가 한 번에 연결할 Connection 수.
        float ejb2SleepTime = (httpServletRequest.getParameter(PropagationConstants.EJB2_SLEEP_TIME_OPTION) != null && httpServletRequest.getParameter(
            PropagationConstants.EJB2_SLEEP_TIME_OPTION).length() != 0)? Float
            .parseFloat(httpServletRequest.getParameter(PropagationConstants.EJB2_SLEEP_TIME_OPTION)) : 0; // EJB2가 작업을 마무리하기 직전 sleep 시간 (초).
        boolean ejb2ConnectionLeak = httpServletRequest.getParameter(PropagationConstants.EJB2_CONNECTION_LEAK_OPTION) != null; // EJB2의 Connection leak 발생 여부.

        // Propagation 을 받은 EJB2 가 요청받은 작업을 수행하기 위해 필요로 하는 HttpServletRequest 의 parameter 값들을 HashMap 에 저장.
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_USING_TRANSACTION, callerGlobalTransactionDecision);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_CALLEE_NAME, PropagationConstants.EJB2);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_EJB_PROPAGATION_METHOD, helper.getEJBPropagationMethod(ejb2JNDIProviderURL));
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_EJB_CLIENT, ejb2EJBClient);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_1_ID, ejb2DataSource1ID);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_1_QUERY, ejb2DataSource1Query);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_2_ID, ejb2DataSource2ID);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_DATA_SOURCE_2_QUERY, ejb2DataSource2Query);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_NUMBER_OF_CONNECTIONS, ejb2NumberOfConnections);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_SLEEP_TIME, ejb2SleepTime);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_CONNECTION_LEAK, ejb2ConnectionLeak);
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_LOGGING_LEVEL, logger.getLoggerLevelName());
        ejb2HttpServletRequestParameterMap.put(PropagationConstants.EJB_LOG_COLOR_EMPHASIS, logger.getUseLogColorEmphasis());

        // 수행하여야 할 propagation 작업이 있는지 체크.
        this.doCallerToEJB2Propagation = ejb2UsingPropagation && ejb2EJBClient != null && ejb2EJBClient.equalsIgnoreCase(
            PropagationConstants.CALLER);
        this.doEJB1ToEJB2Propagation = ejb2UsingPropagation && ejb2EJBClient != null && ejb2EJBClient.equalsIgnoreCase(
            PropagationConstants.EJB1);
        this.ejb2JNDIProviderURL = ejb2JNDIProviderURL;
        this.ejb2JNDILookupParameter = ejb2JNDILookupParameter;

        // EJB1이 직접 EJB2에게 propagation 을 해야 한다면 EJB2가 deployed 된 서버의 port 를 HashMap 에 추가로 저장.
        if (doEJB1ToEJB2Propagation) {
            ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION, ejb2JNDIProviderURL);
            ejb1HttpServletRequestParameterMap.put(PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION, ejb2JNDILookupParameter);
        }
    }

    /**
     * JDBC & Transaction propagation 작업 요청을 받아 callerNumberOfRequests 의 수 만큼 processSingleRequest 를 호출하여 작업 수행.
     * @return : 전체 JDBC & Transaction propagation 작업의 수행 결과를 약속된 byte 값으로 리턴.
     */
    public byte callRequest() {
        if (callerDataSource1ID == null && callerDataSource2ID == null && !doCallerToEJB1Propagation && !doCallerToEJB2Propagation) { // 데이터소스와 Propagation 작업이 존재하지 않으면 JDBC & Transaction propagation 작업을 진행하지 않음.
            // Caller 의 데이터소스가 입력되지 않았고, Caller 가 직접 Propagation 작업을 수행하지도 않음.
            result = PropagationConstants.NULL_OPERATION_REQUESTED;
        } else if ((doCallerToEJB1Propagation && ejb1JNDILookupParameter == null) || (doCallerToEJB2Propagation && ejb2JNDILookupParameter == null) || doCallerToEJB1Propagation && doEJB1ToEJB2Propagation && ejb2JNDILookupParameter == null) { // EJB propagation 을 수행하는데 JNDI Lookup Parameter 가 존재하지 않으면 JDBC & Transaction propagation 작업을 진행하지 않음.
            // EJB1 또는 EJB2로 Propagation 을 수행하기로 하였으나 JNDI Lookup Parameter 가 입력되지 않음.
            result = PropagationConstants.ADDITIONAL_PARAMETER_REQUIRED;
        } else { // Caller 의 데이터소스 또는 propagation 받을 EJB 가 존재할 때에만 JDBC & Transaction propagation 작업 시작.
            // 서버에 call 정보를 로그로 남김.
            displayRequestInformation();

            for (int i = 0; i < callerNumberOfRequests; i++) {
                // 작업을 병렬로 처리하기 위해 별도의 thread pool 을 만들면 각 thread 는 Servlet Engine 의 관리를 받지 못하게 되므로 모든 작업은 single servlet thread 에서 loop 를 돌며 처리 하는 방식을 채택.
                result = processSingleRequest();

                // JDBC & Transaction propagation 작업 수행 결과가 한 번이라도 성공적이지 않으면 실패로 처리.
                if (result != PropagationConstants.REQUEST_SUCCEEDED) {
                    break;
                }
            }
        }

        // JSP 에 결과값 리턴.
        return result;
    }

    /**
     * 서버에 request 정보를 로그로 남김.
     */
    public void displayRequestInformation() {
        // 서버에 로그 출력.
        logger.log(Level.WARNING, "<<== " + PropagationConstants.CALLER + " received a HTTP servlet request. ==>>");
        logger.log(Level.INFO, "============= 다음 요청을 서버로 전송합니다. =============");
        logger.log(Level.INFO, "Requested Time : " + helper.getCurrentTIme());
        if (callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.NO_TX)) {
            logger.log(Level.INFO, "Global Transaction Decision : 사용 안함");
        } else if (callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.COMMIT) || callerGlobalTransactionDecision.equalsIgnoreCase(
            PropagationConstants.ROLLBACK)) {
            logger.log(Level.INFO, "Global Transaction Decision : " + callerGlobalTransactionDecision);
        }
        if (callerDataSource1ID != null) {
            logger.log(Level.INFO, "Lookup Data Source 1 ID : " + callerDataSource1ID);
            logger.log(Level.INFO, "Data Source 1 Query : " + callerDataSource1Query);
        }
        if (callerDataSource2ID != null) {
            logger.log(Level.INFO, "Lookup Data Source 2 ID : " + callerDataSource2ID);
            logger.log(Level.INFO, "Data Source 2 Query : " + callerDataSource2Query);
        }
        logger.log(Level.INFO, "Number of Connections per Request : " + callerNumberOfConnections);
        logger.log(Level.INFO, "Sleep Time : " + callerSleepTime + " seconds");
        logger.log(Level.INFO, "Number of Requests : " + callerNumberOfRequests);
        logger.log(Level.INFO, "Connection Leak : " + callerConnectionLeak);
        logger.log(Level.INFO, "Logger Level : " + logger.getLoggerLevelName());
        if (doCallerToEJB1Propagation || doCallerToEJB2Propagation) {
            StringBuilder stringBuilder = new StringBuilder();
            if (doCallerToEJB1Propagation) {
                stringBuilder.append(PropagationConstants.EJB1);
                if (doCallerToEJB2Propagation) {
                    stringBuilder.append(", ");
                }
            }
            if (doCallerToEJB2Propagation) {
                stringBuilder.append(PropagationConstants.EJB2);
            }
            logger.log(Level.INFO, "Propagation Callee : " + stringBuilder.toString());
        }
        logger.log(Level.INFO, "==========================================================");
    }

    /**
     * JDBC & Transaction propagation 작업을 한 번 수행.
     * @return : JDBC & Transaction propagation 작업을 한 번 수행한 결과를 약속된 byte 값으로 리턴.
     */
    private byte processSingleRequest() {
        // JDBC & Transaction propagation 작업을 한 번 수행한 결과값.
        byte result;

        // Local InitialContext 선언.
        InitialContext localInitialContext = null;

        // WebSphere 9.0.0.9 버전 기준 트랜잭션 시작 이후에는 remote InitialContext 를 생성하지 못하는 버그가 있음. 따라서 트랜잭션 시작 전 미리 생성해 둘 InitialContext 선언.
        InitialContext ejb1InitialContext = null;
        InitialContext ejb2InitialContext = null;

        // UserTransaction 선언.
        UserTransaction userTransaction = null;

        // Connection 과 Statement 를 담을 ArrayList<> 선언. Connection 과 Statement 를 동시에 사용하기 위해 ArrayList<>를 사용.
        ArrayList<Connection> connectionList1 = new ArrayList<Connection>();
        ArrayList<Connection> connectionList2 = new ArrayList<Connection>();
        ArrayList<Statement> statementList1 = new ArrayList<Statement>();
        ArrayList<Statement> statementList2 = new ArrayList<Statement>();

        try {
            localInitialContext = new InitialContext();

            // WebSphere 9.0.0.9 버전 기준 트랜잭션 시작 이후에는 remote InitialContext 를 생성하지 못하는 버그가 있음. 따라서 트랜잭션 시작 전 InitialContext 를 미리 생성.
            ejb1InitialContext = doCallerToEJB1Propagation? createEJBInitialContextInAdvanceDueToWebSphere(ejb1JNDIProviderURL) : null;
            ejb2InitialContext = doCallerToEJB2Propagation? createEJBInitialContextInAdvanceDueToWebSphere(ejb2JNDIProviderURL) : null;

            // 트랜잭션을 사용하기로 한 경우에 한해 Global Transaction begin.
            if (callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.COMMIT) || callerGlobalTransactionDecision.equalsIgnoreCase(
                PropagationConstants.ROLLBACK)) {
                // UserTransaction JNDI lookup.
                userTransaction = (UserTransaction) localInitialContext.lookup("java:comp/UserTransaction");
                logger.log(Level.FINE, "Starting to begin UserTransaction.");
                userTransaction.begin();
                logger.log(Level.FINE, "Finished to begin UserTransaction.");
            }

            DataSource dataSource1 = null;
            DataSource dataSource2 = null;
            if (callerDataSource1ID != null) {
                dataSource1 = (DataSource) localInitialContext.lookup(callerDataSource1ID);
            }
            if (callerDataSource2ID != null) {
                dataSource2 = (DataSource) localInitialContext.lookup(callerDataSource2ID);
            }

            // 데이터소스1에 대해 JDBC 작업을 수행.
            helper.performDataSourceExecution(callerDataSource1ID, dataSource1, callerNumberOfConnections, callerDataSource1Query, connectionList1, statementList1);

            // 데이터소스2에 대해 JDBC 작업을 수행.
            helper.performDataSourceExecution(callerDataSource2ID, dataSource2, callerNumberOfConnections, callerDataSource2Query, connectionList2, statementList2);

            // EJB 에 대해 propagation 수행.
//            performEJBPropagation();
            // WebSphere 9.0.0.9 버전 기준 트랜잭션 시작 이후에는 remote InitialContext 를 생성하지 못하는 버그가 있음. 따라서 트랜잭션 시작 전 미리 생성해 둔 InitialContext 를 사용하여 EJB propagation 을 수행.
            performEJBPropagation(ejb1InitialContext, ejb2InitialContext);

            // Sleep 시간(초)만큼 sleep.
            performSleep();

            // 트랜잭션을 사용하기로 한 경우에 한해 Global Transaction Decision 수행.
            if (userTransaction != null && callerGlobalTransactionDecision.equalsIgnoreCase(
                PropagationConstants.COMMIT)) {
                logger.log(Level.FINE, "Starting to commit UserTransaction.");
                userTransaction.commit();
                logger.log(Level.FINE, "Finished to commit UserTransaction.");
            } else if (userTransaction != null && callerGlobalTransactionDecision.equalsIgnoreCase(
                PropagationConstants.ROLLBACK)) {
                logger.log(Level.FINE, "Starting to rollback UserTransaction.");
                userTransaction.rollback();
                logger.log(Level.FINE, "Finished to rollback UserTransaction.");
            }

            // JDBC & Transaction propagation 작업 성공.
            result = PropagationConstants.REQUEST_SUCCEEDED;
        } catch (Exception e) {
            // 서버 로그에 stack trace 출력.
            e.printStackTrace();

            // 글로벌 트랜잭션을 사용하기로 한 경우에 한해 Global Transaction rollback.
            if (!callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.NO_TX) && userTransaction != null) {
                logger.log(Level.FINE, "Starting to rollback UserTransaction.");
                try {
                    userTransaction.rollback();
                } catch (Exception se) {
                    se.printStackTrace();
                }
                logger.log(Level.FINE, "Finished to rollback UserTransaction.");
            }

            // JDBC & Transaction propagation 작업 실패.
            result = PropagationConstants.REQUEST_FAILED;
        } finally {
            try {
                // 각 데이터소스에서 얻은 StatementList 들을 정리.
                helper.clearStatementLists(statementList1, statementList2);

                // Connection leak 을 발생시키지 않기로 한 경우에만 Connection close.
                if (!callerConnectionLeak) {
                    // 각 데이터소스에서 얻은 ConnectionList 들을 정리.
                    helper.clearConnectionLists(callerDataSource1ID, connectionList1, callerDataSource2ID, connectionList2);
                }

                if (localInitialContext != null) {
                    localInitialContext.close();
                }
                if (ejb1InitialContext != null) {
                    ejb1InitialContext.close();
                }
                if (ejb2InitialContext != null) {
                    ejb2InitialContext.close();
                }
            } catch (Exception e) {
                // 서버 로그에 stack trace 출력.
                e.printStackTrace();

                // JDBC 리소스 정리 실패.
                result = PropagationConstants.CLEAR_RESOURCES_FAILED;
            }
        }
        return result;
    }

    /**
     * WebSphere 9.0.0.9 버전 기준 트랜잭션 시작 이후에는 remote InitialContext 를 생성하지 못하는 버그가 있음.
     * 따라서 트랜잭션 시작 이전에 remote InitialContext 를 미리 생성할 수 있도록 api 제공.
     * @param ejbJNDIProviderURL : EJB 를 호출하기 위한 provider url.
     * @return : EJB lookup 을 위한 InitialContext 를 만드는데 성공했다면 InitialContext 리턴. 에러 발생 시에는 null 리턴.
     * @throws Exception : EJB lookup 을 위한 InitialContext 생성 과정에서 발생하는 Exception.
     */
    public InitialContext createEJBInitialContextInAdvanceDueToWebSphere(String ejbJNDIProviderURL) throws Exception {
        try {
            if (ejbJNDIProviderURL != null && ejbJNDIProviderURL.length() != 0) {
                Hashtable<String, String> properties = new Hashtable<String, String>();
                properties.put(Context.PROVIDER_URL, ejbJNDIProviderURL);

                return new InitialContext(properties);
            } else {
                // JNDI provider URL 이 없다는 것은 local EJB propagation 을 의미.
                return new InitialContext();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create EJB InitialContext.");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 각 EJB 들에 대해 propagation 을 수행.
     * @throws Exception : EJB propagation 과정에서 발생하는 Exception.
     */
    public void performEJBPropagation() throws Exception {
        // EJB1에 대해 propagation 수행.
        if (doCallerToEJB1Propagation) {
            HashMap<String, Object> ejbPropagationParameterMap = null;

            // EJB1이 직접 EJB2에게 propagation 을 해야 하는지 체크.
            if (doEJB1ToEJB2Propagation) {
                ejbPropagationParameterMap = ejb2HttpServletRequestParameterMap;
            }

            helper.performEJBPropagation(PropagationConstants.EJB1, ejb1JNDIProviderURL, ejb1JNDILookupParameter, ejb1HttpServletRequestParameterMap, ejbPropagationParameterMap);
        }

        // EJB2에 대해 propagation 수행.
        if (doCallerToEJB2Propagation) {
            helper.performEJBPropagation(PropagationConstants.EJB2, ejb2JNDIProviderURL, ejb2JNDILookupParameter, ejb2HttpServletRequestParameterMap, null);
        }
    }

    /**
     * WebSphere 9.0.0.9 버전 기준 트랜잭션 시작 이후에는 remote InitialContext 를 생성하지 못하는 버그가 있음.
     * 따라서 트랜잭션 시작 전 미리 만들어 둔 remote InitialContext 를 parameter 로 받아 각 EJB 들에 대해 propagation 을 수행.
     * @param ejb1InitialContext : EJB1 lookup 을 위한 remote InitialContext.
     * @param ejb2InitialContext : EJB2 lookup 을 위한 remote InitialContext.
     * @throws Exception : EJB propagation 과정에서 발생하는 Exception.
     */
    public void performEJBPropagation(
        InitialContext ejb1InitialContext, InitialContext ejb2InitialContext) throws Exception {
        // EJB1에 대해 propagation 수행.
        if (doCallerToEJB1Propagation) {
            HashMap<String, Object> ejbPropagationParameterMap = null;

            // EJB1이 직접 EJB2에게 propagation 을 해야 하는지 체크.
            if (doEJB1ToEJB2Propagation) {
                ejbPropagationParameterMap = ejb2HttpServletRequestParameterMap;
            }

            helper.performEJBPropagation(PropagationConstants.EJB1, ejb1JNDIProviderURL, ejb1JNDILookupParameter, ejb1HttpServletRequestParameterMap, ejbPropagationParameterMap, ejb1InitialContext);
        }

        // EJB2에 대해 propagation 수행.
        if (doCallerToEJB2Propagation) {
            helper.performEJBPropagation(PropagationConstants.EJB2, ejb2JNDIProviderURL, ejb2JNDILookupParameter, ejb2HttpServletRequestParameterMap, null, ejb2InitialContext);
        }
    }

    /**
     * 요청된 sleep 시간(초)만큼 sleep.
     * @throws InterruptedException : Sleep 과정에서 발생하는 Exception.
     */
    public void performSleep() throws InterruptedException {
        // Sleep 시간(초)만큼 sleep.
        if (callerSleepTime > 0) {
            String message;
            if (callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.NO_TX)) {
                message = ".";
            } else {
                message = " before " + callerGlobalTransactionDecision.toLowerCase() + ".";
            }

            logger.log(Level.WARNING, "Starting to sleep for " + callerSleepTime + " second(s)" + message);
            Thread.sleep((int) (callerSleepTime * 1000));
            logger.log(Level.WARNING, "Finished to sleep for " + callerSleepTime + " second(s)" + message);
        }
    }
}