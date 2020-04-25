package com.afiq.motorcycleassist.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotificationHelper {

    public final static String AUTH_KEY_FCM = "AAAASPpxZTA:APA91bEz2NvXG4-xCJ2hjDX1cVIOmzqx3lhzE3pKKAujRxztRUsF9F0rY0TwZFSPUd-xGIdyQFq-XAzvZIiY2QPcmI1XRJ-LH7QieWVLY3RN8_uI7uFofzGRpLIN9-PmSvyHvZCKoC4C";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public PushNotificationHelper() throws IOException {
    }

    public static String sendPushNotification(String deviceToken)
            throws IOException {
        deviceToken = "\"d1jYkBCG0M8:APA91bGH2Mq-2VUMKBuRMG19Vcl6Nvb7UcaNTogXfsLHw_K93BHyNgV1DTy8ZJOumuszqZsbRvdlIUv9fR_asj0yxcV98ak_fR07WJMTdnn_OTM78bF4JcMuAA_Se1zSG5cc_0r3Y5TO\"";

        String result = "";
        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();
        JSONObject info = new JSONObject();

        try {
            json.put("to", deviceToken.trim());
            info.put("title", "notification title"); // Notification title
            info.put("body", "message body"); // Notification
            // body
            json.put("notification", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("GCM Notification is sent successfully");

        return result;

    }

    }