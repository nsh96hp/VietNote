package com.example.nsh96.vietnote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AdapterListNote extends RecyclerView.Adapter<AdapterListNote.ViewHolder> {
    Calendar calendar = Calendar.getInstance();

    Context context;
    DBManager dbm;
    private ArrayList<Note> lstNote;
    private ArrayList<Note> lstNewNote;


    public AdapterListNote(Context context, ArrayList<Note> lstNote, ArrayList<Note> lstNewNote) { //
        this.context = context;
        this.lstNote = lstNote;
        this.lstNewNote = lstNewNote;
    }

    private static OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, int id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void NewList() {
        lstNote = lstNewNote;
        _sort(lstNote, lstNewNote);
        notifyDataSetChanged();
    }


    public void add(Note note) {
        dbm = new DBManager(context);
        lstNote.add(note);
        lstNewNote = new ArrayList<>();
        lstNewNote = dbm.GetSticky();

        notifyItemInserted(lstNote.size());


        notifyItemRangeChanged(0, lstNote.size());
    }

    public void delete(int temp) {
        dbm = new DBManager(context);
        lstNote.remove(temp);
        lstNewNote = new ArrayList<>();
        lstNewNote = dbm.GetSticky();
        notifyItemRemoved(temp);
        notifyItemRangeChanged(0, lstNote.size());
    }

    public void changed(int temp, Note note) {
        dbm = new DBManager(context);
        lstNewNote = new ArrayList<>();
        lstNewNote = dbm.GetSticky();
        lstNote.set(temp, note);
        notifyItemChanged(temp);
    }

    public void SortListUP1() {
        Collections.sort(lstNote);
        Collections.sort(lstNewNote);
        notifyDataSetChanged();
    }

    public void SortListDown1() {
        Collections.sort(lstNote, Note.SortDownCreated);
        Collections.sort(lstNewNote, Note.SortDownCreated);
        notifyDataSetChanged();
    }

    public void SortUpTitle() {
        Collections.sort(lstNote, Note.SortUpTitle);
        Collections.sort(lstNewNote, Note.SortUpTitle);
        notifyDataSetChanged();
    }

    public void SortDownTitle() {
        Collections.sort(lstNote, Note.SortDownTitle);
        Collections.sort(lstNewNote, Note.SortDownTitle);
        notifyDataSetChanged();
    }

    public void SortDownAlarm() {
        Collections.sort(lstNote, Note.SortDownAlarm);
        Collections.sort(lstNewNote, Note.SortDownAlarm);
        notifyDataSetChanged();
    }

    public void SortUpAlarm() {
        Collections.sort(lstNote, Note.SortUpAlarm);
        Collections.sort(lstNewNote, Note.SortUpAlarm);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_note, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date_time = context.getResources().getString(R.string.datetime) + " " + lstNote.get(position).getNdate() + " " + lstNote.get(position).getNtime();
        holder.item_date_time.setText(date_time);
        holder.item_img_note.setVisibility(View.INVISIBLE);

        holder.item_tittleNote.setText(lstNote.get(position).getNtitlle());

        if (lstNote.get(position).getLocked() == 1) {
            holder.item_locked.setImageResource(R.drawable.ic_lock_outline_black_24dp);
            holder.item_content.setText(R.string.content1);
            holder.item_locked.setVisibility(View.VISIBLE);
        } else {
            String temp = lstNote.get(position).getNcontent();
            int l = temp.length();
            if (l < 20) {
                holder.item_content.setText(lstNote.get(position).getNcontent());
            } else {
                holder.item_content.setText(lstNote.get(position).getNcontent().substring(0, 15) + "...");
            }
            holder.item_locked.setVisibility(View.INVISIBLE);

        }
//
        if (sosanhhientai(lstNote.get(position).getNdate(), lstNote.get(position).getNdate())) {
            holder.ll_note.setBackgroundColor(0xFF00FF00);
        }

        if (lstNote.get(position).getNImgName().length() > 0) {

            String[] Image = lstNote.get(position).getNImgName().split("-----");

            Uri selectedImage = Uri.parse(Image[0]);
            Glide.with(context).load(selectedImage).into(holder.item_img_note);
            holder.item_img_note.setVisibility(View.VISIBLE);
        } else {
            holder.item_img_note.setVisibility(View.INVISIBLE);
        }

        switch (lstNote.get(position).getSaved()) {
            case 0:
                holder.ll_note.setBackgroundColor(context.getResources().getColor(R.color.colorBG0));
                break;
            case 1:
                holder.ll_note.setBackgroundColor(context.getResources().getColor(R.color.colorBG));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return lstNote.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_tittleNote, item_date_time, item_content;
        ImageView item_locked, item_img_note;
        LinearLayout ll_note;


        public ViewHolder(final View itemView) {
            super(itemView);

            ll_note = itemView.findViewById(R.id.ll_note);
            item_tittleNote = itemView.findViewById(R.id.item_titleNote);
            item_date_time = itemView.findViewById(R.id.item_date_time);
            item_content = itemView.findViewById(R.id.item_ContentNote);
            item_locked = itemView.findViewById(R.id.item_locked);
            item_img_note = itemView.findViewById(R.id.item_img_note);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idpos = lstNote.get(getAdapterPosition()).getId();
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition(), idpos);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dbm = new DBManager(context);
                    final int idpos = lstNote.get(getAdapterPosition()).getId();

                    if (lstNote.get(getAdapterPosition()).getLocked() == 1) {
                        final Dialog dialog = new Dialog(context, R.style.mydialogstyle);
                        dialog.setContentView(R.layout.dialog_password);


                        final EditText edt_pass = dialog.findViewById(R.id.edt_pass);
                        TextView txt_title = dialog.findViewById(R.id.txt_title);
                        txt_title.setText(R.string.do_you_want_delete);
                        Button btn_back = dialog.findViewById(R.id.btn_pass_back);
                        Button btn_ok = dialog.findViewById(R.id.btn_pass_ok);

                        btn_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (lstNote.get(getAdapterPosition()).getNpass().compareTo(edt_pass.getText().toString()) == 0) {
                                    dbm.deleteNote(idpos);
                                    Toast.makeText(context, context.getResources().getString(R.string.note) + ": " + lstNote.get(getAdapterPosition()).getNtitlle() + " " + context.getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();

                                    lstNote.remove(getAdapterPosition());

                                    lstNewNote = dbm.GetSticky();


                                    notifyItemRemoved(getAdapterPosition());
                                    notifyItemRangeChanged(getAdapterPosition(), lstNote.size());
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    edt_pass.setText("");
                                    edt_pass.setHint(R.string.fail);
                                }
                            }
                        });
                        dialog.show();
                    } else {
                        final Dialog dialog = new Dialog(context, R.style.mydialogstyle);
                        dialog.setContentView(R.layout.dialog_delete);

                        Button btn_back = dialog.findViewById(R.id.btn_delete_back);
                        Button btn_ok = dialog.findViewById(R.id.btn_delete_ok);

                        btn_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dbm.deleteNote(idpos);

                                Toast.makeText(context, context.getResources().getString(R.string.note) + ": " + lstNote.get(getAdapterPosition()).getNtitlle() + " " + context.getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();

                                lstNote.remove(getAdapterPosition());
                                lstNewNote = dbm.GetSticky();

                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), lstNote.size());
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    }
                    return false;
                }
            });

        }
    }

    private boolean sosanhhientai(String date2, String time2) {
        String[] datetime = calendar.getTime().toString().split(" ");
        ContentNote cn = new ContentNote();
        cn.changeDateTime(datetime);
        String date1 = datetime[2] + "/" + datetime[1] + "/" + datetime[5];
        String time1 = datetime[3];
        MainActivity mainActivity = new MainActivity();
        return mainActivity.sosanhngay(date1, date2, time1, time2);
    }


    public void _sort(ArrayList<Note> lstNote, ArrayList<Note> lstNewNote) {
        DBManager db = new DBManager(context);
        if (db.GetSortNote().size() > 0) {
            if (db.GetSortNote().get(0).getUpdown() == 1) {
                switch (db.GetSortNote().get(0).getStyleSort()) {
                    case 0:
                        Collections.sort(lstNote);
                        Collections.sort(lstNewNote);
                        break;
                    case 1:
                        Collections.sort(lstNote, Note.SortUpTitle);
                        Collections.sort(lstNewNote, Note.SortUpTitle);
                        break;
                    case 2:
                        Collections.sort(lstNote, Note.SortUpAlarm);
                        Collections.sort(lstNewNote, Note.SortUpAlarm);
                        break;
                }
            } else {
                switch (db.GetSortNote().get(0).getStyleSort()) {
                    case 0:
                        Collections.sort(lstNote, Note.SortDownCreated);
                        Collections.sort(lstNewNote, Note.SortDownCreated);
                        break;
                    case 1:
                        Collections.sort(lstNote, Note.SortDownTitle);
                        Collections.sort(lstNewNote, Note.SortDownTitle);
                        break;
                    case 2:
                        Collections.sort(lstNote, Note.SortDownAlarm);
                        Collections.sort(lstNewNote, Note.SortDownAlarm);
                        break;
                }
            }
        }
    }
}
