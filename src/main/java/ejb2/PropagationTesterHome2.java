package ejb2;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Created by LCC.
 */
public interface PropagationTesterHome2 extends EJBHome {
    PropagationTester2 create() throws CreateException, RemoteException;
}
