package com.prueba.santander.rsssantander.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.prueba.santander.rsssantander.Adapter.RssFeedListAdapter;
import com.prueba.santander.rsssantander.Beans.Noticia;
import com.prueba.santander.rsssantander.R;
import com.prueba.santander.rsssantander.Util.BBDD;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Button mFetchFeedButton;
    //private SwipeRefreshLayout mSwipeLayout;
    private String link;
    private List<Noticia> mFeedModelList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private String urlImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mEditText = (EditText) findViewById(R.id.rssFeedEditText);
        mEditText.setText(getString(R.string.url));
        mFetchFeedButton = (Button) findViewById(R.id.fetchFeedButton);
        //mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFetchFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchFeedTask().execute((Void) null);
            }
        });
        /*mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });*/
    }


    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        @Override
        protected void onPreExecute() {
            //mSwipeLayout.setRefreshing(true);
            urlLink = mEditText.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(urlLink))
                return false;

            try {
                if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "http://" + urlLink;

                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("David", "Error", e);
                Toast.makeText(ListActivity.this,getString(R.string.nointernet),Toast.LENGTH_LONG).show();
                BBDD bbdd=new BBDD(ListActivity.this);
                mFeedModelList = bbdd.recuperarNoticiasLeidas();
            } catch (XmlPullParserException e) {
                Log.e("David", "Error", e);
                Toast.makeText(ListActivity.this,getString(R.string.nointernet),Toast.LENGTH_LONG).show();
                BBDD bbdd=new BBDD(ListActivity.this);
                mFeedModelList = bbdd.recuperarNoticiasLeidas();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            //mSwipeLayout.setRefreshing(false);

            if (success) {
                Log.i("David", "Creamos el adapter");
                RssFeedListAdapter adapter=new RssFeedListAdapter(mFeedModelList);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BBDD bbdd=new BBDD(ListActivity.this);
                        ArrayList<Noticia> noticias=bbdd.recuperarNoticiasLeidas();
                        boolean presente=false;
                        for(int i=0;i<noticias.size();i++){
                            if(noticias.get(i).title.equals(mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).title)){
                                presente=true;
                            }
                        }
                        if(!presente){
                            bbdd.insertarNoticiaLeida(new Noticia(mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).title,
                                    mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).description,
                                    mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).imageUrl,
                                    mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).link,
                                    ListActivity.this));
                        }

                        Log.i("David", "Hemos pulsado el elemento "+mRecyclerView.getChildLayoutPosition(view));
                        Log.i("David", "El título: "+mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).title);
                        Intent intent=new Intent(ListActivity.this, DetalleActivity.class);
                        intent.putExtra("titulo", mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).title);
                        intent.putExtra("descripcion", mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).description);
                        intent.putExtra("urlImagen", mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).imageUrl);
                        intent.putExtra("link", mFeedModelList.get(mRecyclerView.getChildLayoutPosition(view)).link);
                        startActivity(intent);
                    }
                });
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(ListActivity.this,
                        getString(R.string.norss),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public List<Noticia> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String description = null;
        boolean isItem = false;
        List<Noticia> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && description != null) {
                    if(isItem) {
                        String desc=procesarDescripcion(description);
                        Noticia item = new Noticia(title, desc, link, urlImagen, ListActivity.this);
                        items.add(item);
                        BBDD bbdd=new BBDD(ListActivity.this);
                        bbdd.insertarNoticia(item);
                    }
                    else {
                        mFeedTitle = title;
                        //mFeedLink = link;
                        mFeedDescription = description;
                    }

                    title = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }

    private String procesarDescripcion(String description) {

        Log.i("David", "Descripcion: "+description);
        String regexString = Pattern.quote("<p>") + "(.*?)" + Pattern.quote("</p>");
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(description);
        String textoSinParrafos="";
        while (matcher.find()) {
            textoSinParrafos+=matcher.group(1);
        }
        Log.i("David", "Texto sin parrafos: "+textoSinParrafos);
        String textoUrl="";
        String regexStringUrl = Pattern.quote("<img") + "(.*?)" + Pattern.quote("/>");
        Pattern patternUrl = Pattern.compile(regexStringUrl);
        Matcher matcherUrl = patternUrl.matcher(textoSinParrafos);
        while (matcherUrl.find()) {
            textoUrl+=matcherUrl.group(1);
            break;
        }
        Log.i("David", "El texto con la url: "+textoUrl);
        textoSinParrafos=textoSinParrafos.replace(textoUrl,"");
        textoSinParrafos=textoSinParrafos.replace("<img/>", "");

        if(!textoUrl.equals("")){
            String enlaceImagen;
            enlaceImagen=textoUrl.substring(textoUrl.indexOf("//"));
            Log.i("David", "Texto con el enlace de la imagen, antes de quitarle el último caracter: "+enlaceImagen);
            enlaceImagen=enlaceImagen.substring(0, enlaceImagen.length() - 1);
            enlaceImagen="http:"+enlaceImagen.substring(0, enlaceImagen.length() - 1);
            Log.i("David", "Enlace de la imagen: "+enlaceImagen);
            urlImagen=enlaceImagen;
        }

        return textoSinParrafos;
    }
}
