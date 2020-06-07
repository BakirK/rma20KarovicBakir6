package ba.unsa.etf.rma.spirala.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TransactionDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RMADataBase.db";
    public static final int DATABASE_VERSION = 2;

    public TransactionDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TransactionDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TRANSACTION_TABLE = "transactions";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_INTERNAL_ID = "_id";
    public static final String TRANSACTION_DATE = "date";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String TRANSACTION_ITEMDESCRIPTION = "itemDescription";
    public static final String TRANSACTION_TRANSACTIONINTERVAL = "transactionInterval";
    public static final String TRANSACTION_ENDDATE = "endDate";
    public static final String TRANSACTION_TYPE = "type";
    public static final String TRANSACTION_TITLE = "title";

    private static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE +
                    " ("  + TRANSACTION_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TRANSACTION_ID + " INTEGER UNIQUE, "
                    + TRANSACTION_DATE + " TEXT NOT NULL, "
                    + TRANSACTION_AMOUNT + " REAL NOT NULL, "
                    + TRANSACTION_ITEMDESCRIPTION + " TEXT, "
                    + TRANSACTION_TRANSACTIONINTERVAL + " TEXT, "
                    + TRANSACTION_ENDDATE + " TEXT, "
                    + TRANSACTION_TITLE + " TEXT NOT NULL, "
                    + TRANSACTION_TYPE + " TEXT NOT NULL);";

    private static final String TRANSACTION_TABLE_DROP = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;



    public static final String ACCOUNT_TABLE = "account";
    public static final String ACCOUNT_ID = "id";
    public static final String ACCOUNT_INTERNAL_ID = "_id";
    public static final String ACCOUNT_BUDGET = "budget";
    public static final String ACCOUNT_TOTAL_LIMIT = "totalLimit";
    public static final String ACCOUNT_MONTH_LIMIT = "monthLimit";

    private static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE +
                    " (" + ACCOUNT_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ACCOUNT_ID + " INTEGER UNIQUE, "
                    + ACCOUNT_BUDGET + " REAL NOT NULL, "
                    + ACCOUNT_TOTAL_LIMIT + " REAL NOT NULL, "
                    + ACCOUNT_MONTH_LIMIT + " REAL NOT NULL);";


    private static final String ACCOUNT_TABLE_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(ACCOUNT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_TABLE_DROP);
        db.execSQL(ACCOUNT_TABLE_DROP);
        onCreate(db);
    }
}
