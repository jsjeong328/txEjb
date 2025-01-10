package ejb1;

import jakarta.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface PropagationTester1 extends EJBObject {
    void callEJB(HashMap<String, Object> ejbHttpServletRequestParameterMap,
        HashMap<String, Object> ejbPropagationParameterMap) throws RemoteException;
}
