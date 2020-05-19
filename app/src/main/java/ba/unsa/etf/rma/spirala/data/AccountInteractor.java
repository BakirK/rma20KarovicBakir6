package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.Util;

public class AccountInteractor extends AsyncTask<String, Integer, Void> implements IAccountInteractor {
    private String api_id;
    private OnAccountSearchDone caller;
    private Account account;
    private Context context;

    public AccountInteractor(OnAccountSearchDone p, Context context) {
        this.caller = p;
        this.context = context;
        api_id = context.getString(R.string.api_id);
    }

    @Override
    protected Void doInBackground(String... strings) {

        String url1 = context.getString(R.string.root) + "/account/" + api_id;
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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

    public interface OnAccountSearchDone{
        public void onDone(Account result);
    }
}
