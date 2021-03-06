package ba.unsa.etf.rma.spirala.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Transaction implements Parcelable {
    private int mData;

    protected Transaction(Parcel in) {
        mData = in.readInt();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
        title = in.readString();
        itemDescription = in.readString();
        if (in.readByte() == 0) {
            transactionInterval = null;
        } else {
            transactionInterval = in.readInt();
        }
        date = new Date(in.readLong());
        if (in.readByte() == 0) {
            endDate = null;
        } else {
            endDate = new Date(in.readLong());
        }
        type = Enum.valueOf(Transaction.Type.class, in.readString());
        id = in.readInt();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public static boolean sameMonth(Date dateNow, Date transactionDate) {
        Calendar calendar = toCalendar(dateNow.getTime());
        int yearNow = calendar.get(Calendar.YEAR), monthNow = calendar.get(Calendar.MONTH) + 1;
        calendar.setTime(transactionDate);
        int transactionYear = calendar.get(Calendar.YEAR), transactionMonth = calendar.get(Calendar.MONTH) + 1;
        return yearNow == transactionYear && monthNow == transactionMonth;
    }

    public static boolean sameDay(Date dateNow, Date transactionDate) {
        if(dateNow == null || transactionDate == null) {
            return dateNow == transactionDate;
        }
        Calendar now = toCalendar(dateNow.getTime());
        Calendar tDate = toCalendar(transactionDate.getTime());
        int yearNow = now.get(Calendar.YEAR),
                monthNow = now.get(Calendar.MONTH) + 1,
                dayNow = now.get(Calendar.DAY_OF_MONTH);
        int yearTransaction = tDate.get(Calendar.YEAR),
                monthTransaction = tDate.get(Calendar.MONTH) + 1,
                dayTransaction = tDate.get(Calendar.DAY_OF_MONTH);
        return yearNow == yearTransaction && monthNow == monthTransaction && dayNow == dayTransaction;
    }

    public static boolean dateOverlapping(Date d, Transaction transaction) {
        return ((transaction.getEndDate().after(d) || transaction.getEndDate().equals(d)) && (transaction.getDate().before(d) || transaction.getDate().equals(d)));

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mData);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeString(title);
        dest.writeString(itemDescription);
        if (transactionInterval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(transactionInterval);
        }
        dest.writeLong(date.getTime());
        if(endDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte)1);
            dest.writeLong(endDate.getTime());
        }
        dest.writeString(type.toString());
        dest.writeInt(id);
    }

    public enum Type {
        INDIVIDUALPAYMENT,
        REGULARPAYMENT,
        PURCHASE,
        INDIVIDUALINCOME,
        REGULARINCOME,
        ALL
    }

    private int id;
    private Date date;
    private Double amount;
    private String title;
    private String itemDescription; //null for INCOME transaction
    private Integer transactionInterval; //only for regularincome and regularpayment
    private Date endDate; //for regular transactions
    private Type type;
    private Integer internalId;
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public Transaction(int id, Date date, Double amount, String title, Type type, @Nullable String itemDescription,
                       @Nullable Integer transactionInterval, @Nullable Date endDate) {
        this.id = id;
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

    public Transaction(int id, Date date, Double amount, String title, Type type, @Nullable String itemDescription,
                       @Nullable Integer transactionInterval, @Nullable Date endDate, int internalId) {
        this.id = id;
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
        this.internalId = internalId;
    }

    public Transaction(JSONObject transaction) throws JSONException {
        this.id = transaction.getInt("id");
        Date date = null;
        Date endDate = null;
        try {
            String dateStr = transaction.getString("date");
            String endDateStr = transaction.getString("endDate");
            if(endDateStr != "null") {
                endDate = Transaction.format.parse(endDateStr.substring(0, 10));
            }
            if(dateStr != "null") {
                date = Transaction.format.parse(dateStr.substring(0, 10));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.amount = transaction.getDouble("amount");
        this.title = transaction.getString("title");
        this.type = Transaction.getTypeById(transaction.getInt("TransactionTypeId"));
        this.itemDescription = transaction.getString("itemDescription");
        if(this.itemDescription.equals("null")) this.itemDescription = null;
        Object interval = transaction.get("transactionInterval");
        if(interval != null && !interval.toString().equals("null")) {
            this.transactionInterval = Integer.parseInt(interval.toString());
        } else this.transactionInterval = null;
        this.date = date;
        this.endDate = endDate;
    }

    public static int getTypeId(Type t) {
        switch(t) {
            case REGULARPAYMENT: {
                return 1;
            }
            case REGULARINCOME: {
                return 2;
            }
            case PURCHASE: {
                return 3;
            }
            case INDIVIDUALINCOME: {
                return 4;
            }
            case INDIVIDUALPAYMENT: {
                return 5;
            }
        }
        return 0;
    }

    public static Type getTypeById(int id) {
        switch (id) {
            case 1: return Type.REGULARPAYMENT;
            case 2: return Type.REGULARINCOME;
            case 3: return Type.PURCHASE;
            case 4: return Type.INDIVIDUALINCOME;
            case 5: return Type.INDIVIDUALPAYMENT;
        }
        return Type.ALL;
    }

    public int getId() {
        return id;
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

    public Integer getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(Integer transactionInterval) {
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

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public static boolean isIncome(Transaction.Type type) {
        return type == Transaction.Type.INDIVIDUALINCOME || type == Type.REGULARINCOME;
    }
    public static boolean isRegular(Transaction.Type type) {
        return type == Transaction.Type.REGULARPAYMENT || type == Transaction.Type.REGULARINCOME;
    }
    public static boolean isIndividual(Transaction.Type t) {
        return t == Transaction.Type.INDIVIDUALPAYMENT || t == Transaction.Type.INDIVIDUALINCOME || t == Transaction.Type.PURCHASE;
    }

    public static int getDaysBetween(Date sDateIn, Date eDateIn) {
        Calendar sDate = toCalendar(sDateIn.getTime());
        Calendar eDate = toCalendar(eDateIn.getTime());

        // Get the represented date in milliseconds
        long milis1 = sDate.getTimeInMillis();
        long milis2 = eDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(milis2 - milis1);

        return (int)(diff / (24 * 60 * 60 * 1000));
    }

    public static Calendar toCalendar(long timestamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static int monthsBetween(Date a, Date b) {
        Calendar cal = Calendar.getInstance();
        if (a.before(b)) {
            cal.setTime(a);
        } else {
            cal.setTime(b);
            b = a;
        }
        int c = 0;
        while (cal.getTime().before(b)) {
            cal.add(Calendar.MONTH, 1);
            c++;
        }
        return c - 1;
    }

    public static boolean sameWeek(Date a, Date b) {
        Calendar first = toCalendar(a.getTime());
        Calendar second = toCalendar(b.getTime());
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) && first.get(Calendar.WEEK_OF_YEAR) == second.get(Calendar.WEEK_OF_YEAR);
    }

}
