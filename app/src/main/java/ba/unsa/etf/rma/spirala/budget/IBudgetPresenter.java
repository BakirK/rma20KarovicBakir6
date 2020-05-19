package ba.unsa.etf.rma.spirala.budget;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ba.unsa.etf.rma.spirala.data.Account;

public interface IBudgetPresenter {
    void refreshAccount();
    void updateLimits(double totalLimit, double monthlyLimit);
    void setBudget(double budget);
    void setTotalLimit(double totalLimit);
    void setMonthLimit(double monthLimit);
    double getBudget();
    double getTotalLimit();
    double getMonthlyLimit();
}
