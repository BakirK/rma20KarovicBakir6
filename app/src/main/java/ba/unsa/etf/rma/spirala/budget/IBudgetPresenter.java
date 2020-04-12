package ba.unsa.etf.rma.spirala.budget;

import ba.unsa.etf.rma.spirala.data.Account;

public interface IBudgetPresenter {
    void setTotalLimit(double d);
    void setMonthLimit(double d);
    double getBudget();
    double getTotalLimit();
    double getMonthyLimit();
    void setAccount(Account account);
    void updateAccount(double totalLimit, double monthlyLimit);
}
