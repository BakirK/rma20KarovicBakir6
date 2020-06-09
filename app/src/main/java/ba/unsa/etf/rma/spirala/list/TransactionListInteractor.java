package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionDatabaseInteractor;
import ba.unsa.etf.rma.spirala.data.TransactionUpdateInteractor;
import ba.unsa.etf.rma.spirala.util.Callback;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Requests;
import ba.unsa.etf.rma.spirala.util.Util;

public class TransactionListInteractor extends AsyncTask<String, Integer, Void> implements ITransactionListInteractor {
    private Context context;
    private Callback callback;
    private ArrayList<Transaction> transactions;

    public TransactionListInteractor(Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
        transactions = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(String... strings) {
        getAllPages();
        return null;
    }

    private void getAllPages() {
        publishProgress(0);
        int page = 0;
        transactions = new ArrayList<>();
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
                    Transaction t = new Transaction(results.getJSONObject(i));
                    transactions.add(t);
                }
            } catch (Exception e) {
               e.printStackTrace();
                break outer;
            }
        }
        TransactionDatabaseInteractor tdi = new TransactionDatabaseInteractor();
        ArrayList<Transaction> dbTransactions =  tdi.getTransactions(context);
        if(dbTransactions == null || dbTransactions.isEmpty()) {
            for (Transaction transaction: transactions) {
                try {
                    insertDatabaseTransaction(transaction);
                } catch(Exception e) {
                    //e.printStackTrace();
                }
            }
        }
        syncTransactions();
    }

    private void syncTransactions() {
        publishProgress(50);
        TransactionDatabaseInteractor tdi = new TransactionDatabaseInteractor();
        ArrayList<Transaction> dbTransactions =  tdi.getTransactions(context);
        if(dbTransactions == null || dbTransactions.isEmpty()) {
            return;
        }
        for (Transaction dbTransaction: dbTransactions) {
            //add transaction
            if(dbTransaction.getId() == 0) {
                dbTransactions.remove(dbTransaction);
                String url2 = context.getString(R.string.root)
                        + "/account/"
                        +  context.getString(R.string.api_id)
                        + "/transactions";
                String dateStr = Transaction.format.format(dbTransaction.getDate());
                String titleStr = dbTransaction.getTitle();
                String amountStr = dbTransaction.getAmount().toString();
                String endDateStr = null;
                if(dbTransaction.getEndDate() != null) {
                    endDateStr = Transaction.format.format(dbTransaction.getEndDate());
                }
                String itemDescriptionStr = dbTransaction.getItemDescription();
                String intervalStr = null;
                if(dbTransaction.getTransactionInterval() != null) {
                    intervalStr = dbTransaction.getTransactionInterval().toString();
                }
                String typeIdStr = Integer.toString(Transaction.getTypeId(dbTransaction.getType()));
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("date", dateStr);
                    jsonParam.put("title", titleStr);
                    jsonParam.put("amount", amountStr);
                    jsonParam.put("endDate", endDateStr);
                    jsonParam.put("itemDescription", itemDescriptionStr);
                    jsonParam.put("transactionInterval", intervalStr);
                    jsonParam.put("TransactionTypeId", typeIdStr);
                    String response = Requests.post(url2, jsonParam);
                    transactions.add(new Transaction(new JSONObject(response)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                loop: for (int i = 0; i < transactions.size(); i++) {
                    if (
                            transactions.get(i).getId() == dbTransaction.getId() && dbTransaction.getId() != -1
                    ) {
                        checkDiff(dbTransaction, transactions.get(i));
                        break loop;
                    }
                }
            }
        }
        rangovska: for(Transaction tr: transactions) {
            for(Transaction dbTransaction: dbTransactions) {
                if(tr.getId() == dbTransaction.getId()) {
                    continue rangovska;
                }
            }
            //transaction not found in db - delete it
            String transactionId = null;
            try {
                transactionId = URLEncoder.encode(Integer.toString(tr.getId()), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String deleteUrl = context.getString(R.string.root)
                    + "/account/"
                    +  context.getString(R.string.api_id)
                    + "/transactions/" + transactionId;
            try {
                Requests.delete(deleteUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        publishProgress(100);
    }

    private void checkDiff(Transaction dbTransaction, Transaction transaction) {
        boolean itemDesc = false;
        if(dbTransaction.getItemDescription() == null || transaction.getItemDescription() == null) {
            itemDesc = dbTransaction.getItemDescription() == transaction.getItemDescription();
        } else {
            itemDesc = dbTransaction.getItemDescription().equals(transaction.getItemDescription());
        }
        if(
            dbTransaction.getType() != transaction.getType() ||
            dbTransaction.getTransactionInterval() != transaction.getTransactionInterval() ||
            !Transaction.sameDay(dbTransaction.getDate(), transaction.getDate()) ||
            !Transaction.sameDay(dbTransaction.getEndDate(), transaction.getEndDate()) ||
            !dbTransaction.getAmount().equals(transaction.getAmount()) ||
            !itemDesc ||
            !dbTransaction.getTitle().equals(transaction.getTitle())
        ) {
            transaction.setAmount(dbTransaction.getAmount());
            transaction.setDate(dbTransaction.getDate());
            transaction.setEndDate(dbTransaction.getEndDate());
            transaction.setItemDescription(dbTransaction.getItemDescription());
            transaction.setTitle(dbTransaction.getTitle());
            transaction.setTransactionInterval(dbTransaction.getTransactionInterval());
            transaction.setType(dbTransaction.getType());

            String date = Transaction.format.format(dbTransaction.getDate());
            String title = dbTransaction.getTitle();
            String amount = dbTransaction.getAmount().toString();
            String endDate = null;
            if(dbTransaction.getEndDate() != null) {
                endDate = Transaction.format.format(dbTransaction.getEndDate());
            }

            String itemDescription = dbTransaction.getItemDescription();
            String interval = null;
            if(dbTransaction.getTransactionInterval() != null) {
                interval = dbTransaction.getTransactionInterval().toString();
            }
            String typeId = Integer.toString(Transaction.getTypeId(dbTransaction.getType()));
            String transactionId = Integer.toString(dbTransaction.getId());

            new TransactionUpdateInteractor(new Callback(new ICallback() {
                @Override
                public Object callback(Object o) {
                    return 0;
                }
            }), context).execute(date, title, amount, endDate, itemDescription, interval, typeId, transactionId);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(callback != null) {
            (this.callback).pass(transactions);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        for (Integer i : values) {
            if(i == 0) callback.pass("Getting transactions. Please wait");
            else if(i == 50) callback.pass("Syncing...");
            else callback.pass("Syncing complete.");
        }
        super.onProgressUpdate(values);
    }

    @Override
    public void insertDatabaseTransaction(Transaction transaction) {
        TransactionDatabaseInteractor transactionDatabaseInteractor = new TransactionDatabaseInteractor();
        transactionDatabaseInteractor.addTransaction(context, transaction);
    }
}
