package ba.unsa.etf.rma.spirala.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    private Integer id;
    private double budget;
    private double totalLimit;
    private double monthLimit;
    private String email;


    public Account(Integer id, double budget, double totalLimit, double monthLimit, String email) {
        this.id = id;
        this.budget = budget;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
        this.email = email;
    }

    protected Account(Parcel in) {
        id = in.readInt();
        budget = in.readDouble();
        totalLimit = in.readDouble();
        monthLimit = in.readDouble();
        email = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(double monthLimit) {
        this.monthLimit = monthLimit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(budget);
        dest.writeDouble(totalLimit);
        dest.writeDouble(monthLimit);
        dest.writeString(email);
    }
}

