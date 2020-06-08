package ba.unsa.etf.rma.spirala.data;

import android.content.Context;

public interface IAccountDatabaseInteractor {
    void addAccount(Context context, Account account);
    Account getAccount(Context context);
    void updateAccount(Context context, Account account);
    void deleteAccount(Context context);
}
