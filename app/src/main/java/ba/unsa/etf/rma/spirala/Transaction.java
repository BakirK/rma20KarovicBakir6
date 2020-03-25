package ba.unsa.etf.rma.spirala;

import androidx.annotation.Nullable;

import java.util.Date;

public class Transaction {
    public enum Type {
        INDIVIDUALPAYMENT,
        REGULARPAYMENT,
        PURCHASE,
        INDIVIDUALINCOME,
        REGULARINCOME,
        ALL
    }

    private Date date;
    private Double amount;
    private String title;
    private String itemDescription; //null for INCOME transaction
    private Integer transactionInterval; //only for REGULARINCOME and REGULARPAYMENT
    private Date endDate; //for regular transactions
    private Type type;

    public Transaction(Date date, Double amount, String title, Type type, @Nullable String itemDescription,
                       @Nullable Integer transactionInterval, @Nullable Date endDate) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;

        //description is null for incoming transactions
        if(type == Type.REGULARINCOME || type == Type.INDIVIDUALINCOME) {
            this.itemDescription = null;
        } else {
            this.itemDescription = itemDescription;
        }

        if(type == Type.REGULARINCOME || type == Type.REGULARPAYMENT) {
            this.transactionInterval = transactionInterval;
            this.endDate = endDate;
        } else {
            this.transactionInterval = null;
            this.endDate = null;
        }
    }

    public Transaction() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(int transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }




}
