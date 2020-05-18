package ba.unsa.etf.rma.spirala.data;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.Util;

public class AccountPostInteractor extends AsyncTask<String, Integer, Void> {
    private String api_id = String.valueOf(R.string.api_id);
    private AccountInteractor.OnAccountSearchDone caller;
    private Account account;

    public AccountPostInteractor(AccountInteractor.OnAccountSearchDone p) {
        caller = p;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url1 = R.string.root + "/account/" + api_id;
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            String jsonInputString =
                    "{budget: "+ strings[0] + ", totalLimit: " + strings[1] + ", monthLimit: " + strings[2] +"}";
            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = Util.convertStreamToString(in);
            JSONObject jo = new JSONObject(result);
            Integer id = jo.getInt("id");
            Double budget = jo.getDouble("budget");
            Double totalLimit = jo.getDouble("totalLimit");
            Double monthLimit = jo.getDouble("monthLimit");
            String email = jo.getString("email");
            account = new Account(id, budget, totalLimit, monthLimit, email);
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
