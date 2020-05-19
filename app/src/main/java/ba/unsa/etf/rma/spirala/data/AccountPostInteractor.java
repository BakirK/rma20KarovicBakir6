package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ba.unsa.etf.rma.spirala.R;

public class AccountPostInteractor extends AsyncTask<String, Integer, Void> {
    private String api_id;
    private AccountInteractor.OnAccountSearchDone caller;
    private Account account;
    private Context context;

    public AccountPostInteractor(AccountInteractor.OnAccountSearchDone p, Context context) {
        this.context = context;
        caller = p;
        api_id = context.getString(R.string.api_id);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url1 = context.getString(R.string.root) + "/account/" + api_id;
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("budget", strings[0]);
            jsonParam.put("totalLimit", strings[1]);
            jsonParam.put("monthLimit", strings[2]);
            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.writeBytes(jsonParam.toString());
            os.flush();
            os.close();

            int responseCode= urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String response = "";
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                JSONObject jo = new JSONObject(response);
                Integer id = jo.getInt("id");
                Double budget = jo.getDouble("budget");
                Double totalLimit = jo.getDouble("totalLimit");
                Double monthLimit = jo.getDouble("monthLimit");
                String email = jo.getString("email");
                account = new Account(id, budget, totalLimit, monthLimit, email);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        caller.onDone(account);
    }
}
