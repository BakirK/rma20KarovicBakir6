package ba.unsa.etf.rma.spirala.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.util.Lambda;
import ba.unsa.etf.rma.spirala.util.Util;

public class TransactionSortInteractor extends AsyncTask<String, Integer, Void> {
    private Context context;
    private Lambda lambda;
    ArrayList<Transaction> transactions;

    public TransactionSortInteractor(Lambda lambda, Context context) {
        this.lambda = lambda;
        this.context = context;
        transactions = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(String... strings) {
        getAllPages(strings);
        return null;
    }

    private void getAllPages(String... strings) {
        int page = 0;
        outer: while(true) {

            String url1 = context.getString(R.string.root) + "/account/" +  context.getString(R.string.api_id)
                    + "/transactions/filter?page=" + page;
            if(strings[0] != null && strings[0] != "0") url1 += "&typeId=" + strings[0];
            if(strings[1] != null) url1 += "&month=" + strings[1];
            if(strings[2] != null) url1 += "&year=" + strings[2];
            if(strings[3] != null && strings[4] != null) {
                url1 += "&sort=" + strings[3];
                url1 += "." + strings[4];
            }
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = Util.convertStreamToString(in);
                JSONObject jo = new JSONObject(result);
                JSONArray results = jo.getJSONArray("transactions");
                if(results.length() == 0) {
                    break outer;
                } else page++;
                for (int i = 0; i < results.length(); i++) {
                    transactions.add(new Transaction(results.getJSONObject(i)));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        (this.lambda).pass(transactions);
    }
}
