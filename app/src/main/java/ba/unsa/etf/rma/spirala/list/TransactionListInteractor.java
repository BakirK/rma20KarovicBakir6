package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.Lambda;
import ba.unsa.etf.rma.spirala.util.Util;

public class TransactionListInteractor extends AsyncTask<String, Integer, Void> implements ITransactionListInteractor {
    private Context context;
    private Lambda lambda;
    ArrayList<Transaction> transactions;

    public TransactionListInteractor(Lambda lambda, Context context) {
        this.lambda = lambda;
        this.context = context;
        transactions = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(String... strings) {
        getAllPages();
        return null;
    }

    private void getAllPages() {
        int page = 0;
        outer: while(true) {
            String url1 = context.getString(R.string.root) + "/account/" +  context.getString(R.string.api_id)
                    + "/transactions?page=" + page;
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
