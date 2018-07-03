package com.example.nsh96.vietnote;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    RecyclerView rcvNote;
    DBManager db;
    AdapterListNote adapter;
    ArrayList<Note> lstNote;
    ArrayList<Note> lstNewNote;

    Calendar calendar = Calendar.getInstance();
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public static final String LINK_APP1 = "https://play.google.com/store/apps/details?id=com.hdpsolution.ghichutiengviet";
    public static final String LINK_APP2 = "market://details?id=com.hdpsolution.ghichutiengviet";

    int StickyKey = 0;
    int udKey = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.note));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_app1);
        actionBar.setDisplayUseLogoEnabled(true);

        handleAlarm();


        db = new DBManager(MainActivity.this);
        rcvNote = findViewById(R.id.rcvNote);
        lstNote = new ArrayList<>();
        lstNewNote = new ArrayList<>();
        lstNote = db.GetAll();
        lstNewNote = db.GetSticky();
        _sort(lstNote, lstNewNote);

        adapter = new AdapterListNote(MainActivity.this, lstNote, lstNewNote);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvNote.setLayoutManager(linearLayoutManager);
        rcvNote.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new AdapterListNote.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position, final int id) {
                final Note note;
                if (StickyKey == 0) {
                    note = lstNote.get(position);
                } else {
                    lstNewNote = db.GetSticky();
                    _sort(lstNote, lstNewNote);
                    note = lstNewNote.get(position);
                }

                if (note.getLocked() == 1) {
                    final Dialog dialog = new Dialog(MainActivity.this, R.style.mydialogstyle);
                    dialog.setContentView(R.layout.dialog_password);


                    final EditText edt_pass = dialog.findViewById(R.id.edt_pass);
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
                            if (note.getNpass().compareTo(edt_pass.getText().toString()) == 0) {
                                Intent intent = new Intent(MainActivity.this, ContentNote.class);
                                Bundle bundle = new Bundle();

                                bundle.putInt(VietNote.KEY_EDIT, 0);

                                bundle.putInt(VietNote.ID_KEY, id);
                                bundle.putInt(VietNote.POS_KEY, position);
                                bundle.putSerializable(VietNote.KEY_NOTED, note);

                                intent.putExtra(VietNote.KEY_INFO, bundle);
                                startActivityForResult(intent, 1);
                                dialog.dismiss();
                            } else {
                                edt_pass.setText("");
                                edt_pass.setHint(R.string.fail);
                            }
                        }
                    });
                    dialog.show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ContentNote.class);
                    Bundle bundle = new Bundle();

                    bundle.putInt(VietNote.KEY_EDIT, 0);

                    bundle.putInt(VietNote.ID_KEY, id);
                    bundle.putInt(VietNote.POS_KEY, position);
                    bundle.putSerializable(VietNote.KEY_NOTED, note);


                    intent.putExtra(VietNote.KEY_INFO, bundle);

                    startActivityForResult(intent, 1);

                }

            }
        });
    }


    private void handleAlarm() {
        db = new DBManager(MainActivity.this);
        lstNote = db.GetAll();

        if (lstNote.size() >= 2) {
            ArrayList<Note> lst;
            lst = lstNote;
            for (int i = 0; i < lstNote.size(); i++) {
                for (int j = i + 1; j < lstNote.size(); j++) {
                    if (sosanhngay(lstNote.get(i).getNdate(), lstNote.get(j).getNdate(), lstNote.get(i).getNtime(), lstNote.get(j).getNtime()) == false) {
                        Collections.swap(lst, i, j);
                    }
                }
            }

            String[] datetime = calendar.getTime().toString().split(" ");
            ContentNote cn = new ContentNote();
            cn.changeDateTime(datetime);
            String tdate = datetime[2] + "/" + datetime[1] + "/" + datetime[5];
            String ttime = datetime[3];

            for (int i = 0; i < lst.size(); i++) {
                if (sosanhngay(lst.get(i).getNdate(), tdate, lst.get(i).getNtime(), ttime) == false) {
                    String[] date = lstNote.get(i).getNdate().split("/");
                    String[] time = lstNote.get(i).getNtime().split(":");
                    hengio(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                    break;
                }
            }
        } else {
            if (lstNote.size() == 1) {
                String[] datetime = calendar.getTime().toString().split(" ");
                ContentNote cn = new ContentNote();
                cn.changeDateTime(datetime);
                String tdate = datetime[2] + "/" + datetime[1] + "/" + datetime[5];
                String ttime = datetime[3];
                if (sosanhngay(lstNote.get(0).getNdate(), tdate, lstNote.get(0).getNtime(), ttime) == false) {
                    String[] date = lstNote.get(0).getNdate().split("/");
                    String[] time = lstNote.get(0).getNtime().split(":");
                    hengio(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                }
            }
        }
    }

    public boolean sosanhngay(String date1, String date2, String time1, String time2) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dd1 = null;
        Date dd2 = null;
        try {
            dd1 = format.parse(date1 + " " + time1);
            dd2 = format.parse(date2 + " " + time2);
            long diff = dd2.getTime() - dd1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if ((diffDays + diffHours + diffMinutes + diffSeconds) > 0) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


    private void hengio(int hours, int minute, int day, int month, int year) {

        db = new DBManager(MainActivity.this);
        ArrayList<com.example.nsh96.vietnote.Notification> lstAlarm = db.GetNotiTime();
        if (lstAlarm.size() > 0) {
            //Dat trc
            float diffH = (float) 0.5;
            float diffD = (float) 0.5;

            switch (lstAlarm.get(0).getDv()) {
                case 0:
                    diffH = hours - lstAlarm.get(0).getTime();
                    break;
                case 1:
                    diffD = day - lstAlarm.get(0).getTime();
                    break;
            }
            if (diffH < 0) {
                hours = (int) (24 + diffH);
                diffD = day - 1;
            } else {
                if (diffH > 0.5) {
                    hours = (int) diffH;
                } else {
                    if (diffH == 0) {
                        hours = 0;
                    }
                }
            }
            ////
            if (diffD < 0.5) {
                month = month - 1;
                switch (month) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        day = (int) (31 + diffD);
                        break;
                    case 2:
                        if (year == 2020 || year == 2024) {
                            day = (int) (29 + diffD);
                        } else {
                            day = (int) (28 + diffD);
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        day = (int) (31 + diffD);
                        break;
                    default:
                        break;
                }
            } else {
                if (diffD > 0.5) {
                    day = (int) diffD;
                }
            }
        }


        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra("Music", "On");
        intent.putExtra("Day", day);
        intent.putExtra("Month", month);
        intent.putExtra("Year", year);


        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.e("Bao thuc", day + "/" + month + "/" + year + " " + hours + ":" + minute);
        Toast.makeText(MainActivity.this, getResources().getString(R.string.next_time_alarm) + day + "/" + month + "/" + year + " " + hours + ":" + minute, Toast.LENGTH_LONG).show();

    }

    private void pin() {
        db = new DBManager(MainActivity.this);
        final ArrayList<Pin> arr = db.GetPINCODE();

        if (arr.size() == 0) {
            final Dialog dialog = new Dialog(MainActivity.this, R.style.mydialogstyle);
            dialog.setContentView(R.layout.create_pin);


            final EditText edt_key_pin1 = dialog.findViewById(R.id.edt_key_pin1);
            final EditText edt_key_pin2 = dialog.findViewById(R.id.edt_key_pin2);
            Button btn_back = dialog.findViewById(R.id.btn_pin_back);
            Button btn_ok = dialog.findViewById(R.id.btn_pin_ok);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edt_key_pin1.getText().toString().compareTo((edt_key_pin2.getText().toString())) == 0) {
                        if (edt_key_pin1.getText().toString().length() >= 4 && edt_key_pin1.getText().toString().length() <= 8) {
                            Pin pin = new Pin(Integer.parseInt(edt_key_pin1.getText().toString()));
                            db.Add_PIN(pin);
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.Done), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    } else {
                        edt_key_pin1.setText("");
                        edt_key_pin2.setText("");
                        edt_key_pin1.setHint(R.string.fail);
                    }
                }
            });
            dialog.show();
        } else {
            final Dialog dialog = new Dialog(MainActivity.this, R.style.mydialogstyle);
            dialog.setContentView(R.layout.keyapp);

            final EditText edt_key_pin = dialog.findViewById(R.id.edt_key_pin);
            Button btn_back = dialog.findViewById(R.id.btn_key_back);
            Button btn_ok = dialog.findViewById(R.id.btn_key_ok);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arr.get(0).getPincode() == Integer.parseInt(edt_key_pin.getText().toString())) {
                        db.PinUNLOCK();
                        dialog.dismiss();
                        finish();
                        startActivity(getIntent());
                    } else {
                        edt_key_pin.setText("");
                        edt_key_pin.setHint(R.string.fail);
                    }
                }
            });
            dialog.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification) {
            final Dialog dialog = new Dialog(MainActivity.this, R.style.mydialogstyle);
            dialog.setContentView(R.layout.dialog_alarm);

            final EditText edt_hours = dialog.findViewById(R.id.edt_hours);
            db = new DBManager(MainActivity.this);
            final ArrayList<com.example.nsh96.vietnote.Notification> lst = db.GetNotiTime();

            final Spinner spinner = dialog.findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            getResources().getStringArray(R.array.dv_time)
                    );
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (lst.size() > 0) {
                edt_hours.setText(lst.get(0).getTime() + "");
                spinner.setSelection(lst.get(0).getDv());
            }

            Button btn_back = dialog.findViewById(R.id.btn_noti_back);
            Button btn_ok = dialog.findViewById(R.id.btn_noti_ok);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = new DBManager(MainActivity.this);
                    ArrayList<com.example.nsh96.vietnote.Notification> lst = db.GetNotiTime();
                    if (edt_hours.getText().toString().length() < 1) {
                        edt_hours.setText("0");
                    }
                    if (spinner.getSelectedItemPosition() == 0) {
                        if (Integer.parseInt(edt_hours.getText().toString()) <= 24) {
                            com.example.nsh96.vietnote.Notification noti = new com.example.nsh96.vietnote.Notification(Integer.parseInt(edt_hours.getText().toString()), spinner.getSelectedItemPosition());
                            if (lst.size() > 0) {
                                db.EditNoti(noti);
                                Log.e("Notifi", noti.toString());

                            } else {
                                db.Add_Noti(noti);
                                Log.e("Notifi", noti.toString());

                            }
                            dialog.dismiss();
                            handleAlarm();
                        } else {
                            edt_hours.setText("");
                            edt_hours.setHint(R.string.hours024);
                        }
                    } else {
                        if (Integer.parseInt(edt_hours.getText().toString()) <= 15) {
                            com.example.nsh96.vietnote.Notification noti = new com.example.nsh96.vietnote.Notification(Integer.parseInt(edt_hours.getText().toString()), spinner.getSelectedItemPosition());
                            if (lst.size() > 0) {
                                db.EditNoti(noti);
                                Log.e("Notifi", noti.toString());

                            } else {
                                db.Add_Noti(noti);
                                Log.e("Notifi", noti.toString());

                            }
                            handleAlarm();
                            dialog.dismiss();
                        } else {
                            edt_hours.setText("");
                            edt_hours.setHint(R.string.day015);
                        }
                    }
                }
            });

            dialog.show();

        }
        if (id == R.id.lock) {
            pin();
        }

        if (id == R.id.newnote) {

            Intent intent = new Intent(this, ContentNote.class);
            Bundle bundle = new Bundle();
            bundle.putInt(VietNote.KEY_EDIT, 1);

            intent.putExtra(VietNote.KEY_INFO, bundle);
            startActivityForResult(intent, 2);
        }

        if (id == R.id.share) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, LINK_APP1);
            try {
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), R.string.not_share, Toast.LENGTH_LONG);
            }

        }

        if (id == R.id.rateapp) {
            Uri uri = Uri.parse(LINK_APP2);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            if (!MyAppActivity(intent)) {
                Uri uri1 = Uri.parse(LINK_APP1);
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                if (!MyAppActivity(intent1)) {
                    Toast.makeText(getApplicationContext(), R.string.not_rate, Toast.LENGTH_LONG);
                }
            }
        }
        if (id == R.id.lststicky) {

            if (StickyKey == 0) {

                adapter.NewList();

                ActionBar actionBar = getSupportActionBar();
                //
                actionBar.setTitle(getResources().getString(R.string.note));
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setLogo(R.drawable.ic_app1);
                actionBar.setDisplayUseLogoEnabled(false);
                //
                actionBar.setDisplayHomeAsUpEnabled(true);
                StickyKey = 1;
            }
        }

        if (id == R.id.menu_sort) {
            final Dialog dialog = new Dialog(MainActivity.this, R.style.mydialogstyle);
            dialog.setContentView(R.layout.dialog_sort);
            final DBManager db = new DBManager(MainActivity.this);

            final Spinner spinner = dialog.findViewById(R.id.m_spinner);
            ArrayList<String> arrSpinner = new ArrayList<>();
            arrSpinner.add(getResources().getString(R.string.Sort_created));
            arrSpinner.add(getResources().getString(R.string.Sort_title));
            arrSpinner.add(getResources().getString(R.string.Sort_alarm));
            final ArrayAdapter<String> adapterSpin = new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item, arrSpinner
                    );
            adapterSpin.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            spinner.setAdapter(adapterSpin);

            final ImageButton btn_up = dialog.findViewById(R.id.m_btn_sort_up);
            final ImageButton btn_down = dialog.findViewById(R.id.m_btn_sort_down);

            if (db.GetSortNote().size() > 0) {
                spinner.setSelection(db.GetSortNote().get(0).getStyleSort());
                udKey = db.GetSortNote().get(0).getUpdown();
            }
            if (udKey == 1) {
                udKey = 1;
                btn_up.setVisibility(View.VISIBLE);
                btn_down.setVisibility(View.GONE);
            } else {
                btn_up.setVisibility(View.GONE);
                btn_down.setVisibility(View.VISIBLE);
                udKey = 0;
            }

            btn_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_up.setVisibility(View.VISIBLE);
                    btn_down.setVisibility(View.GONE);
                    udKey = 1;
                }
            });

            btn_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_up.setVisibility(View.GONE);
                    btn_down.setVisibility(View.VISIBLE);
                    udKey = 0;
                }
            });

            Button btn_back = dialog.findViewById(R.id.btn_s_back);
            Button btn_ok = dialog.findViewById(R.id.btn_s_ok);

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SortNote sortNote = new SortNote();
                    sortNote.setUpdown(udKey);

                    sortNote.setStyleSort(spinner.getSelectedItemPosition());
                    if (db.GetSortNote().size() > 0) {
                        db.EditSORT(sortNote);
                    } else {
                        db.Add_SORT(sortNote);
                    }

                    if (udKey == 1) {
                        switch (db.GetSortNote().get(0).getStyleSort()) {
                            case 0:
                                adapter.SortListUP1();

                                break;
                            case 1:
                                adapter.SortUpTitle();

                                break;
                            case 2:
                                adapter.SortUpAlarm();

                                break;
                        }
                    } else {
                        switch (db.GetSortNote().get(0).getStyleSort()) {
                            case 0:
                                adapter.SortListDown1();

                                break;
                            case 1:
                                adapter.SortDownTitle();

                                break;
                            case 2:
                                adapter.SortDownAlarm();

                                break;
                        }
                    }

                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean MyAppActivity(Intent intent) {
        try {
            startActivity(intent);
            return (true);
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {

        if (StickyKey == 1) {
            Intent a = new Intent(this, MainActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            StickyKey = 0;
        }


        return super.getSupportParentActivityIntent();
    }

    @Override
    public void onBackPressed() {
        if (StickyKey == 1) {
            StickyKey = 0;
            finish();
            startActivity(getIntent());
        }
        super.onBackPressed();
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull android.support.v4.app.TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        db = new DBManager(MainActivity.this);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(VietNote.RESULT_DELETE, -1);
                if (result != -1) {
                    adapter.delete(result);
                }
                ///////
                try {
                    Bundle bundle = data.getBundleExtra(VietNote.RESULT_EDIT);
                    Note note = (Note) bundle.getSerializable(VietNote.KEY_NOTED);
                    int pos = bundle.getInt(VietNote.POS_EDIT, -1);
                    if (pos != -1) {                        //edit
                        adapter.changed(pos, note);
                    }
                    if (StickyKey == 1) {
                        if (note.getSaved() == 0) {
                            adapter.delete(pos);
                        }
                    }

                } catch (NullPointerException e) {
                }


            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getBundleExtra(VietNote.KEY_RESULT_ADD);
                Note note = (Note) bundle.getSerializable(VietNote.KEY_NOTED);
                if (StickyKey == 0) {
                    adapter.add(note);
                } else {
                    if (note.getSaved() == 1) {
                        adapter.add(note);
                    }
                }
            }


        }
    }

    public void _sort(ArrayList<Note> lstNote, ArrayList<Note> lstNewNote) {
        db = new DBManager(MainActivity.this);
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
