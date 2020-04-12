package ba.unsa.etf.rma.spirala.budget;

import android.content.Context;

import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.IAccountInteractor;

public class BudgetPresenter implements IBudgetPresenter {
    private IAccountInteractor accountInteractor;
    private Context context;
    private Account account;

    public BudgetPresenter(Context context) {
        accountInteractor = new AccountInteractor();
        this.context = context;
    }

    public BudgetPresenter(Context context, Account account) {
        accountInteractor = new AccountInteractor();
        this.context = context;
        this.account = account;
    }

    @Override
    public void setTotalLimit(double d) {
        accountInteractor.setTotalLimit(d);
        this.account = accountInteractor.getAccount();
    }

    @Override
    public void setMonthLimit(double d) {
        accountInteractor.setMonthLimit(d);
        this.account = accountInteractor.getAccount();
    }

    @Override
    public double getBudget() {
        return account.getBudget();
    }

    @Override
    public double getTotalLimit() {
        return account.getTotalLimit();
    }

    @Override
    public double getMonthyLimit() {
        return account.getMonthLimit();
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public void updateAccount(double totalLimit, double monthlyLimit) {
        accountInteractor.setTotalLimit(totalLimit);
        accountInteractor.setMonthLimit(monthlyLimit);
        this.account = accountInteractor.getAccount();
    }
}
