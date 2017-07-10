package com.prueba.santander.rsssantander.Beans;

import android.content.Context;

/**
 * Created by David on 09/07/2017.
 */

public class Noticia {

    public String title;
    public String description;
    public String imageUrl;
    public String link;
    public Context ctx;

    public Noticia(String title, String description, String link, String urlImagen, Context ctx) {
        this.title = title;
        this.description = description;
        this.imageUrl=urlImagen;
        this.ctx=ctx;
        this.link=link;
    }
}
