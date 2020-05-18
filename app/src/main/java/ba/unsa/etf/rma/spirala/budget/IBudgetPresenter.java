package ba.unsa.etf.rma.spirala.budget;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ba.unsa.etf.rma.spirala.data.Account;

public interface IBudgetPresenter {
    void updateLimits(double totalLimit, double monthlyLimit);
    void setBudget(double budget);
    void setTotalLimit(double totalLimit);
    void setMonthLimit(double monthLimit);
    double getBudget();
    double getTotalLimit();
    double getMonthlyLimit();
    List<Entry> getDailyExpensesEntries();
    List<Entry> getMonthlyExpensesEntries();
    List<Entry> getWeeklyExpensesEntries();

    List<Entry> getDailyIncomeEntries();
    List<Entry> getMonthlyIncomeEntries();
    List<Entry> getWeeklyIncomeEntries();

    List<Entry> getDailyBudgetEntries();
    List<Entry> getMonthlyBudgetEntries();
    List<Entry> getWeeklyBudgetEntries();
}
