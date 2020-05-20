package ba.unsa.etf.rma.spirala.list;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.IAccountInteractor;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionAmount;
import ba.unsa.etf.rma.spirala.data.TransactionSortInteractor;
import ba.unsa.etf.rma.spirala.util.ILambda;
import ba.unsa.etf.rma.spirala.util.Lambda;

public class TransactionListPresenter implements ITransactionListPresenter {
    private ITransactionListView view;
    private Context context;
    private Account account;
    ArrayList<Transaction> transactions = null;

    public TransactionListPresenter(ITransactionListView view, Context context) {
        new TransactionListInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                transactions = (ArrayList<Transaction>) o;
                return 0;
            }
        }), context).execute();
        this.view = view;
        this.context = context;
        refreshAccount();
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
        String typeId = Integer.toString(Transaction.getTypeId(t));
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

        new TransactionSortInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                transactions = (ArrayList<Transaction>) o;
                view.setTransactions(transactions);
                view.notifyTransactionListDataSetChanged();
                return 0;
            }
        }), context).execute(typeId, month, year, el, order);
    }

    @Override
    public void refreshAccount() {
        new AccountInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                account = (Account)o;
                view.setTextViewText(account);
                return 0;
            }
        }), context).execute();
    }

    @Override
    public void getBudget(Lambda l) {
        if(account == null) {
            Log.e("acc null trLiPr gbu", "null");
        }
        new TransactionListInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                return l.pass(account.getBudget() - TransactionAmount.getTotalAmount((ArrayList<Transaction>) o));
            }
        }), context).execute();
    }

    @Override
    public void getTotalLimit(Lambda l) {
        if(account != null) {
            l.pass(account.getTotalLimit());
        }
        new AccountInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                return l.pass(((Account)o).getTotalLimit());
            }
        }), context).execute();
    }

    @Override
    public void getMonthLimit(Lambda l) {
        if(account != null) {
            l.pass(account.getMonthLimit());
        }
        new AccountInteractor(new Lambda(new ILambda() {
            @Override
            public Object callback(Object o) {
                return l.pass(((Account)o).getMonthLimit());
            }
        }), context).execute();
    }
}
