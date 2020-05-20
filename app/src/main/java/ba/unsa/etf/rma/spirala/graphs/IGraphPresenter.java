package ba.unsa.etf.rma.spirala.graphs;

import java.util.ArrayList;

import ba.unsa.etf.rma.spirala.data.Transaction;
import ba.unsa.etf.rma.spirala.util.Lambda;

public interface IGraphPresenter {
    void getDailyEntries(Lambda expenses, Lambda income, Lambda budget);
    void getMonthlyEntries(Lambda expenses, Lambda income, Lambda budget);
    void getWeeklyEntries(Lambda expenses, Lambda income, Lambda budget);

    void getDailyExpensesEntries(ArrayList<Transaction> transactions, Lambda lambda);
    void getMonthlyExpensesEntries(ArrayList<Transaction> transactions, Lambda lambda);
    void getWeeklyExpensesEntries(ArrayList<Transaction> transactions, Lambda lambda);

    void getDailyIncomeEntries(ArrayList<Transaction> transactions, Lambda lambda);
    void getMonthlyIncomeEntries(ArrayList<Transaction> transactions, Lambda lambda);
    void getWeeklyIncomeEntries(ArrayList<Transaction> transactions, Lambda lambda);

    void getDailyBudgetEntries(ArrayList<Transaction> transactions, Lambda lambda);
    void getMonthlyBudgetEntries(ArrayList<Transaction> transactions, Lambda lambda);
    void getWeeklyBudgetEntries(ArrayList<Transaction> transactions, Lambda lambda);
}
