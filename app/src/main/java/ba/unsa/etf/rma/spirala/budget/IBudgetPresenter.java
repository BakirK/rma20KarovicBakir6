package ba.unsa.etf.rma.spirala.budget;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ba.unsa.etf.rma.spirala.data.Account;

public interface IBudgetPresenter {
    void setTotalLimit(double d);
    void setMonthLimit(double d);
    double getBudget();
    double getTotalLimit();
    double getMonthyLimit();
    void setAccount(Account account);
    void updateAccount(double totalLimit, double monthlyLimit);
    List<Entry> getDailyExpensesEntries();
    List<Entry> getMonthlyExpensesEntries();
    List<Entry> getWeeklyExpensesEntries();
}
