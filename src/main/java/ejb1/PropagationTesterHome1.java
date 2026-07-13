package ejb1;

import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

/**
 * Created by LCC.
 */
public interface PropagationTesterHome1 extends EJBHome {
    PropagationTester1 create() throws CreateException, RemoteException;
}
