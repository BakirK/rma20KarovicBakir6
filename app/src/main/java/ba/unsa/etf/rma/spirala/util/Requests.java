package ba.unsa.etf.rma.spirala.util;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Requests {
    public static String post(String url1, JSONObject jsonParam) throws IOException {
        URL url = new URL(url1);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
        os.writeBytes(jsonParam.toString());
        os.flush();
        os.close();
        String response = "";
        int responseCode= urlConnection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((line= br.readLine()) != null) {
                response += line;
            }
        }
        return response;
    }

    public static String delete(String url1) throws IOException {
        URL url = new URL(url1);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("DELETE");

        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        return Util.convertStreamToString(in);
    }

}
