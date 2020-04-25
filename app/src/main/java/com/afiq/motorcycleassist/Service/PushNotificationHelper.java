package com.afiq.motorcycleassist.Service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotificationHelper {

    public void sendGoodSamaritansNotification(String token, String title, String body, String data){
        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("sound","notification_sound.mp3");
            notification.put("body", body);
            JSONObject jsonData = new JSONObject();
            jsonData.put("gsRequestKey",data);
            message.put("notification", notification);
            message.put("data",jsonData);

            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendGoodSamaritansNotification(String token, String title, String body){
        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body", body);
            message.put("notification", notification);

            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendNotificationWithData(String token, String title, String body, String data){
        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("sound","notification_sound.mp3");
            notification.put("body", body);
            JSONObject jsonData = new JSONObject();
            jsonData.put("gsRequestKey",data);
            message.put("notification", notification);
            message.put("data",jsonData);

            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void sendNotification(String token, String title, String body){
        try {

            final String apiKey = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body", body);
            notification.put("sound","noti.wav");
            message.put("notification", notification);

            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
