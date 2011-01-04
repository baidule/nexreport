package test;

import jatools.accessor.ProtectPublic;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class Hello implements ProtectPublic{
    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String hello(String user) {
        return "您好," + user;
    }
    
    public static String myhello(String yy)
    {
    	return "aaaaaaaaaaaaaaaaaaa";
    }
}
