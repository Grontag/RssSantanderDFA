package com.prueba.santander.rsssantander.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prueba.santander.rsssantander.Beans.Noticia;

import java.util.ArrayList;

/**
 * Created by David on 09/07/2017.
 */

public class BBDD extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "rss.db";
    public String TABLE_NOTICIAS="noticias";
    public String TABLE_NOTICIAS_VISITADAS="noticiasvisitadas";
    public Context ctx;
    public SQLiteDatabase db;

    public BBDD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if(db==null){
            db=getWritableDatabase();
        }
        dropTables(db);
        poblarTablas(db);
    }

    private void poblarTablas(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NOTICIAS+" ("+
                "id INTEGER PRIMARY KEY, "+
                "titulo TEXT NOT NULL, "+
                "descripcion TEXT NOT NULL, "+
                "urlimagen TEXT NOT NULL, "+
                "link TEXT NOT NULL");

        db.execSQL("CREATE TABLE "+TABLE_NOTICIAS_VISITADAS+" ("+
                "id INTEGER PRIMARY KEY, "+
                "titulo TEXT NOT NULL, "+
                "descripcion TEXT NOT NULL, "+
                "urlimagen TEXT NOT NULL, "+
                "link TEXT NOT NULL");

    }



    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NOTICIAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertarNoticia(Noticia noticia){
        if(db==null){
            db=getWritableDatabase();
        }
        ContentValues values=new ContentValues();
        values.put("titulo", noticia.title);
        values.put("descripcion", noticia.description);
        values.put("urlimagen", noticia.imageUrl);
        values.put("link", noticia.link);
        db.insert(TABLE_NOTICIAS, null, values);
    }

    public void insertarNoticiaLeida(Noticia noticia){
        if(db==null){
            db=getWritableDatabase();
        }
        ContentValues values=new ContentValues();
        values.put("titulo", noticia.title);
        values.put("descripcion", noticia.description);
        values.put("urlimagen", noticia.imageUrl);
        values.put("link", noticia.link);
        db.insert(TABLE_NOTICIAS_VISITADAS, null, values);
    }

    public ArrayList<Noticia> recuperarNoticiasLeidas(){
        if(db==null){
            db=getReadableDatabase();
        }
        ArrayList<Noticia> noticias=new ArrayList<>();
        Cursor csr=db.rawQuery("SELECT * from "+TABLE_NOTICIAS_VISITADAS, null);
        if(csr.moveToFirst()){
            do{
                noticias.add(new Noticia(csr.getString(0),csr.getString(1), csr.getString(2), csr.getString(3), ctx));
            }while (csr.moveToNext());

            }

        csr.close();
        return noticias;
    }

    public ArrayList<Noticia> recuperarNoticias(){
        if(db==null){
            db=getReadableDatabase();
        }
        ArrayList<Noticia> noticias=new ArrayList<>();
        Cursor csr=db.rawQuery("SELECT * from "+TABLE_NOTICIAS, null);
        if(csr.moveToFirst()){
            do{
                noticias.add(new Noticia(csr.getString(0),csr.getString(1), csr.getString(2), csr.getString(3), ctx));
            }while (csr.moveToNext());

        }

        csr.close();
        return noticias;
    }
}
