package ba.unsa.etf.rma.spirala;

public interface ITransactionDetailPresenter {
    void deleteTransaction(int id);
    void updateTransaction(Transaction transaction);
}
