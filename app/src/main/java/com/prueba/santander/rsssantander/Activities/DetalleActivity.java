package com.prueba.santander.rsssantander.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prueba.santander.rsssantander.R;
import com.squareup.picasso.Picasso;

/**
 * Created by David on 09/07/2017.
 */

public class DetalleActivity extends AppCompatActivity{

    private TextView titulo;
    private TextView descripcion;
    private ImageView imagen;
    private Button navButton;
    private String tit;
    private String desc;
    private String urlImagen;
    private String link;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_layout);
        titulo=(TextView)findViewById(R.id.titulo);
        descripcion=(TextView)findViewById(R.id.descripcion);
        imagen=(ImageView)findViewById(R.id.imagen);
        navButton=(Button)findViewById(R.id.navbutton);
        Bundle bundle=getIntent().getExtras();
        tit=bundle.getString("titulo");
        desc=bundle.getString("descripcion");
        urlImagen=bundle.getString("urlImagen");
        link=bundle.getString("link");
        titulo.setText(tit);
        descripcion.setText(desc);
        Picasso.with(DetalleActivity.this).load(urlImagen).into(imagen);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
