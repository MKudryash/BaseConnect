package com.example.baseconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnAdd, btnClear;
    EditText etName, etAuthor, etStyle;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etAuthor = (EditText) findViewById(R.id.TextAuthor);
        etStyle = (EditText) findViewById(R.id.TextStyle);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        UpdateTable();

    }
    public  void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int AutIndex = cursor.getColumnIndex(DBHelper.KEY_Auth);
            int StlIndex = cursor.getColumnIndex(DBHelper.STL);
            TableLayout dbOutPut = findViewById(R.id.dbOutPut);
            dbOutPut.removeAllViews();
            do {
                TableRow dbOuyPutRow = new TableRow( this);
                dbOuyPutRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TextView OutPutID = new TextView(this);
                params.weight = 1.0f;
                OutPutID.setLayoutParams(params);
                OutPutID.setText(cursor.getString(idIndex));
                dbOuyPutRow.addView(OutPutID);

                TextView OutPutName = new TextView(this);
                params.weight = 3.0f;
                OutPutName.setLayoutParams(params);
                OutPutName.setText(cursor.getString(nameIndex));
                dbOuyPutRow.addView(OutPutName);

                TextView OutPutAut = new TextView(this);
                params.weight = 3.0f;
                OutPutAut.setLayoutParams(params);
                OutPutAut.setText(cursor.getString(AutIndex));
                dbOuyPutRow.addView(OutPutAut);

                TextView OutPutStl = new TextView(this);
                params.weight = 2.0f;
                OutPutStl.setLayoutParams(params);
                OutPutStl.setText(cursor.getString(StlIndex));
                dbOuyPutRow.addView(OutPutStl);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить запись");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOuyPutRow.addView(deleteBtn);

                dbOutPut.addView(dbOuyPutRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAdd:
                String name = etName.getText().toString();
                String author = etAuthor.getText().toString();
                String style = etStyle.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_Auth, author);
                contentValues.put(DBHelper.STL, style);
                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                etName.setText(null);
                etAuthor.setText(null);
                etStyle.setText(null);
                UpdateTable();
                break;
            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutPut = findViewById(R.id.dbOutPut);
                dbOutPut.removeAllViews();
                etName.setText(null);
                etAuthor.setText(null);
                etStyle.setText(null);
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();
                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID+ " = ?", new String[]{String.valueOf(v.getId())});
                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int AutIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_Auth);
                    int StlIndex = cursorUpdater.getColumnIndex(DBHelper.STL);
                    int realID = 1;
                    do {
                        if (cursorUpdater.getInt(idIndex)>realID){
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_Auth, cursorUpdater.getString(AutIndex));
                            contentValues.put(DBHelper.STL, cursorUpdater.getString(StlIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realID++;
                    }while (cursorUpdater.moveToNext());
                    if (cursorUpdater.moveToLast()&& v.getId()!=realID){
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                break;
        }
    }
}

