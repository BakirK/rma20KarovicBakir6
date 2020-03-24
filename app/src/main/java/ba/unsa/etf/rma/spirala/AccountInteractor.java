package ba.unsa.etf.rma.spirala;

public class AccountInteractor implements IAccountInteractor {
    @Override
    public Account getAccount() {
        return AccountModel.account;
    }

    @Override
    public void setBudget(double d) {
        AccountModel.account.setBudget(d);
    }

    @Override
    public void setTotalLimit(double d) {
        AccountModel.account.setTotalLimit(d);
    }

    @Override
    public void setMonthLimit(double d) {
        AccountModel.account.setMonthLimit(d);
    }
}
