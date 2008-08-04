package de.hpi.bpel4chor.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * This class provides a helper method for generating a unique id.
 */
public class IDGenerator {
	
	/**
	 * Generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 * 
	 * @return The generated id. The id will be an empty string if an
	 * exception occured during the id generation. 
	 */
	public static String generateID(Output output) {
		String strRetVal = "";
        String strTemp = "";
		
        // Get CurrentTimeMillis() segment
        strTemp = Long.toHexString(System.currentTimeMillis());
        while (strTemp.length () < 12)
        {
            strTemp = '0' + strTemp;
        }
        strRetVal += strTemp + ':';

        //Get Random Segment       
        SecureRandom prng;
		try {
			prng = SecureRandom.getInstance("SHA1PRNG");
		
	        strTemp = Integer.toHexString(prng.nextInt());
	        while (strTemp.length () < 8)
	        {
	            strTemp = '0' + strTemp;
	        }
	        
	        strRetVal += strTemp.substring(4);
	        
	        return "generatedID_" + strRetVal;
		} catch (NoSuchAlgorithmException e) {
			output.addError(e);
		}
		return "";
	}
}
