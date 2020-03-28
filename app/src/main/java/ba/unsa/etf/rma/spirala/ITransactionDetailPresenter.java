package ba.unsa.etf.rma.spirala;

import androidx.annotation.Nullable;

import java.util.Date;

public interface ITransactionDetailPresenter {
    void updateParameters(Date date, Double amount, String title, Transaction.Type type, @Nullable String itemDescription,
                                @Nullable Integer transactionInterval, @Nullable Date endDate);

    void deleteTransaction();
    void setTransaction(Transaction t);
    Transaction getTransaction();
    boolean checkMonthlyBudget();
    boolean checkTotalBudget();
}
