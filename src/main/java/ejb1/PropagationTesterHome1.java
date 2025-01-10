package ejb1;

import jakarta.ejb.EJBHome;
import java.rmi.RemoteException;
import jakarta.ejb.CreateException;

/**
 * Created by LCC.
 */
public interface PropagationTesterHome1 extends EJBHome {
    PropagationTester1 create() throws CreateException, RemoteException;
}
