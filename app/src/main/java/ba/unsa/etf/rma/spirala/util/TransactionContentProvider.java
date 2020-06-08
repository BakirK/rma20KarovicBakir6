package ba.unsa.etf.rma.spirala.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionContentProvider extends ContentProvider {
    private static final int ALLROWS =1;
    private static final int ONEROW = 2;
    private static final UriMatcher uM;
    static {
        uM = new UriMatcher(UriMatcher.NO_MATCH);
        uM.addURI("rma.provider.transactions","elements",ALLROWS);
        uM.addURI("rma.provider.transactions","elements/#",ONEROW);
    }
    TransactionDBOpenHelper mHelper;
    @Override
    public boolean onCreate() {
        mHelper = new TransactionDBOpenHelper(getContext(),
                TransactionDBOpenHelper.DATABASE_NAME,null,
                TransactionDBOpenHelper.DATABASE_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database;
        try {
            database=mHelper.getWritableDatabase();
        } catch (SQLiteException e){
            database=mHelper.getReadableDatabase();
        }
        String groupby=null;
        String having=null;
        SQLiteQueryBuilder squery = new SQLiteQueryBuilder();

        switch (uM.match(uri)){
            case ONEROW:
                String idRow = uri.getPathSegments().get(1);
                squery.appendWhere(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID+"="+idRow);
            default:break;
        }
        squery.setTables(TransactionDBOpenHelper.TRANSACTION_TABLE);
        Cursor cursor = squery.query(database,projection,selection,selectionArgs,groupby,having,sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uM.match(uri)){
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.rma.elemental";
            case ONEROW:
                return "vnd.android.cursor.item/vnd.rma.elemental";
            default:
                throw new IllegalArgumentException("Unsuported uri: "+uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database;
        try{
            database=mHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=mHelper.getReadableDatabase();
        }
        long id = database.insert(TransactionDBOpenHelper.TRANSACTION_TABLE, null, values);
        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String idRow;
        switch (uM.match(uri)){
            case ONEROW:
                idRow = uri.getPathSegments().get(1);
                break;
            default: {
                throw new IllegalArgumentException("InternalId missing in: "+uri.toString());
            }
        }
        SQLiteDatabase database;
        try{
            database=mHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=mHelper.getReadableDatabase();
        }
        if (selection == null) selection = "";
        selection += TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID + "=?";
        ArrayList<String> args = new ArrayList<>();
        if(selectionArgs != null) {
            Collections.addAll(args, selectionArgs);
        }
        args.add(idRow);
        int deletedRows = database.delete(TransactionDBOpenHelper.TRANSACTION_TABLE, selection, args.toArray(new String[args.size()]));
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String idRow;
        switch (uM.match(uri)){
            case ONEROW:
                idRow = uri.getPathSegments().get(1);
                break;
            default: {
                throw new IllegalArgumentException("InternalId missing in: "+uri.toString());
            }
        }
        SQLiteDatabase database;
        try {
            database=mHelper.getWritableDatabase();
        } catch (SQLiteException e){
            database=mHelper.getReadableDatabase();
        }
        if (selection == null) selection = "";
        selection += TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID + "=?";
        ArrayList<String> args = new ArrayList<>();
        if(selectionArgs != null) {
            Collections.addAll(args, selectionArgs);
        }
        args.add(idRow);
        int updatedRows = database.update(TransactionDBOpenHelper.TRANSACTION_TABLE, values, selection, args.toArray(new String[args.size()]) );
        return updatedRows;
    }
}
