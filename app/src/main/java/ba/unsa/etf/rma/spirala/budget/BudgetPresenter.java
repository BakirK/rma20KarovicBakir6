package ba.unsa.etf.rma.spirala.budget;

import android.content.Context;
import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.AccountPostInteractor;
import ba.unsa.etf.rma.spirala.util.ILambda;
import ba.unsa.etf.rma.spirala.util.Lambda;

public class BudgetPresenter implements IBudgetPresenter {
    private Context context;
    private Account account;
    private IBudgetView view;
    public BudgetPresenter(IBudgetView view, Context context) {
        this.context = context;
        this.view = view;
        refreshAccount();
    }

    @Override
    public void refreshAccount() {
        new AccountInteractor(
                new Lambda(new ILambda() {
                    @Override
                    public Object callback(Object o) {
                        account = (Account)o;
                        view.refreshFields(account);
                        return 0;
                    }
                }),
                context
        ).execute();
    }

    @Override
    public void updateLimits(double totalLimit, double monthlyLimit) {
        new AccountPostInteractor(
                new Lambda(new ILambda() {
                    @Override
                    public Object callback(Object o) {
                        account = (Account)o;
                        view.refreshFields(account);
                        return 0;
                    }
                }), context
        ).execute(
            Double.toString(account.getBudget()),
            Double.toString(totalLimit),
            Double.toString(monthlyLimit)
        );
    }

    @Override
    public void setBudget(double budget) {
        new AccountPostInteractor(
                new Lambda(new ILambda() {
                    @Override
                    public Object callback(Object o) {
                        account = (Account)o;
                        view.refreshFields(account);
                        return 0;
                    }
                }),
                context
        ).execute(
            Double.toString(budget),
            Double.toString(account.getTotalLimit()),
            Double.toString(account.getMonthLimit())
        );
    }

    @Override
    public void setTotalLimit(double totalLimit) {
            new AccountPostInteractor(
                    new Lambda(new ILambda() {
                        @Override
                        public Object callback(Object o) {
                            account = (Account)o;
                            view.refreshFields(account);
                            return 0;
                        }
                    }),
                    context
            ).execute(
                Double.toString(account.getBudget()),
                Double.toString(totalLimit),
                Double.toString(account.getMonthLimit())
        );
    }

    @Override
    public void setMonthLimit(double monthLimit) {
        new AccountPostInteractor(
                new Lambda(new ILambda() {
                    @Override
                    public Object callback(Object o) {
                        account = (Account)o;
                        view.refreshFields(account);
                        return 0;
                    }
                }),
                context
        ).execute(
            Double.toString(account.getBudget()),
            Double.toString(account.getTotalLimit()),
            Double.toString(monthLimit)
        );
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
    public double getMonthlyLimit() {
        return account.getMonthLimit();
    }
}
