package helper;

import constants.PropagationConstants;
import ejb1.PropagationTester1;
import ejb1.PropagationTesterHome1;
import ejb2.PropagationTester2;
import ejb2.PropagationTesterHome2;
import logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;

/**
 * Created by LCC.
 */
public class PropagationHelper {
    // Logger 선언.
    private Logger logger;

    public PropagationHelper(Logger logger) {
        this.logger = logger;
    }

    /**
     * 현재 시간을 주어진 format 형태로 계산.
     * @return : 주어진 format 형태로 계산된 현재 시간.
     */
    public String getCurrentTIme() {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return simpleDateFormat.format(new Date(currentTimeMillis));
    }

    /**
     * EJB 를 호출하는 방식이 RMI 인지, IIOP 인지 판단.
     * @param jndiProviderURL : EJB 를 호출하기 위한 provider url.
     * @return : EJB 를 호출하는 방식이 RMI 인지, IIOP 인지 여부.
     */
    public String getEJBPropagationMethod(String jndiProviderURL) {
        if (jndiProviderURL == null) {
            return PropagationConstants.RMI;
        } else {
            return !jndiProviderURL.toUpperCase().contains(
                PropagationConstants.IIOP) ? PropagationConstants.RMI : PropagationConstants.IIOP;
        }
    }

    /**
     * 주어진 데이터소스에 대해 JDBC 작업을 수행.
     * @param callerDataSourceID : 데이터소스 ID.
     * @param dataSource : 데이터소스 객체.
     * @param numberOfConnections : 동시에 사용할 Connection 수.
     * @param callerDataSourceQuery : 데이터소스 query.
     * @param connectionList : Connection 을 담은 ArrayList<>.
     * @param statementList : Statement 를 담은 ArrayList<>.
     * @throws SQLException : JDBC 작업 수행 과정에서 발생하는 Exception.
     */
    public void performDataSourceExecution(String callerDataSourceID, DataSource dataSource, int numberOfConnections, String callerDataSourceQuery, ArrayList<Connection> connectionList, ArrayList<Statement> statementList) throws SQLException {
        // 데이터소스에 대해 한 번에 연결할 Connection 수 만큼 getConnection,
        fillConnectionList(callerDataSourceID, dataSource, numberOfConnections, connectionList);

        // 데이터소스에서 얻은 Connection 들에 대해 createStatement.
        fillStatementList(callerDataSourceID, connectionList, statementList);

        // 데이터소스에서 얻은 Statement 들에 대해 execute.
        executeSQLQuery(callerDataSourceID, statementList, callerDataSourceQuery);
    }

    /**
     * 데이터소스에 대해 한 번에 연결할 Connection 수 만큼 getConnection,
     * @param dataSourceID : 데이터소스 ID.
     * @param dataSource : 데이터소스 ID에 대해 JNDI lookup 해온 데이터소스 객체.
     * @param numberOfConnections : 동시에 사용할 Connection 수.
     * @param connectionList : 데이터소스 ID에 해당하는 데이터소스 대해 getConnection 요청으로 얻은 Connection 들을 담은 ArrayList<Connection>.
     * @throws SQLException : DataSource # getConnection 에 실패하였을 때 발생하는 Exception.
     */
    public void fillConnectionList(String dataSourceID, DataSource dataSource, int numberOfConnections, ArrayList<Connection> connectionList) throws SQLException {
        if (dataSource != null) {
            logger.log(Level.FINE, "Starting to get " + numberOfConnections + " connection(s) for data source [" + dataSourceID + "].");
            for (int i = 0; i < numberOfConnections; i++) {
                connectionList.add(dataSource.getConnection());
            }
            logger.log(Level.FINE, "Finished to get " + numberOfConnections + " connection(s) for data source [" + dataSourceID + "].");
        }
    }

    /**
     * 데이터소스에서 얻은 Connection 들에 대해 createStatement.
     * @param dataSourceID : 데이터소스 ID.
     * @param connectionList : 데이터소스 ID에 해당하는 데이터소스 대해 getConnection 요청으로 얻은 Connection 들을 담은 ArrayList<Connection>.
     * @param statementList : 데이터소스 ID에 해당하는 데이터소스에게서 얻은 Connection 들에 대해 createStatement 요청으로 얻은 Statement 들을 담은 ArrayList<Statement>.
     * @throws SQLException : Connection # createStatement 에 실패하였을 때 발생하는 Exception.
     */
    public void fillStatementList(String dataSourceID, ArrayList<Connection> connectionList, ArrayList<Statement> statementList) throws SQLException {
        if (connectionList.size() != 0) {
            logger.log(Level.FINE, "Starting to get " + connectionList.size() + " statement(s) for data source [" + dataSourceID + "].");
            for (Connection connection : connectionList) {
                statementList.add(connection.createStatement());
            }
            logger.log(Level.FINE, "Finished to get " + connectionList.size() + " statement(s) for data source [" + dataSourceID + "].");
        }
    }

    /**
     * 데이터소스에서 얻은 Statement 들에 대해 execute.
     * @param dataSourceID : 데이터소스 ID.
     * @param statementList : 데이터소스 ID에 해당하는 데이터소스에게서 얻은 Connection 들에 대해 createStatement 요청으로 얻은 Statement 들을 담은 ArrayList<Statement>.
     * @param dataSourceQuery : 데이터소스 ID에 해당하는 statement 들에 대해 수행할 SQL query.
     * @throws SQLException : Statement # execute 에 실패하였을 때 발생하는 Exception.
     */
    public void executeSQLQuery(
        String dataSourceID, ArrayList<Statement> statementList, String dataSourceQuery) throws SQLException {
        if (statementList.size() != 0 && dataSourceQuery != null) {
            logger.log(Level.FINE, "Starting to execute " + statementList.size() + " query(s) for data source [" + dataSourceID + "].");
            for (Statement statement : statementList) {
                boolean noUpdate = statement.execute(dataSourceQuery);
                int updatedRowsCount;
                boolean isEmptyResult = true;

                while (true) {
                    if (noUpdate) { // SELECT query 와 같이 row update 를 하지 않는 SQL 이 execute 되었을 때.
                        ResultSet resultSet = null;
                        try {
                            resultSet = statement.getResultSet();
                            while (resultSet.next()) {
                                Object object = resultSet.getObject(1);
                                logger.log(Level.WARNING, "There is a value [" + object + "] in the first column. Query : [" + dataSourceQuery.toUpperCase() + "].");
                                isEmptyResult = false;
                            }
                        } finally {
                            if (resultSet != null) {
                                resultSet.close();
                            }
                        }
                    } else { // INSERT query 와 같이 row update 를 하는 SQL 이 execute 되었을 때.
                        updatedRowsCount = statement.getUpdateCount();
                        if (updatedRowsCount == -1) {
                            if (isEmptyResult) {
                                logger.log(Level.WARNING, "The table is empty. Query : [" + dataSourceQuery.toUpperCase() + "].");
                            }
                            break;
                        } else {
                            logger.log(
                                Level.WARNING, updatedRowsCount + " rows are updated. Query : [" + dataSourceQuery.toUpperCase() + "].");
                            isEmptyResult = false;
                        }
                    }
                    noUpdate = statement.getMoreResults();
                }
            }
            logger.log(Level.FINE, "Finished to execute " + statementList.size() + " query(s) for data source [" + dataSourceID + "].");
        }
    }

    /**
     * 요청받은 방식에 맞게 요청받은 서버로의 EJB propagation 수행.
     * @param ejbCalleeName : EJB propagation 을 EJB1 과 EJB2 중 어느 곳으로 수행할 것인지에 대한 값.
     * @param ejbJNDIProviderURL : EJB 를 호출하기 위한 provider url.
     * @param ejbJNDILookupParameter : EJB 의 JNDI lookup name.
     * @param ejbHttpServletRequestParameterMap : Propagation 을 받을 EJB 가 요청된 작업을 수행하는데 필요한 정보가 담겨있는 HashMap.
     * @param ejbPropagationParameterMap : Propagation 을 받을 EJB1 이 또 다시 EJB2 에게 propagation 을 해야 할 때 EJB2가 요청된 작업을 수행하는데 필요한 정보가 담겨있는 HashMap. 만약 EJB1이 EJB2에게 propagation 을 하지 않는다면 null.
     * @throws Exception : EJB propagation 과정에서 발생하는 Exception.
     */
    public void performEJBPropagation(
        String ejbCalleeName, String ejbJNDIProviderURL, String ejbJNDILookupParameter, HashMap<String, Object> ejbHttpServletRequestParameterMap, HashMap<String, Object> ejbPropagationParameterMap) throws Exception {
        Hashtable<String, String> properties = new Hashtable<String, String>();
        if (ejbJNDIProviderURL != null) {
            properties.put(Context.PROVIDER_URL, ejbJNDIProviderURL);
        }

        InitialContext remoteInitialContext = new InitialContext(properties);
        performEJBPropagation(ejbCalleeName, ejbJNDIProviderURL, ejbJNDILookupParameter, ejbHttpServletRequestParameterMap, ejbPropagationParameterMap, remoteInitialContext);
    }

    /**
     * 요청받은 방식에 맞게 요청받은 서버로의 EJB propagation 수행.
     * @param ejbCalleeName : EJB propagation 을 EJB1 과 EJB2 중 어느 곳으로 수행할 것인지에 대한 값.
     * @param ejbJNDIProviderURL : EJB 를 호출하기 위한 provider url.
     * @param ejbJNDILookupParameter : EJB 의 JNDI lookup name.
     * @param ejbHttpServletRequestParameterMap : Propagation 을 받을 EJB 가 요청된 작업을 수행하는데 필요한 정보가 담겨있는 HashMap.
     * @param ejbPropagationParameterMap : Propagation 을 받을 EJB1 이 또 다시 EJB2 에게 propagation 을 해야 할 때 EJB2가 요청된 작업을 수행하는데 필요한 정보가 담겨있는 HashMap. 만약 EJB1이 EJB2에게 propagation 을 하지 않는다면 null.
     * @param ejbInitialContext : EJB lookup 을 위한 InitialContext.
     * @throws Exception : EJB propagation 과정에서 발생하는 Exception.
     */
    public void performEJBPropagation(
        String ejbCalleeName, String ejbJNDIProviderURL, String ejbJNDILookupParameter, HashMap<String, Object> ejbHttpServletRequestParameterMap, HashMap<String, Object> ejbPropagationParameterMap, InitialContext ejbInitialContext) throws Exception {
        // EJB InitialContext 가 null 이면 propagation 작업 생략.
        if (ejbInitialContext == null) {
            logger.log(Level.WARNING, "Unable to perform EJB propagation because InitialContext is null.");
            return;
        }

        if (ejbJNDILookupParameter == null || ejbJNDILookupParameter.length() == 0) {
            logger.log(Level.WARNING, "JNDI lookup parameter for EJB is not specified.");
            throw new Exception("JNDI lookup parameter for EJB is not specified.");
        }

        try {
            Object object = ejbInitialContext.lookup(ejbJNDILookupParameter);

            String ejbEJBPropagationMethod = getEJBPropagationMethod(ejbJNDIProviderURL); // EJB propagation 을 RMI 방식과 IIOP 방식 중 어느 것으로 수행할 것인지에 대한 값.
            logger.log(Level.WARNING, "Starting to call " + ejbCalleeName + " using " + ejbEJBPropagationMethod + ".");
            if(ejbJNDILookupParameter.equals("ejb1")){
//                PropagationTesterHome1 testerEJBHome = (PropagationTesterHome1) PortableRemoteObject.narrow(object, PropagationTesterHome1.class);
                PropagationTesterHome1 testerEJBHome = (PropagationTesterHome1)object;
                PropagationTester1 testerEJB = testerEJBHome.create();
                testerEJB.callEJB(ejbHttpServletRequestParameterMap, ejbPropagationParameterMap);
            }else{
                PropagationTesterHome2 testerEJBHome = (PropagationTesterHome2)object;
                PropagationTester2 testerEJB = testerEJBHome.create();
                testerEJB.callEJB(ejbHttpServletRequestParameterMap, ejbPropagationParameterMap);
            }
            logger.log(Level.WARNING, "Finished to call " + ejbCalleeName + " using " + ejbEJBPropagationMethod + ".");
        } finally{
            ejbInitialContext.close();
        }
    }

    /**
     * 각 statementList 에 대해 closeStatements(ArrayList<Statement>) 를 호출.
     * @param statementList1 : connectionList1 로부터 생성된 Statement 들을 담은 ArrayList<Statement>.
     * @param statementList2 : connectionList2 로부터 생성된 Statement 들을 담은 ArrayList<Statement>.
     * @throws SQLException : Statement # close 에 실패하였을 때 발생하는 Exception.
     */
    public void clearStatementLists(
        ArrayList<Statement> statementList1, ArrayList<Statement> statementList2) throws SQLException {
        // 데이터소스1에서 얻은 Statement 들에 대해 close.
        closeStatements(statementList1);

        // 데이터소스2에서 얻은 Statement 들에 대해 close.
        closeStatements(statementList2);

        // statementList clear.
        statementList1.clear();
        statementList2.clear();
    }

    /**
     * statementList 에 담긴 Statement 들을 close.
     * @param statementList : Statement 들을 담은 ArrayList<Statement>.
     * @throws SQLException : Statement # close 에 실패하였을 때 발생하는 Exception.
     */
    private void closeStatements(ArrayList<Statement> statementList) throws SQLException {
        for (Statement statement : statementList) {
            statement.close();
        }
    }

    /**
     * 각 connectionList 에 대해 closeConnections(ArrayList<Connection>) 를 호출.
     * @param dataSourceID1 : 데이터소스1 ID
     * @param connectionList1 : 데이터소스1에 해당하는 데이터소스 대해 getConnection 요청으로 얻은 Connection 들을 담은 ArrayList<Connection>.
     * @param dataSourceID2 : 데이터소스2 ID
     * @param connectionList2 : 데이터소스2에 해당하는 데이터소스 대해 getConnection 요청으로 얻은 Connection 들을 담은 ArrayList<Connection>.
     * @throws SQLException : Connection # close 에 실패하였을 때 발생하는 Exception.
     */
    public void clearConnectionLists(String dataSourceID1, ArrayList<Connection> connectionList1, String dataSourceID2, ArrayList<Connection> connectionList2) throws SQLException {
        // 데이터소스1에서 얻은 Connection 들에 대해 close.
        closeConnections(dataSourceID1, connectionList1);

        // 데이터소스2에서 얻은 Connection 들에 대해 close.
        closeConnections(dataSourceID2, connectionList2);

        // connectionList clear.
        connectionList1.clear();
        connectionList2.clear();
    }

    /**
     * connectionList 에 담긴 Connection 들을 close.
     * @param dataSourceID : 데이터소스 ID.
     * @param connectionList : Connection 들을 담은 ArrayList<Connection>.
     * @throws SQLException : Connection # close 에 실패하였을 때 발생하는 Exception.
     */
    private void closeConnections(
        String dataSourceID, ArrayList<Connection> connectionList) throws SQLException {
        if (!connectionList.isEmpty()) {
            logger.log(Level.FINE, "Starting to close " + connectionList.size() + " connection(s) for data source [" + dataSourceID + "].");
            for (Connection connection : connectionList) {
                connection.close();
            }
            logger.log(Level.FINE, "Finished to close " + connectionList.size() + " connection(s) for data source [" + dataSourceID + "].");
        }
    }
}
