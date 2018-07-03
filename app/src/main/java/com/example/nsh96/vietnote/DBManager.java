package com.example.nsh96.vietnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.ValueIterator;
import android.util.Log;

import java.util.ArrayList;

public class DBManager extends DatabaseHelper {
    Context context;

    public DBManager(Context context) {
        super(context);
    }
    public void Add_Note(Note note){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(VietNote.TITLE,note.getNtitlle());
        values.put(VietNote.PASS,note.getNpass());
        values.put(VietNote.LOCKED,note.getLocked());
        values.put(VietNote.DATE,note.getNdate());
        values.put(VietNote.TIME,note.getNtime());
        values.put(VietNote.CONTENT,note.getNcontent());
        values.put(VietNote.IMAGE,note.getNImgName());
        values.put(VietNote.IMPORTANT,note.getSaved());
        values.put(VietNote.RECORD,note.getRecord());
        db.insert(VietNote.TABLE_NOTE,null,values);
        db.close();
    }
    public void Add_PIN(Pin pin){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(VietNote.PINCODE,pin.getPincode());

        db.insert(VietNote.TABLE_PIN,null,values);
        db.close();
    }

    public void Add_Noti(Notification notification){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(VietNote._TIME,notification.getTime());
        values.put(VietNote._UNIT,notification.getDv());

        db.insert(VietNote.TABLE_TIME,null,values);
        db.close();
    }
    public boolean EditNoti(Notification noti){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_TIME+" SET "+VietNote._TIME+"="+noti.getTime()+", "+VietNote._UNIT+"="+noti.getDv()+";";
        db.execSQL(sql);
        db.close();
        return true;
    }
    public ArrayList<Notification> GetNotiTime(){
        SQLiteDatabase db= this.getWritableDatabase();
        String sql="SELECT * FROM "+VietNote.TABLE_TIME+";";
        Cursor cursor = db.rawQuery(sql,null);
        ArrayList<Notification> lst= new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Notification noti=new Notification(cursor.getInt(0),cursor.getInt(1));
                lst.add(noti);
            }while (cursor.moveToNext());
        }
        db.close();
        return lst;
    }

    public ArrayList<Pin> GetPINCODE(){
        SQLiteDatabase db= this.getWritableDatabase();
        String sql="SELECT * FROM "+VietNote.TABLE_PIN+";";
        Cursor cursor = db.rawQuery(sql,null);
        ArrayList<Pin> lst= new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Pin pin=new Pin(cursor.getInt(0));
                lst.add(pin);
            }while (cursor.moveToNext());
        }
        db.close();
        return lst;
    }


    public void UP_Note(Note note,int key){

        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(VietNote.TITLE,note.getNtitlle());
        values.put(VietNote.PASS,note.getNpass());
        values.put(VietNote.LOCKED,note.getLocked());
        values.put(VietNote.DATE,note.getNdate());
        values.put(VietNote.TIME,note.getNtime());
        values.put(VietNote.CONTENT,note.getNcontent());
        values.put(VietNote.IMAGE,note.getNImgName());
        values.put(VietNote.IMPORTANT,note.getSaved());
        values.put(VietNote.RECORD,note.getRecord());
        db.update(VietNote.TABLE_NOTE,values,""+VietNote.ID_NOTE+"="+key,null);
        db.close();
    }

    public boolean DeleteRecord(int key){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_NOTE+" SET "+VietNote.RECORD+"='' WHERE "+VietNote.ID_NOTE+"="+key+"";
        db.execSQL(sql);
        db.close();
        return true;
    }
    public boolean DeleteImage(int key){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_NOTE+" SET "+VietNote.IMAGE+"='' WHERE "+VietNote.ID_NOTE+"="+key+"";
        db.execSQL(sql);
        db.close();
        return true;
    }
    public boolean DeleteOneRecord(int key,String path){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_NOTE+" SET "+VietNote.RECORD+"='"+path+"' WHERE "+VietNote.ID_NOTE+"="+key+"";
        db.execSQL(sql);
        db.close();
        return true;
    }
    public boolean DeleteOneImg(int key,String path){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_NOTE+" SET "+VietNote.IMAGE+"='"+path+"' WHERE "+VietNote.ID_NOTE+"="+key+"";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public boolean PinUNLOCK(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_NOTE+" SET "+VietNote.PASS+"='', locked=0;";
        db.execSQL(sql);
        db.close();
        return true;
    }
    public ArrayList<Note> GetAll(){
        SQLiteDatabase db= this.getWritableDatabase();
        String sql="SELECT * FROM "+VietNote.TABLE_NOTE+";";
        Cursor cursor = db.rawQuery(sql,null);
        ArrayList<Note> lst= new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                    Note note=new Note(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8),cursor.getString(9));
                    lst.add(note);
            }while (cursor.moveToNext());
        }
        db.close();
        return lst;
    }

    public Note GetLast(){
        SQLiteDatabase db= this.getWritableDatabase();
        String sql="SELECT * FROM "+VietNote.TABLE_NOTE+";";
        Cursor cursor = db.rawQuery(sql,null);
        Note note= new Note();
        if(cursor.moveToLast()){
            do{
                note=new Note(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8),cursor.getString(9));

            }while (cursor.moveToNext());
        }
        db.close();
        return note;
    }

    public ArrayList<Note> GetSticky(){
        SQLiteDatabase db= this.getWritableDatabase();
        String sql="SELECT * FROM "+VietNote.TABLE_NOTE+" WHERE "+VietNote.IMPORTANT+"=1;";
        Cursor cursor = db.rawQuery(sql,null);
        ArrayList<Note> lst= new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Note note=new Note(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getInt(8),cursor.getString(9));
                lst.add(note);
            }while (cursor.moveToNext());
        }
        db.close();
        return lst;
    }


    public boolean deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="DELETE FROM "+VietNote.TABLE_NOTE+" WHERE "+VietNote.ID_NOTE+"="+id+";";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public boolean EditSORT(SortNote sortNote){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="UPDATE "+VietNote.TABLE_SORT+" SET "+VietNote.UPDOWN+"="+sortNote.getUpdown()+", "+VietNote.STYLE_SORT+"="+sortNote.getStyleSort()+";";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public void Add_SORT(SortNote sortNote){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(VietNote.UPDOWN,sortNote.getUpdown());
        values.put(VietNote.STYLE_SORT,sortNote.getStyleSort());

        db.insert(VietNote.TABLE_SORT,null,values);
        db.close();
    }
    public ArrayList<SortNote> GetSortNote(){
        SQLiteDatabase db= this.getWritableDatabase();
        String sql="SELECT * FROM "+VietNote.TABLE_SORT+";";
        Cursor cursor = db.rawQuery(sql,null);
        ArrayList<SortNote> lst= new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                SortNote sortNote=new SortNote(cursor.getInt(0),cursor.getInt(1));
                lst.add(sortNote);
            }while (cursor.moveToNext());
        }
        db.close();
        return lst;
    }
}
