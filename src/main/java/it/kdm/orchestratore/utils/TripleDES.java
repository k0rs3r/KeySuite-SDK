package it.kdm.orchestratore.utils;

import it.kdm.doctoolkit.zookeeper.ApplicationProperties;
import keysuite.cache.ClientCacheAuthUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;

public class TripleDES {
	
	public static void main(String[] args) throws Exception {
		String text = "textToEncrypt";
		String codedtext = TripleDES.encrypt(text);
		String decodedtext = TripleDES.decrypt(codedtext);
		System.out.println(text + " Encrypt---> " + codedtext);
		System.out.println(codedtext + " Decrypt---> " + decodedtext);
	}

    public static String encrypt(String message) throws Exception{
        String secretKey = getSecret();
        return encrypt(message,secretKey);
    }

	public static String encrypt(String message, String secretKey) throws Exception {


		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] plainTextBytes = message.getBytes("utf-8");
		byte[] buf = cipher.doFinal(plainTextBytes);
		byte[] base64Bytes = Base64.encodeBase64(buf);
		String base64EncryptedString = new String(base64Bytes);
		return base64EncryptedString;
	}

    private static String getSecret() throws Exception {
//        Properties properties = new Properties();
//        File myloc = new File(Utils.getConfigHome(), "system.properties");
//        try (InputStream inputStream = new FileInputStream(myloc)) {
//            properties.load(inputStream);
//        }
//        return properties.getProperty("secretKey");
		return ApplicationProperties.getInstance("system.properties").getPropertyByKey("secretKey", "");
    }

    public static String decrypt(String encryptedText) throws Exception {
        String secretKey = getSecret();
        return decrypt(encryptedText,secretKey);
    }

    public static String decrypt(String encryptedText, String secretKey) throws Exception {

        byte[] message = Base64.decodeBase64(encryptedText.getBytes("utf-8"));
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		Cipher decipher = Cipher.getInstance("DESede");
		decipher.init(Cipher.DECRYPT_MODE, key);
		byte[] plainText = decipher.doFinal(message);
		return new String(plainText, "UTF-8");
	}

	public static String getSecureTag(String username, String aoo, String ipAddress) throws UnsupportedEncodingException {
		Calendar c = Calendar.getInstance();
		Integer minuteOfValidityToken = new Integer(ApplicationProperties.get("token.validity", "30"));
		c.add(Calendar.MINUTE, minuteOfValidityToken);
		String timeInMillis = ""+c.getTimeInMillis();

		if (isLocal(ipAddress))
			ipAddress = "localhost";

		java.util.Base64.Encoder urlEncoder = java.util.Base64.getUrlEncoder().withoutPadding();
		String base64UsernameAoo = urlEncoder.encodeToString((username+"##"+aoo+"##"+timeInMillis+"##"+ipAddress).getBytes("UTF-8"));
		String linkToEncoded = username + "##" + aoo + "##" + timeInMillis + "##" + ipAddress;
		String md5EncodedUrl = DigestUtils.md5DigestAsHex(linkToEncoded.toLowerCase().getBytes());
		return base64UsernameAoo + "=" + md5EncodedUrl;
	}

	private static boolean isLocal(String ipAddress){
		try {
			InetAddress inet = InetAddress.getByName(ipAddress);
			if (inet.isLoopbackAddress() || inet.isLoopbackAddress() || inet.isSiteLocalAddress());
				return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}

	public static String getTokenFromTaggedLink(String link, String ipAddress) throws UnsupportedEncodingException {

		if (isLocal(ipAddress))
			ipAddress = "localhost";

		String result = null;
		if(Strings.isNotEmpty(link) && link.contains(";")){
			String[] parts = link.split(";");
			if (parts.length!=2)
				return null;
			String val = link.split(";")[1];
			parts = val.split("=");
			if (parts.length!=2)
				return null;
			String user_aoo = parts[0];
			String md5check = parts[1];

			java.util.Base64.Decoder urlDecoder = java.util.Base64.getUrlDecoder();
			byte[] decoded = urlDecoder.decode(user_aoo);
			user_aoo = new String(decoded, "UTF-8");
			parts = user_aoo.split("##");
			if (parts.length!=4)
				return null;

			String user = user_aoo.split("##")[0];
			String aoo =  user_aoo.split("##")[1];
			String timeInMillis =  user_aoo.split("##")[2];
			String requestIpAddress =  user_aoo.split("##")[3];

			String tag = user+"##"+aoo+"##"+timeInMillis+"##"+ipAddress;
			String md5Url = DigestUtils.md5DigestAsHex(tag.toLowerCase().getBytes());

			if(md5Url.equalsIgnoreCase(md5check)){
			    Calendar c = Calendar.getInstance();
			    if(c.getTimeInMillis()<Long.parseLong(timeInMillis) && requestIpAddress.equalsIgnoreCase(ipAddress)){
                    result= ClientCacheAuthUtils.getInstance().simpleJWTToken(aoo, user);
                }
			}
		}
		return result;
	}
}