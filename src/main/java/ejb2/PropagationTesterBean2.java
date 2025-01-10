package ejb2;

/**
 * Created by LCC.
 */

import constants.PropagationConstants;
import helper.PropagationHelper;
import logging.Logger;

import jakarta.ejb.EJBException;
import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class PropagationTesterBean2 implements SessionBean {
    // JDBC & Transaction propagation 작업 수행을 도와줄 helper 선언.
    PropagationHelper helper;

    // Logger 선언.
    private Logger logger;

    // JDBC & Transaction 작업을 위한 필드 선언.
    private String callerGlobalTransactionDecision; // EJB 를 호출한 Caller 가 Client Managed Transaction (CMT) 을  사용했는지 여부.
    private String ejbCalleeName; // Propagation 을 받은 EJB 의 이름.
    private String ejbEJBPropagationMethod; // EJB 에 대한 propagation 전파 방식.
    private String ejbEJBClient; // EJB 를 호출한 클라이언트.
    private String ejbDataSource1ID; // EJB 가 사용하는 데이터소스1의 ID.
    private String ejbDataSource1Query; // EJB 가 사용하는 데이터소스1의 query.
    private String ejbDataSource2ID; // EJB 가 사용하는 데이터소스2의 ID.
    private String ejbDataSource2Query; // EJB 가 사용하는 데이터소스2의 query.
    private int ejbNumberOfConnections; // EJB 가 한 번에 연결할 Connection 수.
    private float ejbSleepTime; // EJB 가 작업을 마무리하기 직전 sleep 시간 (초).
    private boolean ejbConnectionLeak; // EJB 의 Connection leak 발생 여부.

    boolean doEJB1ToEJB2Propagation; // EJB2 에 대해 EJB1 이 수행하여야 할 Propagation 작업이 있는지 여부.
    String ejb2JNDIProviderURL; // EJB2를 호출하기 위한 provider url.
    String ejb2JNDILookupParameter; // EJB2의 JNDI lookup name.

    /**
     * EJB 에서 JDBC & Transaction 작업을 수행하는 API.
     * @param ejbHttpServletRequestParameterMap : Propagation 을 받은 EJB 가 요청된 작업을 수행하는데 필요한 정보가 담겨있는 HashMap.
     * @param ejbPropagationParameterMap : Propagation 을 받은 EJB1 이 또 다시 EJB2 에게 propagation 을 해야 할 때 EJB2가 요청된 작업을 수행하는데 필요한 정보가 담겨있는 HashMap. 만약 EJB1이 EJB2에게 propagation 을 하지 않는다면 null.
     * @throws RemoteException : EJB 에서 JDBC & Transaction 작업을 실패했을 때 발생시키는 Exception.
     */
    public void callEJB(
        HashMap<String, Object> ejbHttpServletRequestParameterMap, HashMap<String, Object> ejbPropagationParameterMap) throws RemoteException {
        // EJB 의 작업에 필요한 field 값 초기화.
        initializeEJBField(ejbHttpServletRequestParameterMap);

        // 서버에 로그 출력.
        logger.log(
            Level.WARNING, "<<== " + ejbCalleeName + " received a(n) " +  ejbEJBPropagationMethod + " propagation from " + ejbEJBClient + ". ==>>");

        if (ejbDataSource1ID == null && ejbDataSource2ID == null && !doEJB1ToEJB2Propagation && ejbSleepTime <= 0) {
            logger.log(Level.INFO, "데이터소스를 사용하지도 EJB 전파를 수행하지도 Sleep 하지도 않아 요청을 종료합니다.");
            return;
        } else {
            // 서버에 call 정보를 로그로 남김.
            displayRequestInformation();
        }

        // Connection 과 Statement 를 담을 ArrayList<> 선언.
        ArrayList<Connection> connectionList1 = new ArrayList<Connection>();
        ArrayList<Connection> connectionList2 = new ArrayList<Connection>();
        ArrayList<Statement> statementList1 = new ArrayList<Statement>();
        ArrayList<Statement> statementList2 = new ArrayList<Statement>();

        try {
            InitialContext localInitialContext = new InitialContext();
            DataSource dataSource1 = null;
            DataSource dataSource2 = null;
            if (ejbDataSource1ID != null) {
                dataSource1 = (DataSource) localInitialContext.lookup(ejbDataSource1ID);
            }
            if (ejbDataSource2ID != null) {
                dataSource2 = (DataSource) localInitialContext.lookup(ejbDataSource2ID);
            }

            // 데이터소스1에 대해 JDBC 작업을 수행.
            helper.performDataSourceExecution(ejbDataSource1ID, dataSource1, ejbNumberOfConnections, ejbDataSource1Query, connectionList1, statementList1);

            // 데이터소스2에 대해 JDBC 작업을 수행.
            helper.performDataSourceExecution(ejbDataSource2ID, dataSource2, ejbNumberOfConnections, ejbDataSource2Query, connectionList2, statementList2);

            // Sleep 시간(초)만큼 sleep.
            performSleep();

            // EJB1이 직접 EJB2에게 propagation 수행.
            if (doEJB1ToEJB2Propagation) {
                try {
                    helper.performEJBPropagation(PropagationConstants.EJB2, ejb2JNDIProviderURL,
                        ejb2JNDILookupParameter, ejbPropagationParameterMap, null);
                }catch(Exception e){
                    if (e instanceof jakarta.transaction.TransactionRolledbackException){
                        System.out.println(">>>>>javax.transaction.TransactionRolledbackException<<<<<");
                    }/*else if(e instanceof org.omg.CORBA.TRANSACTION_ROLLEDBACK){
                        System.out.println(">>>>>org.omg.CORBA.TRANSACTION_ROLLEDBACK<<<<<");
                    }*/
                }
            }
        } catch (Exception e) {
            // 서버 로그에 stack trace 출력.
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        } finally {
            try {
                // 각 데이터소스에서 얻은 Statement 들에 대해 close 호출.
                helper.clearStatementLists(statementList1, statementList2);

                // Connection leak 을 발생시키지 않기로 한 경우에만 Connection close.
                if (!ejbConnectionLeak) {
                    // 각 데이터소스에서 얻은 Connection 들에 대해 close 호출.
                    helper.clearConnectionLists(ejbDataSource1ID, connectionList1, ejbDataSource2ID, connectionList2);
                }
            } catch (Exception e) {
                // 서버 로그에 stack trace 출력.
                e.printStackTrace();
            }
        }
    }

    /**
     * EJB 에서 JDBC & Transaction 작업을 위해 전달된 parameter 값 parsing.
     * @param ejbHttpServletRequestParameterMap : EJB 에 대해 요청된 HttpServletRequest parameter 들을 담은 HashMap 객체.
     */
    private void initializeEJBField(HashMap<String, Object> ejbHttpServletRequestParameterMap) {
        this.callerGlobalTransactionDecision = (String) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_USING_TRANSACTION);
        this.ejbCalleeName = (String) ejbHttpServletRequestParameterMap.get(PropagationConstants.EJB_CALLEE_NAME);
        this.ejbEJBPropagationMethod = (String) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_EJB_PROPAGATION_METHOD);
        this.ejbEJBClient = (String) ejbHttpServletRequestParameterMap.get(PropagationConstants.EJB_EJB_CLIENT);
        this.ejbDataSource1ID = (String) ejbHttpServletRequestParameterMap.get(PropagationConstants.EJB_DATA_SOURCE_1_ID);
        this.ejbDataSource1Query = (String) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_DATA_SOURCE_1_QUERY);
        this.ejbDataSource2ID = (String) ejbHttpServletRequestParameterMap.get(PropagationConstants.EJB_DATA_SOURCE_2_ID);
        this.ejbDataSource2Query = (String) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_DATA_SOURCE_2_QUERY);
        this.ejbNumberOfConnections = (Integer) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_NUMBER_OF_CONNECTIONS);
        this.ejbSleepTime = (Float) ejbHttpServletRequestParameterMap.get(PropagationConstants.EJB_SLEEP_TIME);
        this.ejbConnectionLeak = (Boolean) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_CONNECTION_LEAK);

        // Logger 에 설정해 둘 logging level 과 log color emphasis 사용 여부를 얻어 Logger 객체 생성.
        String ejbLoggerLevel = (String) ejbHttpServletRequestParameterMap.get(PropagationConstants.EJB_LOGGING_LEVEL); // EJB 의 Logger 에 설정해 둘 logging level.
        boolean ejbLogColorEmphasis =  (Boolean) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB_LOG_COLOR_EMPHASIS); // EJB 에서 출력되는 로그에 색깔을 입혀 강조 효과를 줄 것인지 여부.
        this.logger = new Logger(ejbLoggerLevel, ejbLogColorEmphasis);

        // JDBC & Transaction propagation 작업 수행을 도와줄 helper 객체 생성.
        this.helper = new PropagationHelper(logger);

        // EJB1이 직접 EJB2에게 propagation 을 수행할 경우 필요한 정보 저장.
        this.ejb2JNDIProviderURL = (String) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB2_JNDI_PROVIDER_URL_OPTION);
        this.ejb2JNDILookupParameter = (String) ejbHttpServletRequestParameterMap.get(
            PropagationConstants.EJB2_JNDI_LOOKUP_PARAMETER_OPTION);
        this.doEJB1ToEJB2Propagation = this.ejb2JNDILookupParameter != null;
    }

    /**
     * 서버에 request 정보를 로그로 남김.
     */
    public void displayRequestInformation() {
        logger.log(Level.INFO, "============= 다음 요청을 서버로 전송합니다. =============");
        logger.log(Level.INFO, "Requested Time : " + helper.getCurrentTIme());
        if (callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.NO_TX)) {
            logger.log(Level.INFO, "Global Transaction Decision : 사용 안함");
        } else if (callerGlobalTransactionDecision.equalsIgnoreCase(PropagationConstants.COMMIT) || callerGlobalTransactionDecision.equalsIgnoreCase(
            PropagationConstants.ROLLBACK)) {
            logger.log(Level.INFO, "Global Transaction Decision : " + callerGlobalTransactionDecision);
        }
        if (ejbDataSource1ID != null) {
            logger.log(Level.INFO, "Lookup Data Source 1 ID : " + ejbDataSource1ID);
            logger.log(Level.INFO, "Data Source 1 Query : " + ejbDataSource1Query);
        }
        if (ejbDataSource2ID != null) {
            logger.log(Level.INFO, "Lookup Data Source 2 ID : " + ejbDataSource2ID);
            logger.log(Level.INFO, "Data Source 2 Query : " + ejbDataSource2Query);
        }
        logger.log(Level.INFO, "Number of Connections per Request : " + ejbNumberOfConnections);
        logger.log(Level.INFO, "Sleep Time : " + ejbSleepTime + " seconds");
        logger.log(Level.INFO, "Connection Leak : " + ejbConnectionLeak);
        logger.log(Level.INFO, "Logger Level : " + logger.getLoggerLevelName());
        if (doEJB1ToEJB2Propagation) {
            logger.log(Level.INFO, "Propagation Callee : " + PropagationConstants.EJB2);
        }
        logger.log(Level.INFO, "==========================================================");
    }

    /**
     * 요청된 sleep 시간(초)만큼 sleep.
     * @throws InterruptedException : Sleep 과정에서 발생하는 Exception.
     */
    public void performSleep() throws InterruptedException {
        // Sleep 시간(초)만큼 sleep.
        if (ejbSleepTime > 0) {
            logger.log(Level.WARNING, "Starting to sleep for " + ejbSleepTime + " second(s) before sending response to " + ejbEJBClient + ".");
            Thread.sleep((int) (ejbSleepTime * 1000));
            logger.log(Level.WARNING, "Finished to sleep for " + ejbSleepTime + " second(s) before sending response to " + ejbEJBClient + ".");
        }
    }

    @Override
    public void setSessionContext(SessionContext sessionContext) throws EJBException, RemoteException {

    }

    @Override
    public void ejbRemove() throws EJBException, RemoteException {

    }

    @Override
    public void ejbActivate() throws EJBException, RemoteException {

    }

    @Override
    public void ejbPassivate() throws EJBException, RemoteException {

    }
}
