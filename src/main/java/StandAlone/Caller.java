package StandAlone;

import constants.PropagationConstants;
import ejb1.PropagationTester1;
import ejb1.PropagationTesterHome1;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.HashMap;
import java.util.Hashtable;

public class Caller {
    public static void main(String[] args) {
        try {
            Hashtable<String, String> ht = new Hashtable<String, String>();
            ht.put(Context.INITIAL_CONTEXT_FACTORY, "jeus.jndi.JNSContextFactory");
            ht.put(Context.URL_PKG_PREFIXES, "jeus.jndi.jns.url");
            ht.put(Context.PROVIDER_URL, "192.168.14.99:19736");
            ht.put(Context.SECURITY_CREDENTIALS, "jeus");
            ht.put(Context.SECURITY_PRINCIPAL, "jeus");
            InitialContext ctx = new InitialContext(ht);
            PropagationTesterHome1 home = (PropagationTesterHome1) ctx.lookup("ejb1");
//            PropagationTesterHome1 home = (PropagationTesterHome1) ctx.lookup("java:global/Bean1/ejb1!ejb1.PropagationTesterHome1");
            PropagationTester1 obj = home.create();
            HashMap<String, Object> hashMap = new HashMap();
            hashMap.put(PropagationConstants.EJB_USING_TRANSACTION, "NoTx");
            hashMap.put(PropagationConstants.EJB_CALLEE_NAME, "ejb1");
            hashMap.put(PropagationConstants.EJB_NUMBER_OF_CONNECTIONS, 1);
            float time = 0;
            hashMap.put(PropagationConstants.EJB_SLEEP_TIME, time);
            hashMap.put(PropagationConstants.EJB_CONNECTION_LEAK, false);
            hashMap.put(PropagationConstants.EJB_LOG_COLOR_EMPHASIS, true);
            HashMap<String, Object> hashMap2 = new HashMap();
            obj.callEJB(hashMap, hashMap2);
            ctx.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
