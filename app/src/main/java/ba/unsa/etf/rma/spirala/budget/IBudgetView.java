package ba.unsa.etf.rma.spirala.budget;

import ba.unsa.etf.rma.spirala.data.Account;

public interface IBudgetView {
    void refreshFields(Account account);
    void showToast(String text);
}
