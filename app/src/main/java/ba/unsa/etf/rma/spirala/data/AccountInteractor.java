package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.Callback;
import ba.unsa.etf.rma.spirala.util.Util;

public class AccountInteractor extends AsyncTask<String, Integer, Void> implements IAccountInteractor {
    private String api_id;
    private Account account;
    private Callback callback;
    private Context context;

    public AccountInteractor(Callback callback, Context context) {
        this.context = context;
        this.callback = callback;
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
            account = new Account(id, budget, totalLimit, monthLimit);
            AccountDatabaseInteractor temp = new AccountDatabaseInteractor();
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
        callback.pass(account);
    }
}
