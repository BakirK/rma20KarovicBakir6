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
import ba.unsa.etf.rma.spirala.util.Lambda;
import ba.unsa.etf.rma.spirala.util.Util;

public class AccountInteractor extends AsyncTask<String, Integer, Void> implements IAccountInteractor {
    private String api_id;
    //private OnAccountSearchDone caller;
    private Account account;
    private Lambda lambda;
    private Context context;

    public AccountInteractor(Lambda lambda, Context context) {
        //this.caller = p;
        this.context = context;
        this.lambda = lambda;
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
        //caller.onDone(account);
        lambda.pass(account);
    }

    public interface OnAccountSearchDone{
        public void onDone(Account result);
    }
}
