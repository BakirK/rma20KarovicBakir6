package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountDatabaseInteractor;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionAmount;
import ba.unsa.etf.rma.spirala.data.TransactionDatabaseInteractor;
import ba.unsa.etf.rma.spirala.data.TransactionSortInteractor;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Callback;

public class TransactionListPresenter implements ITransactionListPresenter {
    private ITransactionListView view;
    private Context context;
    private Account account;
    ArrayList<Transaction> transactions = null;

    public TransactionListPresenter(ITransactionListView view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public Account getAccount() {
        if(account == null) {
            Log.e("account is null trLiPr", "null");
        }
        return account;
    }


    @Override
    public void refreshTransactions(Transaction.Type t, String orderBy, Date d) {
        Calendar c = Transaction.toCalendar(d.getTime());
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String year = String.valueOf(c.get(Calendar.YEAR));
        if(month.length() == 1) month = "0" + month;
        String el = null;
        String order = null;
        if(orderBy.startsWith("Price")) {
            el = "amount";
        } else if(orderBy.startsWith("Title")) {
            el = "title";
        } else {
            el = "date";
        }
        if(orderBy.endsWith("Ascending")) {
            order = "asc";
        } else {
            order="desc";
        }

        if(t != null) {
            String typeId = Integer.toString(Transaction.getTypeId(t));
            new TransactionSortInteractor(new Callback(new ICallback() {
                @Override
                public Object callback(Object o) {
                    transactions = (ArrayList<Transaction>) o;
                    view.setTransactions(transactions);
                    view.notifyTransactionListDataSetChanged();
                    return 0;
                }
            }), context).execute(typeId, month, year, el, order);
        } else {
            new TransactionSortInteractor(new Callback(new ICallback() {
                @Override
                public Object callback(Object o) {
                    transactions = (ArrayList<Transaction>) o;
                    view.setTransactions(transactions);
                    view.notifyTransactionListDataSetChanged();
                    return 0;
                }
            }), context).execute(null, month, year, el, order);
        }


    }

    @Override
    public void refreshCursorTransactions(Transaction.Type t, String orderBy, Date d) {
        view.setCursor(new TransactionDatabaseInteractor().getTransactionCursor(context, t, orderBy, d));
    }

    @Override
    public void refreshAccount(boolean network) {
        if(network) {
            new AccountInteractor(new Callback(new ICallback() {
                @Override
                public Object callback(Object o) {
                    account = (Account) o;
                    view.setTextViewText(account);
                    return 0;
                }
            }), context).execute();
        } else {
            AccountDatabaseInteractor accountDatabaseInteractor = new AccountDatabaseInteractor();
            account = accountDatabaseInteractor.getAccount(context);
            view.setTextViewText(account);
        }
    }

    @Override
    public void getBudget(Callback l) {
        if(account == null) {
            Log.e("acc null trLiPr gbu", "null");
        }
        new TransactionListInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                if(o instanceof ArrayList) {
                    return l.pass(account.getBudget() - TransactionAmount.getTotalAmount((ArrayList<Transaction>) o));
                }
                return 0;
            }
        }), context).execute();
    }

    @Override
    public void syncTransactions() {
        new TransactionListInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                if(o instanceof String) {
                    view.showToast((String)o);
                }
                return 0;
            }
        }), context).execute();
    }

}
