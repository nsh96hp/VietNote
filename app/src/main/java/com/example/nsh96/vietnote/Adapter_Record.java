package com.example.nsh96.vietnote;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Adapter_Record extends RecyclerView.Adapter<Adapter_Record.ViewHolder> {

    Context context;
    Activity mActivity;
    private ArrayList<String> lstRecord;
    int sl=1;


    public Adapter_Record(Context context, Activity mActivity, ArrayList<String> lstRecord) {
        this.context = context;
        this.lstRecord = lstRecord;
        this.mActivity = mActivity;
    }

    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, String path);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private static OnLongItemClickListener listener1;

    public interface OnLongItemClickListener {
        void onItemClick(View itemView, int position, String path);
    }

    public void setOnLongItemClickListener(OnLongItemClickListener listener1) {
        this.listener1 = listener1;
    }

    public void add(String temp) {
        //lstRecord.add(temp);
        sl++;
        notifyItemInserted(lstRecord.size());
        notifyItemRangeChanged(0, lstRecord.size());
    }
    public void delete(int temp) {
        sl--;
        notifyItemRemoved(temp);
        notifyItemRangeChanged(0, lstRecord.size());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_record_note, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        sl=lstRecord.size();
        if(sl==1){
            holder.cv_record_item.requestLayout();
            holder.cv_record_item.getLayoutParams().width=getScreenWidth()/(1)-40;


            holder.btn_play_record.requestLayout();
            holder.btn_play_record.getLayoutParams().width=getScreenWidth()/(1)-40;


            holder.btn_pause_record.requestLayout();
            holder.btn_pause_record.getLayoutParams().width=getScreenWidth()/(1)-40;

        }else {
            holder.cv_record_item.requestLayout();
            holder.cv_record_item.getLayoutParams().width=getScreenWidth()/(2)-40;


            holder.btn_play_record.requestLayout();
            holder.btn_play_record.getLayoutParams().width=getScreenWidth()/(2)-40;

            holder.btn_pause_record.requestLayout();
            holder.btn_pause_record.getLayoutParams().width=getScreenWidth()/(2)-40;
        }


        holder.btn_pause_record.setVisibility(View.GONE);
        holder.btn_play_record.setVisibility(View.VISIBLE);
        holder.txt_temp.setText(context.getResources().getString(R.string.Time) + "0s");




    }
    private static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    public int getItemCount() {
        return lstRecord.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btn_play_record, btn_pause_record;
        CardView cv_record_item;
        TextView txt_temp;
        MediaPlayer mediaPlayer = new MediaPlayer();
        int keyMedia = 0;

        public ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener1 != null)
                        listener1.onItemClick(itemView, getAdapterPosition(), lstRecord.get(getAdapterPosition()).toString());
                    return false;
                }
            });



            btn_pause_record = itemView.findViewById(R.id.btn_pause_record1);
            btn_play_record = itemView.findViewById(R.id.btn_play_record1);
            txt_temp = itemView.findViewById(R.id.txt_temp1);
            cv_record_item=itemView.findViewById(R.id.cv_record_item);


            btn_play_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mediaRecorder = new MediaRecorder();
                    if (mediaPlayer.isPlaying()) {
                        keyMedia = 1;
                    }
                    if (keyMedia == 1) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                    try {
                        mediaPlayer.setDataSource(lstRecord.get(getAdapterPosition()).toString());
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    keyMedia = 0;

                    btn_pause_record.setVisibility(View.VISIBLE);
                    btn_play_record.setVisibility(View.GONE);

                    final Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                        txt_temp.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int temp = Math.round(mediaPlayer.getCurrentPosition() / 1000)+1;
                                                txt_temp.setText(context.getResources().getString(R.string.Time) + temp + "s");
                                            }
                                        });
                                    } else {
                                        timer.cancel();
                                        timer.purge();
                                        btn_pause_record.setVisibility(View.GONE);
                                        btn_play_record.setVisibility(View.VISIBLE);
                                        txt_temp.setText(context.getResources().getString(R.string.Time) + "0s");
                                        keyMedia = 1;
                                    }
                                }
                            });
                        }
                    }, 0, 1000);
                }
            });

            ////////////
            btn_pause_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_pause_record.setVisibility(View.GONE);
                    btn_play_record.setVisibility(View.VISIBLE);
                    if (mediaPlayer != null) {
                        txt_temp.setText(context.getResources().getString(R.string.Time) + "0s");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                }
            });
        }
    }
}
