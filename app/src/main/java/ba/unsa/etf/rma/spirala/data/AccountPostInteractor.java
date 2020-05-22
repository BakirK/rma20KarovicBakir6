package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.Callback;
import ba.unsa.etf.rma.spirala.util.Requests;

public class AccountPostInteractor extends AsyncTask<String, Integer, Void> {
    private String api_id;
    private Account account;
    private Callback callback;
    private Context context;

    public AccountPostInteractor(Callback callback, Context context) {
        this.context = context;
        this.callback = callback;
        //caller = p;
        api_id = context.getString(R.string.api_id);
    }

    @Override
    protected Void doInBackground(String... strings) {
        String url1 = context.getString(R.string.root) + "/account/" + api_id;
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("budget", strings[0]);
            jsonParam.put("totalLimit", strings[1]);
            jsonParam.put("monthLimit", strings[2]);

            String response = Requests.post(url1, jsonParam);
            JSONObject jo = new JSONObject(response);
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
        callback.pass(account);
    }
}
