package com.belaku.kcet;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Collections;
import java.util.List;

import static com.belaku.kcet.QuestionsActivity.mInterstitialAd;
import static com.belaku.kcet.SplashActivity.makeToast;
import static com.belaku.kcet.SplashActivity.prodFlag;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    private static final int AD_TYPE = 0, CONTENT_TYPE = 1;
    List<QnA> list;
    Context context;
    private View v;

    public RvAdapter(List<QnA> data, Context context) {
        this.list = data;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        if (prodFlag)
            if (position != 0 && position % 5 == 0)
                return AD_TYPE;
        return CONTENT_TYPE;


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == CONTENT_TYPE) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            v = layoutInflater.inflate(R.layout.rv_row_layout, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        } else if (viewType == AD_TYPE) {

            AdView v = new AdView(context);

            v.setAdSize(AdSize.BANNER);

            v.setAdUnitId(context.getResources().getString(R.string.admob_banner_id));

            float density = context.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height);
            v.setLayoutParams(params);

            AdRequest adRequest = new AdRequest.Builder().build();
            if (adRequest != null && v != null) {
                v.loadAd(adRequest);
            }

            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;

        }

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (getItemViewType(position) == CONTENT_TYPE) {
            holder.textViewQ.setText(Html.fromHtml(list.get(position).getStringQ()));
            holder.textViewO.setText(Html.fromHtml("0.\t\t\t" + list.get(position).getArrayListOptions().get(0) + "<br>" +
                    "1.\t\t\t" + list.get(position).getArrayListOptions().get(1) + "<br>" +
                    "2.\t\t\t" + list.get(position).getArrayListOptions().get(2) + "<br>" +
                    "3.\t\t\t" + list.get(position).getArrayListOptions().get(3)));
            holder.textViewA.setText(Html.fromHtml("Ans : " + list.get(position).getStringA()));


            if (list.get(position).getStringQimg().length() > 0) {
                holder.imageViewQ.setVisibility(View.VISIBLE);
                Glide.with(context).load(list.get(position).getStringQimg()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        makeToast("Poor connectivity!");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.imageViewQ);
            } else holder.imageViewQ.setVisibility(View.GONE);
            if (list.get(position).getStringAimg().length() > 0) {
                holder.imageViewO.setVisibility(View.VISIBLE);
                Glide.with(context).load(list.get(position).getStringAimg()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        makeToast("Poor connectivity!");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.imageViewO);
            } else holder.imageViewO.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewQ, imageViewO;
        public TextView textViewQ, textViewO, textViewA;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageViewQ = itemView.findViewById(R.id.imgv_q);
            this.imageViewO = itemView.findViewById(R.id.imgv_o);
            this.textViewQ = itemView.findViewById(R.id.tx_q);
            this.textViewO = itemView.findViewById(R.id.tx_o);
            this.textViewA = itemView.findViewById(R.id.tx_a);
            this.progressBar = itemView.findViewById(R.id.progress);

        }
    }
}
