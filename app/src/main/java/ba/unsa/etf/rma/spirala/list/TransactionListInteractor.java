package ba.unsa.etf.rma.spirala.list;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.transition.Transition;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.spirala.R;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.Callback;
import ba.unsa.etf.rma.spirala.util.TransactionDBOpenHelper;
import ba.unsa.etf.rma.spirala.util.Util;

public class TransactionListInteractor extends AsyncTask<String, Integer, Void> implements ITransactionListInteractor {
    private Context context;
    private Callback callback;
    ArrayList<Transaction> transactions;

    public TransactionListInteractor(Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
        transactions = new ArrayList<>();
    }

    public TransactionListInteractor() {
        transactions = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(context != null) {
            getAllPages();
        }
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
                    Transaction t = new Transaction(results.getJSONObject(i));
                    transactions.add(t);
                    //insertTransaction(t);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break outer;
            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        (this.callback).pass(transactions);
    }

    @Override
    public Cursor getTransactionCursor(Context context, Transaction.Type t, String orderBy, Date d) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL,
                TransactionDBOpenHelper.TRANSACTION_ENDDATE,
        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = "";
        ArrayList<String> whereArgs = new ArrayList<>();

        //type
        if(t != null && !t.toString().equals("ALL")) {
            where += "type = ? AND ";
            whereArgs.add(t.toString());
        }

        //date
        where += "date >= ? AND date <= ?";
        Calendar c = Transaction.toCalendar(d.getTime());
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String year = String.valueOf(c.get(Calendar.YEAR));
        if(month.length() == 1) month = "0" + month;
        String firstDayDate = year + "-" + month + "-01";
        whereArgs.add(firstDayDate);

        //last day in month
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDayDate = year + "-" + month + c.get(Calendar.DAY_OF_MONTH);
        whereArgs.add(lastDayDate);

        //order by clause
        String order = "";
        if(orderBy.startsWith("Price")) {
            order = "amount";
        } else if(orderBy.startsWith("Title")) {
            order = "title";
        } else {
            order = "date";
        }
        if(orderBy.endsWith("Ascending")) {
            order += " ASC";
        } else {
            order +=" DESC";
        }

        return cr.query(adresa, kolone, where, whereArgs.toArray(new String[whereArgs.size()]), order);
    }

    @Override
    public void insertTransaction(Transaction transaction) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri address = Uri.parse("content://rma.provider.transactions/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.TRANSACTION_ID, transaction.getId());
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(TransactionDBOpenHelper.TRANSACTION_DATE, Transaction.format.format(transaction.getDate()));
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE, transaction.getType().toString());
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE, transaction.getTitle());

        Integer transactionInterval = null;
        if(transaction.getTransactionInterval() != null) {
            transactionInterval = transaction.getTransactionInterval();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_TRANSACTIONINTERVAL, transactionInterval);

        String endDate = null;
        if(transaction.getEndDate() != null) {
            endDate = Transaction.format.format(transaction.getEndDate());
        }

        values.put(TransactionDBOpenHelper.TRANSACTION_ENDDATE, endDate);
        String itemDescription = null;
        if(transaction.getItemDescription() != null) {
            itemDescription = transaction.getItemDescription();
        }
        values.put(TransactionDBOpenHelper.TRANSACTION_ITEMDESCRIPTION, itemDescription);
        cr.insert(address, values);
    }
}
