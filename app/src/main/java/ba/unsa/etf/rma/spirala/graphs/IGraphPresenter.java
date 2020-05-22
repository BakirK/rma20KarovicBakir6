package ba.unsa.etf.rma.spirala.graphs;

import java.util.ArrayList;

import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.Callback;

public interface IGraphPresenter {
    void getDailyEntries(Callback expenses, Callback income, Callback budget);
    void getMonthlyEntries(Callback expenses, Callback income, Callback budget);
    void getWeeklyEntries(Callback expenses, Callback income, Callback budget);

    void getDailyExpensesEntries(ArrayList<Transaction> transactions, Callback callback);
    void getMonthlyExpensesEntries(ArrayList<Transaction> transactions, Callback callback);
    void getWeeklyExpensesEntries(ArrayList<Transaction> transactions, Callback callback);

    void getDailyIncomeEntries(ArrayList<Transaction> transactions, Callback callback);
    void getMonthlyIncomeEntries(ArrayList<Transaction> transactions, Callback callback);
    void getWeeklyIncomeEntries(ArrayList<Transaction> transactions, Callback callback);

    void getDailyBudgetEntries(ArrayList<Transaction> transactions, Callback callback);
    void getMonthlyBudgetEntries(ArrayList<Transaction> transactions, Callback callback);
    void getWeeklyBudgetEntries(ArrayList<Transaction> transactions, Callback callback);
}
