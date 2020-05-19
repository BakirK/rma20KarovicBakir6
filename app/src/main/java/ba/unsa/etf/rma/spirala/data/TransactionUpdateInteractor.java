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
import ba.unsa.etf.rma.spirala.list.TransactionListInteractor;
import ba.unsa.etf.rma.spirala.util.Lambda;
import ba.unsa.etf.rma.spirala.util.Requests;

public class TransactionUpdateInteractor extends AsyncTask<String, Integer, Void> {
    private Context context;
    private Lambda lambda;
    Transaction transaction;


    TransactionUpdateInteractor(Lambda lambda, Context context) {
        this.lambda = lambda;
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String transactionId = null;
        try {
            transactionId = URLEncoder.encode(strings[7], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = context.getString(R.string.root)
                + "/account/"
                +  context.getString(R.string.api_id)
                + "/transactions/" + transactionId;
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("date", strings[0]);
            jsonParam.put("title", strings[1]);
            jsonParam.put("amount", strings[2]);
            jsonParam.put("endDate", strings[3]);
            jsonParam.put("itemDescription", strings[4]);
            jsonParam.put("transactionInterval", strings[5]);
            jsonParam.put("typeId", strings[6]);
            String response = Requests.post(url1, jsonParam);
            transaction = TransactionListInteractor.extractTransaction(new JSONObject(response));
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
        lambda.pass(transaction);
    }
}
