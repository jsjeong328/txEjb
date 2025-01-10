package constants;

/**
 * Created by LCC.
 */
public class PropagationConstants {
    // Caller Parameter 선언.
    public static final String CALLER_GLOBAL_TRANSACTION_DECISION = "CallerTXDecision";
    public static final String CALLER_DATA_SOURCE_1_ID_OPTION = "CallerDS1ID";
    public static final String CALLER_DATA_SOURCE_1_QUERY_OPTION = "CallerDS1Query";
    public static final String CALLER_DATA_SOURCE_2_ID_OPTION = "CallerDS2ID";
    public static final String CALLER_DATA_SOURCE_2_QUERY_OPTION = "CallerDS2Query";
    public static final String CALLER_NUMBER_OF_CONNECTIONS_OPTION = "CallerNumOfConnections";
    public static final String CALLER_SLEEP_TIME_OPTION = "CallerSleepTime";
    public static final String CALLER_NUMBER_OF_REQUESTS_OPTION = "CallerNumOfRequests";
    public static final String CALLER_CONNECTION_LEAK_OPTION = "CallerConnectionLeak";
    public static final String CALLER_LOGGING_LEVEL_OPTION = "CallerLoggingLevel";
    public static final String CALLER_LOG_COLOR_EMPHASIS_OPTION = "CallerLogColorEmphasis";

    // EJB1 Parameter 선언.
    public static final String EJB1_USING_EJB_OPTION = "EJB1UseEJB";
    public static final String EJB1_JNDI_PROVIDER_URL_OPTION = "EJB1ProviderURL";
    public static final String EJB1_JNDI_LOOKUP_PARAMETER_OPTION = "EJB1LookupParameter";
    public static final String EJB1_EJB_CLIENT_OPTION = "EJB1Client";
    public static final String EJB1_DATA_SOURCE_1_ID_OPTION = "EJB1DS1ID";
    public static final String EJB1_DATA_SOURCE_1_QUERY_OPTION = "EJB1DS1Query";
    public static final String EJB1_DATA_SOURCE_2_ID_OPTION = "EJB1DS2ID";
    public static final String EJB1_DATA_SOURCE_2_QUERY_OPTION = "EJB1DS2Query";
    public static final String EJB1_NUMBER_OF_CONNECTIONS_OPTION = "EJB1NumOfConnections";
    public static final String EJB1_SLEEP_TIME_OPTION = "EJB1SleepTime";
    public static final String EJB1_CONNECTION_LEAK_OPTION = "EJB1ConnectionLeak";

    // EJB2 Parameter 선언.
    public static final String EJB2_USING_EJB_OPTION = "EJB2UseEJB";
    public static final String EJB2_JNDI_PROVIDER_URL_OPTION = "EJB2ProviderURL";
    public static final String EJB2_JNDI_LOOKUP_PARAMETER_OPTION = "EJB2LookupParameter";
    public static final String EJB2_EJB_CLIENT_OPTION = "EJB2Client";
    public static final String EJB2_DATA_SOURCE_1_ID_OPTION = "EJB2DS1ID";
    public static final String EJB2_DATA_SOURCE_1_QUERY_OPTION = "EJB2DS1Query";
    public static final String EJB2_DATA_SOURCE_2_ID_OPTION = "EJB2DS2ID";
    public static final String EJB2_DATA_SOURCE_2_QUERY_OPTION = "EJB2DS2Query";
    public static final String EJB2_NUMBER_OF_CONNECTIONS_OPTION = "EJB2NumOfConnections";
    public static final String EJB2_SLEEP_TIME_OPTION = "EJB2SleepTime";
    public static final String EJB2_CONNECTION_LEAK_OPTION = "EJB2ConnectionLeak";

    // 각 EJB 에 전달되는 HttpServletRequest parameter map 에 공통적으로 사용될 key 값 선언.
    public static final String EJB_USING_TRANSACTION = "EJBUseTX";
    public static final String EJB_CALLEE_NAME = "EJBCalleeName";
    public static final String EJB_EJB_PROPAGATION_METHOD = "EJBPropagationMethod";
    public static final String EJB_EJB_CLIENT = "EJBClient";
    public static final String EJB_DATA_SOURCE_1_ID = "EJBDS1ID";
    public static final String EJB_DATA_SOURCE_1_QUERY = "EJBDS1Query";
    public static final String EJB_DATA_SOURCE_2_ID = "EJBDS2ID";
    public static final String EJB_DATA_SOURCE_2_QUERY = "EJBDS2Query";
    public static final String EJB_NUMBER_OF_CONNECTIONS = "EJBNumOfConnections";
    public static final String EJB_SLEEP_TIME = "EJBSleepTime";
    public static final String EJB_CONNECTION_LEAK = "EJBConnectionLeak";
    public static final String EJB_LOGGING_LEVEL = "EJBLoggingLevel";
    public static final String EJB_LOG_COLOR_EMPHASIS = "EJBLogColorEmphasis";

    // 글로벌 트랜잭션의 결정 선언.
    public static final String NO_TX = "NoTx";
    public static final String COMMIT = "Commit";
    public static final String ROLLBACK = "Rollback";

    // EJB Propagation Method 선언.
    public static final String RMI = "RMI";
    public static final String IIOP = "IIOP";

    // EJB Client 선언.
    public static final String CALLER = "Caller";
    public static final String EJB1 = "EJB1";
    public static final String EJB2 = "EJB2";

    // Return 값 선언.
    public static final byte BEFORE_PROCESSING_REQUEST = 0;
    public static final byte REQUEST_SUCCEEDED = 1;
    public static final byte REQUEST_FAILED = 2;
    public static final byte CLEAR_RESOURCES_FAILED = 3;
    public static final byte NULL_OPERATION_REQUESTED = 4;
    public static final byte ADDITIONAL_PARAMETER_REQUIRED = 5;
}
