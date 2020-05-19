package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

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
import ba.unsa.etf.rma.spirala.data.TransactionModel;
import ba.unsa.etf.rma.spirala.util.Lambda;
import ba.unsa.etf.rma.spirala.util.Util;

public class TransactionListInteractor extends AsyncTask<String, Integer, Void> implements ITransactionListInteractor {
    private Context context;
    private Lambda lambda;
    ArrayList<Transaction> transactions;

    TransactionListInteractor(Lambda lambda, Context context) {
        this.lambda = lambda;
        this.context = context;
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
                    + "/transacions?page=" + page;
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = Util.convertStreamToString(in);
                JSONObject jo = new JSONObject(result);
                JSONArray results = jo.getJSONArray("transactions");
                if(result.length() == 0) {
                    break outer;
                } else page++;
                for (int i = 0; i < result.length(); i++) {
                    transactions.add(extractTransaction(results.getJSONObject(i)));
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

    public static Transaction extractTransaction(JSONObject transaction) throws JSONException {
        int id = transaction.getInt("id");
        Date date = null;
        Date endDate = null;
        try {
            String dateStr = transaction.getString("date");
            String endDateStr = transaction.getString("endDate");
            if(endDateStr != null) {
                endDate = Transaction.format.parse(endDateStr.substring(0, 10));
            }
            if(dateStr != null) {
                date = Transaction.format.parse(dateStr.substring(0, 10));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Double amount = transaction.getDouble("amount");
        String title = transaction.getString("title");
        Transaction.Type type = Transaction.getTypeById(transaction.getInt("TransactionTypeId"));
        String itemDescription = transaction.getString("itemDescription");
        Integer transactionInterval = transaction.getInt("transactionInterval");
        return new Transaction(id, date, amount, title, type, itemDescription, transactionInterval, endDate);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        (this.lambda).pass(transactions);
    }

    @Override
    public ArrayList<Transaction> getTransactions() {
        return TransactionModel.transactions;
    }


    @Override
    public void remove(int i) {
        TransactionModel.transactions.remove(i);
    }

    @Override
    public void set(int i, Transaction updatedTransaction) {
        TransactionModel.transactions.set(i, updatedTransaction);
    }

    @Override
    public Transaction createTransaction(Date date,
                                         Double amount,
                                         String title,
                                         Transaction.Type type,
                                         @Nullable String itemDescription,
                                         @Nullable Integer transactionInterval,
                                         @Nullable Date endDate) {
        Transaction transaction = new Transaction(0, date, amount, title, type, itemDescription, transactionInterval, endDate);
        TransactionModel.transactions.add(transaction);
        return  transaction;
    }


}
