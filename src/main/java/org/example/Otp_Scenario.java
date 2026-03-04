package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;

public class Otp_Scenario  {

    public static String encrypt(String strToEncrypt, String secret, String type, String iv) {
        try {
            SecretKeySpec key_temp = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
            Cipher cipher;
            if (type.equals("CBC")) {
                IvParameterSpec parameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
                cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, key_temp, parameterSpec);
            } else if (type.equals("ECB")) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, key_temp);
            } else {
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, key_temp);
            }
            final String encryptedString =
                    Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
            return encryptedString;

        } catch (Exception e) {
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret, String type, String iv) {

        try {
            Cipher cipher;
            if (strToDecrypt.contains("\"")) {
                strToDecrypt = strToDecrypt.split("\"")[1];
            }
            SecretKeySpec key_temp = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
            if (type.equals("CBC")) {
                IvParameterSpec parameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
                cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, key_temp, parameterSpec);
            } else if (type.equals("ECB")) {
                cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.DECRYPT_MODE, key_temp);
            } else {
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, key_temp);
            }
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
        }
        return null;
    }

    public String calloperationlist(String mobileNo){

        String authorization = "U0lvWGYyTjRGV3pSMnRzMTozNlQ5VlE3el92dEJaSFJENVVRZ0JPd1RqdXMx";
        String connectionFrom = "Upgrade-ws";
        String source="lambda";
        String token = "202307230800011690099201053PfRlEFA73w8nosu9zEHNRTKVwQixC3x";
        String operation= "operationallist";
        String operationURL = "https://superapp.bajajfinserv.in/apis/operationallist";
//		      String operationURL = "https://sauat.bajajfinserv.in/apis/operationallist";
        String encryption = "2023CYcXxujEX1sL";
        String oauthkey = "JJUQ1FPBD1ALHLE9";
        String viewotpkey = "";

        String encryptedBody = "YSZIA4WhafP3gC039DUV6BaaEpUIPgCt/2/QWnNo/6N0vQz1h0RRFfTSBA5ZHUt/sFWQOdMz8/4iMgAtKix8100Fpkt7TLwtTnW66VnqoTQ=";

        System.out.println("encryped body is :" +encryptedBody);

        try{
            String strTemp = "", response = "";
            URL url = new URL(operationURL);

            HttpURLConnection getMessageIDconnection = (HttpURLConnection) url.openConnection();
            getMessageIDconnection.setRequestMethod("POST");
            getMessageIDconnection.setDoOutput(true);
            getMessageIDconnection.setRequestProperty("Content-Type", "application/json");
            getMessageIDconnection.setRequestProperty("Accept", "application/json");
            getMessageIDconnection.setRequestProperty("Authorization", authorization);
            getMessageIDconnection.setRequestProperty("Connection-from", connectionFrom);
            getMessageIDconnection.setRequestProperty("source", source);
            getMessageIDconnection.setRequestProperty("token", token);
            getMessageIDconnection.setRequestProperty("operation", operation);

            byte[] out = encryptedBody.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = getMessageIDconnection.getOutputStream();
            stream.write(out);
            BufferedReader bf = new BufferedReader(new InputStreamReader(getMessageIDconnection.getInputStream()));
            while (null != (strTemp = bf.readLine())) {
                response += strTemp;
            }
            String EncryptedResponse = response.toString();
            System.out.println(response.toString());
            System.out.print("responsebody :" +response);
            String dcrptedBody = decrypt(EncryptedResponse, encryption, "AES", "");
            System.out.print("dcrptedBody :" +dcrptedBody);
            getMessageIDconnection.disconnect();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(dcrptedBody);
                JsonNode operationList = rootNode.get("operationList");
                if (operationList != null && operationList.isArray())
                {

                    viewotpkey = operationList.get(0).get("hashcode").asText();
                    //oauthkey = operationList.get(1).get("hashcode").asText();
                    System.out.println("\nView otp key  : ."+viewotpkey);

                    System.out.println("Oauth key : "+oauthkey);
                    return callOauth(oauthkey,viewotpkey,mobileNo);

                }
                else
                {
                    System.out.println("OperationList is not an array or is null.");
                }
            }
            catch (Exception e) {
                System.out.println("Exception : ."+e.getMessage());
                e.printStackTrace(); }

        }catch (Exception e){
            System.out.println(e);
            System.out.println("Failed successfully");
        }
        return "";
    }

    public String callOauth(String oauthkey, String validateOtpKey, String mobileNo){

        String authorization = "U0lvWGYyTjRGV3pSMnRzMTozNlQ5VlE3el92dEJaSFJENVVRZ0JPd1RqdXMx";
        String connectionFrom = "rest";
        String source="lambda";
        String operationURL = "https://superapp.bajajfinserv.in/apis/oauth-token";
//		      String operationURL = "https://sauat.bajajfinserv.in/apis/operationallist";

        String body = "{\n"
                + "	\"grant_type\": \"password\",\n"
                + "	\"deviceId\": \"abc\",\n"
                + "	\"channelId\": \"313\",\n"
                + "	\"username\": \"lambda\",\n"
                + "	\"passwordType\": \"password\",\n"
                + "	\"password\": \"qw63bcjk\"\n"
                + "}";

        String encryptedBody = encrypt(body, oauthkey,"AES","");
        System.out.println("encryped body is :" +encryptedBody);

        try
        {
            String strTemp = "", response = "";
            URL url = new URL(operationURL);

            HttpURLConnection getMessageIDconnection = (HttpURLConnection) url.openConnection();
            getMessageIDconnection.setRequestMethod("POST");
            getMessageIDconnection.setDoOutput(true);
            getMessageIDconnection.setRequestProperty("Content-Type", "application/json");
            getMessageIDconnection.setRequestProperty("Accept", "application/json");
            getMessageIDconnection.setRequestProperty("Authorization", authorization);
            getMessageIDconnection.setRequestProperty("Connection-from", connectionFrom);
            getMessageIDconnection.setRequestProperty("source", source);

            byte[] out = encryptedBody.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = getMessageIDconnection.getOutputStream();
            stream.write(out);
            BufferedReader bf = new BufferedReader(new InputStreamReader(getMessageIDconnection.getInputStream()));
            while (null != (strTemp = bf.readLine())) {
                response += strTemp;
            }

            // Get response headers
            Map<String, List<String>> headerFields = getMessageIDconnection.getHeaderFields();

            // Convert headers to a string format
            StringBuilder headers = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                headers.append(entry.getKey()).append(": ").append(String.join(", ", entry.getValue())).append("\n");
            }

            String EncryptedResponse = response.toString();
            System.out.println("Encrypted Response is :" +response.toString());
            System.out.print("responsebody :" +response);
            System.out.print("response headers :" +headers.toString());

            getMessageIDconnection.disconnect();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response);
                String token = rootNode.get("access_token").toString();
                System.out.println("\nToken is   : ."+token);

                return getOtp(token,validateOtpKey,mobileNo);

            }
            catch (Exception e) {
                System.out.println("Exception : ."+e.getMessage());
                e.printStackTrace(); }

        }catch (Exception e){
            System.out.println(e);
            System.out.println("Failed successfully");
        }
        return "";
    }

    public String getOtp(String token, String validateOtpKey, String mobileNo) {
        String authorization = "U0lvWGYyTjRGV3pSMnRzMTozNlQ5VlE3el92dEJaSFJENVVRZ0JPd1RqdXMx";
        String connectionFrom = "rest";
        String source = "lambda";
        String operationURL = "https://superapp.bajajfinserv.in/apis/viewotp";

        String body = "{\n" +
                "    \"username\": \"" + mobileNo + "\"\n" +
                "}";

        String encryptedBody = encrypt(body, validateOtpKey, "AES", "");
        System.out.println("encryped body is :" + encryptedBody);

        try {
            String strTemp = "", response = "";
            URL url = new URL(operationURL);

            HttpURLConnection getMessageIDconnection = (HttpURLConnection) url.openConnection();
            getMessageIDconnection.setRequestMethod("POST");
            getMessageIDconnection.setDoOutput(true);
            getMessageIDconnection.setRequestProperty("Content-Type", "application/json");
            getMessageIDconnection.setRequestProperty("Accept", "application/json");
            getMessageIDconnection.setRequestProperty("Authorization", authorization);
            getMessageIDconnection.setRequestProperty("Connection-from", connectionFrom);
            getMessageIDconnection.setRequestProperty("source", source);
            getMessageIDconnection.setRequestProperty("token", token);

            byte[] out = encryptedBody.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = getMessageIDconnection.getOutputStream();
            stream.write(out);

            BufferedReader bf = new BufferedReader(new InputStreamReader(getMessageIDconnection.getInputStream()));
            while (null != (strTemp = bf.readLine())) {
                response += strTemp;
            }

            String EncryptedResponse = response.toString();
            System.out.println(response.toString());
            System.out.print("\nresponsebody :" + response);
            String dcrptedBody = decrypt(EncryptedResponse, validateOtpKey, "AES", "");
            System.out.print("\ndecrptedBody for otp :" + dcrptedBody);
            getMessageIDconnection.disconnect();

            try {
                String jsonResponse = dcrptedBody;
                JSONArray jsonArray = new JSONArray(jsonResponse);

                // NEW LOGIC: Find the most recent unverified OTP
                String finalOtp = null;
                long latestSendTime = 0;

                System.out.println("\n=== Analyzing OTP Records ===");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String otp = jsonObject.getString("otp");
                    boolean otpVerifiedFlag = jsonObject.optBoolean("otpVerifiedFlag", false);
                    long sendTime = jsonObject.getJSONObject("phoneDelivery").getLong("sendTime");
                    int ttl = jsonObject.getInt("ttl");
                    String readableTime = formatTimestamp(sendTime);

                    System.out.println("Record " + (i+1) + ":");
                    System.out.println("  OTP: " + otp);
                    System.out.println("  Verified: " + otpVerifiedFlag);
                    System.out.println("  Send Time: " + sendTime + " (" + readableTime + ")");
                    System.out.println("  TTL: " + ttl + " seconds");
                    System.out.println("  Is Valid OTP Format: " + isValidOtpFormat(otp));

                    // Select the most recent unverified OTP with valid format and positive TTL
                    if (!otpVerifiedFlag && ttl > 0 && isValidOtpFormat(otp) && sendTime > latestSendTime) {
                        finalOtp = otp;
                        latestSendTime = sendTime;
                        System.out.println("  ✅ Selected as current best candidate");
                    } else {
                        System.out.println("  ❌ Skipped (verified=" + otpVerifiedFlag + ", ttl=" + ttl + ", validFormat=" + isValidOtpFormat(otp) + ")");
                    }
                }

                if (finalOtp != null) {
                    System.out.println("\n🎯 Final Selected OTP: " + finalOtp);
                    System.out.println("📅 Selected OTP Send Time: " + formatTimestamp(latestSendTime));
                    return finalOtp;
                } else {
                    System.out.println("\n⚠️ No valid unverified OTP found, using most recent:");
                    // Fallback logic with readable timestamp
                    long mostRecentTime = 0;
                    String fallbackOtp = null;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        long sendTime = jsonObject.getJSONObject("phoneDelivery").getLong("sendTime");
                        String otp = jsonObject.getString("otp");

                        if (sendTime > mostRecentTime && isValidOtpFormat(otp)) {
                            mostRecentTime = sendTime;
                            fallbackOtp = otp;
                        }
                    }

                    if (fallbackOtp != null) {
                        System.out.println("Fallback OTP: " + fallbackOtp);
                        System.out.println("📅 Fallback OTP Send Time: " + formatTimestamp(mostRecentTime));
                    }
                    return fallbackOtp != null ? fallbackOtp : "";
                }

            } catch (Exception e) {
                System.out.println("Exception parsing OTP response: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Exception in getOtp: " + e);
            System.out.println("Failed successfully");
        }
        return "";
    }

//	  public String getOtp(String token, String validateOtpKey,String mobileNo){
//
//		      String authorization = "U0lvWGYyTjRGV3pSMnRzMTozNlQ5VlE3el92dEJaSFJENVVRZ0JPd1RqdXMx";
//		      String connectionFrom = "rest";
//		      String source="lambda";
//		      String operationURL = "https://superapp.bajajfinserv.in/apis/viewotp";
////		      String operationURL = "https://sauat.bajajfinserv.in/apis/operationallist";
//
//		      String body =
//			          "{\n" +
//			                  "    \"username\": \"" + mobileNo + "\"\n" +
//			                  "}";
//
//
//		      String encryptedBody = encrypt(body, validateOtpKey,"AES","");
//		      System.out.println("encryped body is :" +encryptedBody);
//
//		      try{
//		    	String strTemp = "", response = "";
//		        URL url = new URL(operationURL);
//
//		        HttpURLConnection getMessageIDconnection = (HttpURLConnection) url.openConnection();
//		        getMessageIDconnection.setRequestMethod("POST");
//		        getMessageIDconnection.setDoOutput(true);
//		        getMessageIDconnection.setRequestProperty("Content-Type", "application/json");
//		        getMessageIDconnection.setRequestProperty("Accept", "application/json");
//		        getMessageIDconnection.setRequestProperty("Authorization", authorization);
//		        getMessageIDconnection.setRequestProperty("Connection-from", connectionFrom);
//		        getMessageIDconnection.setRequestProperty("source", source);
//		        getMessageIDconnection.setRequestProperty("token", token);
//
//		        byte[] out = encryptedBody.getBytes(StandardCharsets.UTF_8);
//		        OutputStream stream = getMessageIDconnection.getOutputStream();
//		        stream.write(out);
//		        //System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
//		        BufferedReader bf = new BufferedReader(new InputStreamReader(getMessageIDconnection.getInputStream()));
//		        while (null != (strTemp = bf.readLine())) {
//					response += strTemp;
//				}
//		        String EncryptedResponse = response.toString();
//		        System.out.println(response.toString());
//		        System.out.print("\nresponsebody :" +response);
//		        String dcrptedBody = decrypt(EncryptedResponse, validateOtpKey,"AES","");
//				System.out.print("\ndecrptedBody for otp :" +dcrptedBody);
//				getMessageIDconnection.disconnect();
//
//				try {
//					ObjectMapper objectMapper = new ObjectMapper();
//					JsonNode rootNode = objectMapper.readTree(response);
//					System.out.println("\nOTP is   : ."+rootNode.toString());
//
//					String jsonResponse = dcrptedBody;
//					JSONArray jsonArray = new JSONArray(jsonResponse);
//					JSONObject jsonObject = jsonArray.getJSONObject(0);
//
//					String FINALotp = jsonObject.getString("otp");
//					System.out.println("Extracted OTP: "+FINALotp);
//					return FINALotp;
//					}
//
//				catch (Exception e) {
//					System.out.println("Exception : ."+e.getMessage());
//					e.printStackTrace(); }
//
//		    }catch (Exception e){
//		        System.out.println(e);
//		        System.out.println("Failed successfully");
//		    }
//			return "";
//		}

    /**
     * Helper method to validate if OTP is in correct format (numeric, not encrypted)
     */
    private boolean isValidOtpFormat(String otp) {
        if (otp == null || otp.trim().isEmpty()) {
            return false;
        }

        // Check if it's a valid numeric OTP (typically 4-8 digits)
        return otp.matches("\\d{4,8}") && !otp.contains("=") && !otp.contains("+") && !otp.contains("/");
    }

    // Add this helper method to your OnboardingwithOTPScenarioPage class
    private String formatTimestamp(long timestamp) {
        try {
            // Convert milliseconds to Instant
            Instant instant = Instant.ofEpochMilli(timestamp);

            // Convert to LocalDateTime using system default timezone
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            // Format as readable string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return "Invalid timestamp: " + timestamp;
        }
    }



}
