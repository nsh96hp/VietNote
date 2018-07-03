package com.example.nsh96.vietnote;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Adapter_Image extends RecyclerView.Adapter<Adapter_Image.ViewHolder> {

    Context context;
    private ArrayList<String> lstImage;

    public Adapter_Image(Context context, ArrayList<String> lstImage) {
        this.context = context;
        this.lstImage = lstImage;
    }

    int sl=1;
    @NonNull
    @Override
    public Adapter_Image.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_cardview_note, parent, false);
        return new ViewHolder(itemView);
    }

    private static OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View itemView,int position,String path);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    private static OnLongItemClickListener listener1;
    public interface OnLongItemClickListener{
        void onItemClick(View itemView,int position,String pathl);
    }
    public void setOnLongItemClickListener(OnLongItemClickListener listener1){
        this.listener1=listener1;
    }

    public void add(String temp) {
        sl++;
        notifyItemInserted(lstImage.size());
        notifyItemRangeChanged(0, lstImage.size());
    }
    public void delete(int temp) {
        sl--;
        notifyItemRemoved(temp);
        notifyItemRangeChanged(0, lstImage.size());
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Image.ViewHolder holder, int position) {
        sl=lstImage.size();
        Uri selectedImage = Uri.parse(lstImage.get(position));
        Glide.with(context).load(selectedImage).into(holder.item_c_img_note);


        if(sl==1){
            holder.img_cv.requestLayout();
            holder.img_cv.getLayoutParams().width=getScreenWidth()/(1)-40;
            holder.img_cv.getLayoutParams().height=getScreenWidth()/(1)-40;

            holder.item_c_img_note.requestLayout();
            holder.item_c_img_note.getLayoutParams().width=getScreenWidth()/(1)-40;
            holder.item_c_img_note.getLayoutParams().height=getScreenWidth()/(1)-40;
        }else {
            holder.item_c_img_note.requestLayout();
            holder.item_c_img_note.getLayoutParams().width=getScreenWidth()/(2)-40;
            holder.item_c_img_note.getLayoutParams().height=getScreenWidth()/(2)-40;
            holder.img_cv.requestLayout();
            holder.img_cv.getLayoutParams().width=getScreenWidth()/(2)-40;
            holder.img_cv.getLayoutParams().height=getScreenWidth()/(2)-40;
        }


    }
    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    @Override
    public int getItemCount() {
        return lstImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView img_cv;
        ImageView item_c_img_note;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition(),lstImage.get(getAdapterPosition()).toString());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener1 != null)
                        listener1.onItemClick(itemView, getLayoutPosition(),lstImage.get(getAdapterPosition()).toString());
                    return false;
                }
            });
            img_cv=itemView.findViewById(R.id.img_cv);
            item_c_img_note=itemView.findViewById(R.id.item_c_img_note);
        }
    }
}
