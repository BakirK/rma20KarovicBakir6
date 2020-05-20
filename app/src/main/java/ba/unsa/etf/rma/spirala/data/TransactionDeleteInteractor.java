package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.Lambda;
import ba.unsa.etf.rma.spirala.util.Requests;

public class TransactionDeleteInteractor extends AsyncTask<String, Integer, Void> {
    private Context context;
    private Lambda lambda;
    JSONObject jo = null;

    public TransactionDeleteInteractor(Lambda lambda, Context context) {
        this.lambda = lambda;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String transactionId = null;
        try {
            transactionId = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = context.getString(R.string.root)
                + "/account/"
                +  context.getString(R.string.api_id)
                + "/transactions/" + transactionId;
        try {
            String response = Requests.delete(url1);
            jo = new JSONObject(response);
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
        lambda.pass(jo);
    }
}
