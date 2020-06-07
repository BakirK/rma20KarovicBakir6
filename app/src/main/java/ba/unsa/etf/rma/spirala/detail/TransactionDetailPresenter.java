package ba.unsa.etf.rma.spirala.detail;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.ITransactionDatabaseInteractor;
import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.data.TransactionAmount;
import ba.unsa.etf.rma.spirala.data.TransactionDatabaseInteractor;
import ba.unsa.etf.rma.spirala.data.TransactionDeleteInteractor;
import ba.unsa.etf.rma.spirala.data.TransactionPostInteractor;
import ba.unsa.etf.rma.spirala.data.TransactionSortInteractor;
import ba.unsa.etf.rma.spirala.data.TransactionUpdateInteractor;
import ba.unsa.etf.rma.spirala.list.TransactionListInteractor;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Callback;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {
    private Context context;
    private Transaction transaction;
    private Account account;

    public TransactionDetailPresenter(Context context) {
        new AccountInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                account = (Account) o;
                return 0;
            }
        }), context).execute();
        this.context = context;
    }

    @Override
    public void deleteTransaction(boolean network) {
        if(network) {
            int id = transaction.getId();
            new TransactionDeleteInteractor(new Callback(new ICallback() {
                @Override
                public Object callback(Object o) {
                    JSONObject jo = (JSONObject) o;
                    try {
                        if(jo.getString("error") != null) {
                            Log.d("d", "tr not found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            }), context).execute(Integer.toString(id));
        } else {
            TransactionDatabaseInteractor transactionDatabaseInteractor = new TransactionDatabaseInteractor();
            transactionDatabaseInteractor.deleteTransaction(context, transaction.getInternalId());
        }

    }

    @Override
    public void setTransaction(Transaction t) {
        this.transaction = t;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void overMonthLimit(Callback callback, Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription, @Nullable Integer transactionInterval, @Nullable Date endDate) {
        Double thisAmount = 0.;
        if(this.transaction != null) {
            if(Transaction.isIncome(this.transaction.getType())) {
                thisAmount = -this.transaction.getAmount();
            } else {
                thisAmount = this.transaction.getAmount();
            }
            if(Transaction.isRegular(transaction.getType())) {
                int monthsBetween = Transaction.monthsBetween(transaction.getDate(), transaction.getEndDate());
                int daysBetween = Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
                int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
                thisAmount = thisAmount * averageNumberOfDaysPerMonth / transaction.getTransactionInterval();
            }
        }

        if(Transaction.isIncome(type)) {
            amount *= -1;
        }

        if(Transaction.isRegular(type)) {
            int monthsBetween = Transaction.monthsBetween(date, endDate);
            int daysBetween = Transaction.getDaysBetween(date, endDate);
            int averageNumberOfDaysPerMonth = daysBetween/(monthsBetween+1);
            amount = amount * averageNumberOfDaysPerMonth / transactionInterval;
        }

        Calendar c = Transaction.toCalendar(date.getTime());
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String year = String.valueOf(c.get(Calendar.YEAR));

        Double finalThisAmount = thisAmount;
        Double finalAmount = amount;
        new TransactionSortInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                Double monthExpenses = TransactionAmount.getMonthlyAmount((ArrayList<Transaction>) o, date);
                monthExpenses -= finalThisAmount;
                monthExpenses += finalAmount;
                Log.d("month total", Double.toString(monthExpenses));
                boolean over = account.getMonthLimit() < monthExpenses;
                callback.pass(over);
                return 0;
            }
        }), context).execute(null, month, year, null, null);
    }


    @Override
    public void overGlobalLimit(Callback callback, Date date, double amount, String title, Transaction.Type type, @Nullable String itemDescription, @Nullable Integer transactionInterval, @Nullable Date endDate) {
        Double thisAmount = 0.;
        if(this.transaction != null) {
            if(Transaction.isIncome(this.transaction.getType())) {
                thisAmount = -this.transaction.getAmount();
            } else {
                thisAmount = this.transaction.getAmount();
            }
            if(Transaction.isRegular(transaction.getType())) {
                thisAmount = thisAmount * Transaction.getDaysBetween(transaction.getDate(), transaction.getEndDate());
            }
        }
        if(Transaction.isIncome(type)) {
            amount *= -1;
        }

        if(Transaction.isRegular(type)) {
            amount = amount * Transaction.getDaysBetween(date, endDate);
        }


        Double finalThisAmount = thisAmount;
        Double finalAmount = amount;
        new TransactionListInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                Double totalExpenses = TransactionAmount.getTotalAmount((ArrayList<Transaction>) o);
                totalExpenses -= finalThisAmount;
                totalExpenses += finalAmount;
                boolean over = totalExpenses > account.getTotalLimit();
                callback.pass(over);
                return 0;
            }
        }), context).execute();
    }

    @Override
    public void updateParameters(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                                 @Nullable Integer transactionInterval, @Nullable Date endDate, boolean network) {
        if(this.transaction == null) {
            if(network) {
                String dateStr = Transaction.format.format(date);
                String titleStr = title;
                String amountStr = amount.toString();
                String endDateStr = null;
                if(endDate != null) {
                    endDateStr = Transaction.format.format(endDate);
                }
                String itemDescriptionStr = itemDescription;
                String intervalStr = null;
                if(transactionInterval != null) {
                    intervalStr = transactionInterval.toString();
                }
                String typeIdStr = Integer.toString(Transaction.getTypeId(type));
                new TransactionPostInteractor(new Callback(new ICallback() {
                    @Override
                    public Object callback(Object o) {
                        transaction = (Transaction)o;
                        return 0;
                    }
                }), context).execute(dateStr, titleStr, amountStr, endDateStr, itemDescriptionStr, intervalStr, typeIdStr);
            } else {
                transaction = new Transaction(-1, date, amount, title, type, itemDescription, transactionInterval, endDate);
                TransactionDatabaseInteractor transactionDatabaseInteractor = new TransactionDatabaseInteractor();
                transactionDatabaseInteractor.addTransaction(context, transaction);
            }

        } else {
            transaction.setTitle(title);
            transaction.setAmount(amount);
            transaction.setDate(date);
            transaction.setType(type);
            if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.INDIVIDUALINCOME) {
                transaction.setItemDescription(null);
            } else {
                transaction.setItemDescription(itemDescription);
            }

            if(type == Transaction.Type.REGULARINCOME || type == Transaction.Type.REGULARPAYMENT) {
                transaction.setTransactionInterval(transactionInterval);
                transaction.setEndDate(endDate);
            } else {
                transaction.setTransactionInterval(null);
                transaction.setEndDate(null);
            }

            if(network) {
                updateTransaction();
            } else {
                TransactionDatabaseInteractor transactionDatabaseInteractor = new TransactionDatabaseInteractor();
                transactionDatabaseInteractor.updateTransaction(context, transaction);
            }
        }
    }

    private void updateTransaction() {
        String date = Transaction.format.format(transaction.getDate());
        String title = transaction.getTitle();
        String amount = transaction.getAmount().toString();
        String endDate = null;
        if(transaction.getEndDate() != null) {
            endDate = Transaction.format.format(transaction.getEndDate());
        }

        String itemDescription = transaction.getItemDescription();
        String interval = null;
        if(transaction.getTransactionInterval() != null) {
            interval = transaction.getTransactionInterval().toString();
        }
        String typeId = Integer.toString(Transaction.getTypeId(transaction.getType()));
        String transactionId = Integer.toString(transaction.getId());

        new TransactionUpdateInteractor(new Callback(new ICallback() {
            @Override
            public Object callback(Object o) {
                transaction = (Transaction)o;
                return 0;
            }
        }), context).execute(date, title, amount, endDate, itemDescription, interval, typeId, transactionId);
    }

}
