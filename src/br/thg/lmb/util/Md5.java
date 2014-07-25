package br.thg.lmb.util;



public class Md5 {
	
	public static String md5(String senha){  

		try {   
	         java.security.MessageDigest md =   
	            java.security.MessageDigest.getInstance("MD5");   
	         md.update(senha.getBytes());   
	         byte[] hash = md.digest();   
	         StringBuffer hexString = new StringBuffer();   
	         for (int i = 0; i < hash.length; i++) {   
	            if ((0xff & hash[i]) < 0x10)   
	               hexString.append(   
	                  "0" + Integer.toHexString((0xFF & hash[i])));   
	            else   
	               hexString.append(Integer.toHexString(0xFF & hash[i]));   
	         }   
	         return hexString.toString();   
	      }   
	      catch (Exception nsae) {   
	         nsae.printStackTrace();   
	      }
	      
	      return null;
	   }

}
