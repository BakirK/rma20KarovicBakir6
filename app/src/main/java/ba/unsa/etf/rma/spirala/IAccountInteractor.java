package ba.unsa.etf.rma.spirala;

public interface IAccountInteractor {
    Account getAccount();
    void setBudget(double d);
    void setTotalLimit(double d);
    void setMonthLimit(double d);
    double getBudget();
    double getTotalLimit();
    double getMonthLimit();
}
