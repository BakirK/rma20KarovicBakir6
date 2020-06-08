package ba.unsa.etf.rma.spirala.budget;

import android.content.Context;
import ba.unsa.etf.rma.spirala.data.Account;
import ba.unsa.etf.rma.spirala.data.AccountDatabaseInteractor;
import ba.unsa.etf.rma.spirala.data.AccountInteractor;
import ba.unsa.etf.rma.spirala.data.AccountPostInteractor;
import ba.unsa.etf.rma.spirala.util.ICallback;
import ba.unsa.etf.rma.spirala.util.Callback;

public class BudgetPresenter implements IBudgetPresenter {
    private Context context;
    private Account account;
    private IBudgetView view;
    public BudgetPresenter(IBudgetView view, Context context) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void refreshAccount(boolean network) {
        if(network) {
            new AccountInteractor(
                    new Callback(new ICallback() {
                        @Override
                        public Object callback(Object o) {
                            account = (Account)o;
                            view.refreshFields(account);
                            return 0;
                        }
                    }),
                    context
            ).execute();
        } else {
            AccountDatabaseInteractor adi = new AccountDatabaseInteractor();
            this.account = adi.getAccount(context);
            view.refreshFields(this.account);
        }

    }

    @Override
    public void updateLimits(double totalLimit, double monthlyLimit, boolean network) {
        if(network) {
            new AccountPostInteractor(
                    new Callback(new ICallback() {
                        @Override
                        public Object callback(Object o) {
                            account = (Account)o;
                            view.refreshFields(account);
                            view.showToast("Changes saved");
                            return 0;
                        }
                    }), context
            ).execute(
                    Double.toString(account.getBudget()),
                    Double.toString(totalLimit),
                    Double.toString(monthlyLimit)
            );
        } else {
            AccountDatabaseInteractor adi = new AccountDatabaseInteractor();
            this.account.setTotalLimit(totalLimit);
            this.account.setMonthLimit(monthlyLimit);
            adi.updateAccount(context, this.account);
            this.account = adi.getAccount(context);
            view.refreshFields(this.account);
            view.showToast("Changes saved");
        }

    }


}
