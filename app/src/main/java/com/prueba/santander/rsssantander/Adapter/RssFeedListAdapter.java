package com.prueba.santander.rsssantander.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prueba.santander.rsssantander.Beans.Noticia;
import com.prueba.santander.rsssantander.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by David on 09/07/2017.
 */

public class RssFeedListAdapter
        extends RecyclerView.Adapter<RssFeedListAdapter.FeedModelViewHolder>
        implements View.OnClickListener{

    private List<Noticia> noticias;
    private View.OnClickListener listener;

    @Override
    public void onClick(View view) {
        if(listener != null){
            listener.onClick(view);
        }

    }

    public class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }
    }

    public RssFeedListAdapter(List<Noticia> noticias) {
        this.noticias = noticias;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rss_feed, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        v.setOnClickListener(this);
        return holder;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final Noticia noticia = noticias.get(position);
        ((TextView)holder.rssFeedView.findViewById(R.id.titleText)).setText(noticia.title);
        ((TextView)holder.rssFeedView.findViewById(R.id.descriptionText))
                .setText(noticia.description);
        ImageView imagen=(ImageView)holder.rssFeedView.findViewById(R.id.imageView);
        Picasso.with(noticia.ctx)
                .load(noticia.imageUrl)
                .into(imagen);
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }


}
