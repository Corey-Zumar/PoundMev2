package com.ceazy.poundme.Database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ceazy.poundme.Pound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PoundsDB {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_ADDRESS= "address";
	public static final String KEY_TIME = "time";
	public static final String KEY_HASHTAG = "hashtag";
	public static final String KEY_HASHPHRASE = "hashphrase";
	public static final String KEY_FUNCTION = "function";
	public static final String KEY_SEEN = "seen";
	private static final String DATABASE_NAME = "PoundsDB";
	private static final String DATABASE_TABLE = "poundsTable";
	private static final int DATABASE_VERSION = 1;
	private database dbHelper;
	private final Context context;
	private SQLiteDatabase poundsDatabase;

	private static class database extends SQLiteOpenHelper {

		public database(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_ADDRESS + " TEXT NOT NULL, "  +
					KEY_TIME + " TEXT NOT NULL, "	+
					KEY_HASHTAG + " TEXT NOT NULL, "  +
					KEY_HASHPHRASE + " TEXT NOT NULL, " +
					KEY_FUNCTION + " TEXT NOT NULL, " +
					KEY_SEEN + " TEXT NOT NULL);"

			);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}

	}
	
	public Context getContext() {
		return context;
	}

	public PoundsDB(Context context) {
		this.context = context;

	}

	public PoundsDB open() throws SQLException {
		dbHelper = new database(getContext());
		poundsDatabase = dbHelper.getWritableDatabase(); 
		return this; 

	}

	public void close() {
		dbHelper.close();
	}

	public long createEntry(Pound pound) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ADDRESS, pound.getAddress());
		cv.put(KEY_TIME, pound.getTime());
		cv.put(KEY_HASHTAG, pound.getHashTag());
		cv.put(KEY_HASHPHRASE, pound.getHashPhrase());
		cv.put(KEY_FUNCTION, pound.getFunction());
		cv.put(KEY_SEEN, (pound.getSeenStatus() ? 1 : 0));
		return poundsDatabase.insert(DATABASE_TABLE, null, cv);
	}
	
	public Map<String, List<Pound>> getCategorizedPounds() {
		String[] columns = new String[]{KEY_ADDRESS, KEY_TIME, KEY_HASHTAG, 
				KEY_HASHPHRASE, KEY_FUNCTION, KEY_SEEN};
		Cursor c = poundsDatabase.query(DATABASE_TABLE, columns, null, null, null, 
				null, null);
		Map<String, List<Pound>> poundsMap = new LinkedHashMap<String, List<Pound>>();
		if(c.moveToFirst()) {
			for(c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
				String address = c.getString(0);
				long date = c.getLong(1);
				String hashTag = c.getString(2);
				String hashPhrase = c.getString(3);
				String function = c.getString(4);
				Pound pound = new Pound(address, date, hashTag, hashPhrase, function);
				pound.setSeenStatus(c.getInt(5) == 1);
				if(!poundsMap.containsKey(function)) {
					List<Pound> poundsList = new ArrayList<Pound>();
					poundsMap.put(function, poundsList);
				}
				poundsMap.get(function).add(pound);
				if(!pound.getSeenStatus()) {
					
				}
			}
		}
		return poundsMap;
	}
	
	public void setSeen(long time) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_SEEN, 1);
		poundsDatabase.update(DATABASE_TABLE, cv, KEY_TIME + " IS " + time, null);
	}
	
	public List<Pound> getAllPounds() {
		String[] columns = new String[]{KEY_ADDRESS, KEY_TIME, KEY_HASHTAG, 
				KEY_HASHPHRASE, KEY_FUNCTION};
		Cursor c = poundsDatabase.query(DATABASE_TABLE, columns, null, null, null, 
				null, null);
		List<Pound> poundsList = new ArrayList<Pound>();
		if(c.moveToFirst()) {
		for(c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
			String address = c.getString(0);
			long date = c.getLong(1);
			String hashTag = c.getString(2);
			String hashPhrase = c.getString(3);
			String function = c.getString(4);
			Pound pound = new Pound(address, date, hashTag, hashPhrase, function);
			poundsList.add(pound);
		}
		}
		return poundsList;
	}

	public void deleteEntry(long time) {
		poundsDatabase.delete(DATABASE_TABLE, KEY_TIME + " IS "+time, null);
	}
	
	public void deleteAllEntries() {
		poundsDatabase.delete(DATABASE_TABLE, KEY_TIME + " IS NOT NULL", null);
	}
}