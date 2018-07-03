package com.example.nsh96.vietnote;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.kunzisoft.switchdatetime.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ContentNote extends AppCompatActivity {
    EditText cn_tittle, cn_content;
    TextView cn_datetime;
    ImageButton btn_date_time;

    Button btn_start_record, btn_end_record;
    LinearLayout ll_record1;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy H-mm");
    int editer;
    int idkey;
    private Uri filePath;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String pathSave = "";
    String pathSaved = "";
    String[] arrPathSave;
    String pathImg = "";
    String pathImgSaved = "";
    String[] arrImgSave;

    Adapter_Image adapter_image;
    ArrayList<String> lstImg;
    ArrayList<String> lstNewImg;
    RecyclerView rcv_img;

    Adapter_Record adapterRecord;
    ArrayList<String> lstRecord;
    ArrayList<String> newRecord;
    RecyclerView rcv_record;


    private boolean checkper = false;
    int pos=-1;
    int sav=0;

    DBManager dbManager = new DBManager(ContentNote.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_note);

        ActionBar actionBar = getSupportActionBar();
        //
        actionBar.setTitle(getResources().getString(R.string.note));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setLogo(R.drawable.ic_app1);
        actionBar.setDisplayUseLogoEnabled(false);
        //
        actionBar.setDisplayHomeAsUpEnabled(true);

        findbyId();


        final Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(VietNote.KEY_INFO);
        checkPermission(ContentNote.this);
        int edit = bundle.getInt(VietNote.KEY_EDIT);


        if (edit == 0) {
            editer = 0;
            idkey = bundle.getInt(VietNote.ID_KEY);

            pos=bundle.getInt(VietNote.POS_KEY);
            Note note = (Note) bundle.getSerializable(VietNote.KEY_NOTED);
            sav=    note.getSaved();
            cn_tittle.setText(note.getNtitlle());
            cn_content.setText(note.getNcontent());

            if (note.getRecord().length() > 0) {
                pathSave = note.getRecord();
            } else {
                rcv_record.setVisibility(View.GONE);
            }
            if (note.getNImgName().length() > 0) {
                pathImg = note.getNImgName();
            } else {
                rcv_img.setVisibility(View.GONE);
            }
            cn_datetime.setText(note.getNdate() + " " + note.getNtime());

            arrPathSave = note.getRecord().split("-----");
            arrImgSave = note.getNImgName().split("-----");

        } else {
            editer = 1;
        }
        //Load lan dau khi sua
        lstRecord = new ArrayList<>();
        newRecord = new ArrayList<>();
        if (arrPathSave != null) {
            if (arrPathSave[0].compareTo("") != 0) {
                if (arrPathSave.length > 0) {
                    for (int i = 0; i < arrPathSave.length; i++) {
                        lstRecord.add(arrPathSave[i]);
                    }
                }
            }
        }

        adapterRecord = new Adapter_Record(ContentNote.this, ContentNote.this, lstRecord);
        rcv_record.setLayoutManager(new GridLayoutManager(this, 2));
        rcv_record.setAdapter(adapterRecord);


        lstNewImg = new ArrayList<>();
        lstImg = new ArrayList<>();
        if (arrImgSave != null) {
            if (arrImgSave[0].compareTo("") != 0) {
                if (arrImgSave.length > 0) {
                    for (int i = 0; i < arrImgSave.length; i++) {
                        lstImg.add(arrImgSave[i]);
                    }
                }
            }
        }

        adapter_image = new Adapter_Image(ContentNote.this, lstImg);
        rcv_img.setLayoutManager(new GridLayoutManager(this, 2));
        rcv_img.setAdapter(adapter_image);

        adapter_image.setOnItemClickListener(new Adapter_Image.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position, String path) {
                if (Build.VERSION.SDK_INT < 24) {
                    try {
                        if (path.length() > 0) {
                            Uri uPath = Uri.parse(path);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uPath);
                            intent.setDataAndType(uPath, "image/*");
                            startActivity(intent);
                        }
                    } catch (ActivityNotFoundException e) {
                    }
                } else {
                    if (path.length() > 0) {
                        Uri uPath = Uri.parse(path);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uPath);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uPath, "image/*");
                        startActivity(intent);
                    }
                }
            }
        });
        adapter_image.setOnLongItemClickListener(new Adapter_Image.OnLongItemClickListener() {

            @Override
            public void onItemClick(View itemView, final int position, final String pathl) {
                final Dialog dialog = new Dialog(ContentNote.this, R.style.mydialogstyle);
                dialog.setContentView(R.layout.dialog_delete);

                TextView title_delete = dialog.findViewById(R.id.title_delete);
                title_delete.setText(R.string.do_you_want_delete_img);

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
                        lstImg.remove(position);
                        adapter_image.delete(position);

                        File file = new File(pathl);
                        file.delete();


                        if (editer == 1) {
                            //Chua luu trong database
                        } else {
                            if (lstImg.size() > 1) {
                                pathImgSaved = lstImg.get(0);
                                for (int i = 1; i < lstRecord.size(); i++) {
                                    pathImgSaved += "-----" + lstImg.get(i);
                                }
                            } else {
                                if (lstImg.size() == 1) {
                                    pathImgSaved = lstImg.get(0);
                                } else {
                                    pathImgSaved = "";
                                }
                            }
                            dbManager.DeleteOneImg(idkey, pathImgSaved);
                        }

                        pathImg = "";
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        adapterRecord.setOnLongItemClickListener(new Adapter_Record.OnLongItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position, final String path) {

                final Dialog dialog6 = new Dialog(ContentNote.this, R.style.mydialogstyle);
                dialog6.setContentView(R.layout.dialog_delete);

                TextView title_delete = dialog6.findViewById(R.id.title_delete);
                title_delete.setText(R.string.deleteRecord);

                Button btn_back2 = dialog6.findViewById(R.id.btn_delete_back);
                Button btn_ok2 = dialog6.findViewById(R.id.btn_delete_ok);

                btn_back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog6.dismiss();
                    }
                });

                btn_ok2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbManager = new DBManager(ContentNote.this);

                        File dfile = new File(path);
                        dfile.delete();


                        if (editer == 1) {
                            //Chua luu trong database
                            lstRecord.remove(position);
                            adapterRecord.delete(position);
                        } else {
                            lstRecord.remove(position);
                            adapterRecord.delete(position);
                            if (lstRecord.size() > 1) {
                                pathSaved = lstRecord.get(0);
                                for (int i = 1; i < lstRecord.size(); i++) {
                                    pathSaved += "-----" + lstRecord.get(i);
                                }
                            } else {
                                if (lstRecord.size() == 1) {
                                    pathSaved = lstRecord.get(0);
                                } else {
                                    pathSaved = "";
                                }
                            }
                            dbManager.DeleteOneRecord(idkey, pathSaved);

                        }

                        pathSave = "";
                        dialog6.dismiss();
                    }
                });
                dialog6.show();
            }
        });


        ct_recorder();


        btn_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchDateTimeDialogFragment dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Date Time?",
                        "OK",
                        "Cancel"
                );
                try {
                    dateTimeFragment.setSimpleDateMonthAndDayFormat(sdf);
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                    e.printStackTrace();
                }
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.set24HoursMode(true);
                dateTimeFragment.setMinimumDateTime(calendar.getTime());
                dateTimeFragment.setMaximumDateTime(new GregorianCalendar(VietNote.MAX_YEAR_CALENDAR, Calendar.DECEMBER, 31).getTime());
                dateTimeFragment.setDefaultDateTime(calendar.getTime());
                try {
                    dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                }

                dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        String[] datetime = date.toString().split(" ");
                        changeDateTime(datetime);
                        String tdate = datetime[2] + "/" + datetime[1] + "/" + datetime[5];
                        String ttime = datetime[3];
                        cn_datetime.setText(tdate + " " + ttime);
                    }
                    @Override
                    public void onNegativeButtonClick(Date date) {
                    }
                });
                dateTimeFragment.show(getSupportFragmentManager(), "Dialog");
            }
        });
    }

    private void ct_recorder() {
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
        btn_start_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                btn_start_record.setVisibility(View.GONE);
                btn_end_record.setVisibility(View.VISIBLE);
                Toast.makeText(ContentNote.this, getResources().getString(R.string.start_record), Toast.LENGTH_SHORT).show();
            }
        });
        btn_end_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                ll_record1.setVisibility(View.GONE);
                lstRecord.add(pathSave);
                newRecord.add(pathSave);
                adapterRecord.add(pathSave);
                rcv_record.setVisibility(View.VISIBLE);
                Toast.makeText(ContentNote.this, getResources().getString(R.string.end_record), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupMediaRecorder() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/"+VietNote.KEY_FOLDER+"/";
        File newdir = new File(dir);
        if (!newdir.exists()) {
            newdir.mkdirs();
        }
        String file = dir + android.text.format.DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + "_audio_record.aac";
        File newfileMp3 = new File(file);
        try {
            newfileMp3.createNewFile();
        } catch (IOException e) {
        }

        pathSave = file;

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void findbyId() {
        rcv_record = findViewById(R.id.rcv_item);
        rcv_img = findViewById(R.id.rcv_image);
        cn_content = findViewById(R.id.cn_content);
        cn_datetime = findViewById(R.id.cn_datetime);

        String[] datetime = calendar.getTime().toString().split(" ");
        changeDateTime(datetime);
        String tdate = datetime[2] + "/" + datetime[1] + "/" + datetime[5];
        String ttime = datetime[3];
        cn_datetime.setText(tdate + " " + ttime);


        cn_tittle = findViewById(R.id.cn_tittle);
        btn_date_time = findViewById(R.id.btn_date_time);


        btn_start_record = findViewById(R.id.btn_start_record);
        btn_end_record = findViewById(R.id.btn_end_record);


        ll_record1 = findViewById(R.id.ll_record1);


        ll_record1.setVisibility(View.GONE);


    }

    private void takeaphoto() { //Lấy từ gellary
        if (checkPermission(ContentNote.this)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        } else {
            ActivityCompat.requestPermissions(ContentNote.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, VietNote.KEY_PERMISSIONS);
        }
    }

    private void camera() {
        capturarFoto();
    }

    private void capturarFoto() {
        if (Build.VERSION.SDK_INT < 24) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"+VietNote.KEY_FOLDER+"/";
            File newdir = new File(dir);
            if (!newdir.exists()) {
                newdir.mkdirs();
            }
            String file = dir + android.text.format.DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            } catch (IOException e) {

            }

            Uri outputFileUri = Uri.fromFile(newfile);
            filePath = outputFileUri;
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(this);
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    filePath = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                    startActivityForResult(takePictureIntent, 0);
                }
            }
        }
    }

    public File createImageFile(Context context) throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "NoteHDP", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //Tu may
            if (data != null) {
                Uri selectedImage = data.getData();
                String path = selectedImage.toString();
                lstImg.add(path);
                lstNewImg.add(path);
                adapter_image.add(path);
                rcv_img.setVisibility(View.VISIBLE);
            }
        }
        if (requestCode == 0) { //Tu camera
            String path = filePath.toString();
            lstImg.add(path);
            lstNewImg.add(path);
            adapter_image.add(path);
            rcv_img.setVisibility(View.VISIBLE);
        }
    }

    void changeDateTime(String[] datetime) {
        switch (datetime[1]) {
            case "Jan":
                datetime[1] = "1";
                break;
            case "Feb":
                datetime[1] = "2";
                break;
            case "Mar":
                datetime[1] = "3";
                break;
            case "Apr":
                datetime[1] = "4";
                break;
            case "May":
                datetime[1] = "5";
                break;
            case "Jun":
                datetime[1] = "6";
                break;
            case "Jul":
                datetime[1] = "7";
                break;
            case "Aug":
                datetime[1] = "8";
                break;
            case "Sep":
                datetime[1] = "9";
                break;
            case "Oct":
                datetime[1] = "10";
                break;
            case "Nov":
                datetime[1] = "11";
                break;
            case "Dec":
                datetime[1] = "12";
                break;
        }

    }

    public static boolean checkPermission(final Context context) //
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Ckeck Per", "1");
            } else {
                Log.e("Ckeck Per", "2");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VietNote.KEY_PERMISSIONS);
            }

        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case VietNote.KEY_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    checkper = true;
                } else {
                    // Permission Denied
                    Toast.makeText(ContentNote.this, getResources().getString(R.string.write_denied), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.m_delete:
                final Dialog dialog1 = new Dialog(ContentNote.this, R.style.mydialogstyle);
                dialog1.setContentView(R.layout.dialog_delete);

                Button btn_back = dialog1.findViewById(R.id.btn_delete_back);
                Button btn_ok = dialog1.findViewById(R.id.btn_delete_ok);

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBManager db = new DBManager(ContentNote.this);

                        if (editer == 1) {
                            if (lstRecord != null) {
                                for (int i = 0; i < lstRecord.size(); i++) {
                                    File dfile = new File(lstRecord.get(i));
                                    dfile.delete();
                                }
                            }
                            if (lstImg != null) {
                                for (int i = 0; i < lstImg.size(); i++) {
                                    File dfile = new File(lstImg.get(i));
                                    dfile.delete();
                                }
                            }
                            dialog1.dismiss();
                            finish();
                        } else {
                            db.deleteNote(idkey);
                            //Xoa file ghi am
                            if (lstRecord != null) {
                                for (int i = 0; i < lstRecord.size(); i++) {
                                    File dfile = new File(lstRecord.get(i));
                                    dfile.delete();
                                }
                            }
                            if (lstImg != null) {
                                for (int i = 0; i < lstImg.size(); i++) {
                                    File dfile = new File(lstImg.get(i));
                                    dfile.delete();
                                }
                            }


                            dialog1.dismiss();

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(VietNote.RESULT_DELETE,pos);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();

                        }
                    }
                });
                dialog1.show();


                break;
            case R.id.m_photo:
                if(lstImg.size()>=6){
                    final Dialog dialog = new Dialog(ContentNote.this, R.style.mydialogstyle);
                    dialog.setContentView(R.layout.dialog_uri);

                    Button btnBack=dialog.findViewById(R.id.btn_b);
                    Button btnOK=dialog.findViewById(R.id.btn_o);

                    btnBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }else {
                    final Dialog dialog = new Dialog(ContentNote.this, R.style.mydialogstyle);
                    dialog.setContentView(R.layout.dialog_choose_img);

                    ImageButton btn_cam = dialog.findViewById(R.id.btn_cam);
                    ImageButton btn_gallery = dialog.findViewById(R.id.btn_gallery);

                    btn_cam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            camera();
                            dialog.dismiss();
                        }
                    });
                    btn_gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            takeaphoto();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }




                break;
            case R.id.m_save:
                final Dialog dialog3 = new Dialog(ContentNote.this, R.style.mydialogstyle);
                dialog3.setContentView(R.layout.dialog_save);


                final EditText edt_save_pass = dialog3.findViewById(R.id.edt_save_pass);
                Button btn_back1 = dialog3.findViewById(R.id.btn_save_back);
                Button btn_save = dialog3.findViewById(R.id.btn_save_ok);

                btn_back1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog3.dismiss();
                    }
                });

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        note.setNtitlle(cn_tittle.getText().toString());
                        if (cn_tittle.getText().toString().length() < 1) {
                            note.setNtitlle(getResources().getString(R.string.no_title));
                        }

                        note.setNcontent(cn_content.getText().toString());

                        String[] datetime = cn_datetime.getText().toString().split(" ");

                        note.setNdate(datetime[0]);
                        note.setNtime(datetime[1]);
                        if (edt_save_pass.getText().toString().length() > 0) {
                            note.setNpass(edt_save_pass.getText().toString());
                            note.setLocked(1);
                        } else {
                            note.setNpass("");
                            note.setLocked(0);
                        }
                        try {
                            if (lstImg.size() > 1) {
                                pathImgSaved = lstImg.get(0);
                                for (int i = 1; i < lstImg.size(); i++) {
                                    pathImgSaved += "-----" + lstImg.get(i);
                                    note.setNImgName(pathImgSaved);
                                }
                            } else {
                                if (lstImg.size() == 1) {
                                    note.setNImgName(lstImg.get(0));
                                } else {
                                    note.setNImgName("");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        note.setSaved(0);

                        if (lstRecord.size() > 1) {
                            pathSaved = lstRecord.get(0);
                            for (int i = 1; i < lstRecord.size(); i++) {
                                pathSaved += "-----" + lstRecord.get(i);
                                note.setRecord(pathSaved);
                            }
                        } else {
                            if (lstRecord.size() == 1) {
                                note.setRecord(lstRecord.get(0));
                            } else {
                                note.setRecord("");
                            }
                        }


                        if (cn_content.getText().toString().length() > 0) {
                            //Update or add with key
                            if (editer == 1) {
                                dbManager.Add_Note(note);
                                Note n= dbManager.GetLast();
                                note.setId(n.getId());
                                Intent returnIntent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(VietNote.KEY_NOTED, note);
                                returnIntent.putExtra(VietNote.KEY_RESULT_ADD,bundle);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            } else {
                                dbManager.UP_Note(note, idkey);

                                Intent returnIntent = new Intent();
                                note.setId(idkey);
                                Bundle bundle = new Bundle();
                                bundle.putInt(VietNote.POS_EDIT,pos);
                                bundle.putSerializable(VietNote.KEY_NOTED, note);
                                returnIntent.putExtra(VietNote.RESULT_EDIT,bundle);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }


                        } else {
                            Toast.makeText(ContentNote.this, getResources().getString(R.string.contentisempty), Toast.LENGTH_LONG).show();
                            dialog3.dismiss();
                        }
                    }
                });
                dialog3.show();
                break;

            case R.id.menu_delete_img:

                if(lstImg!=null&&lstImg.size()>0){
                    final Dialog dialog5 = new Dialog(ContentNote.this, R.style.mydialogstyle);
                    dialog5.setContentView(R.layout.dialog_delete);

                    TextView title_delete = dialog5.findViewById(R.id.title_delete);
                    title_delete.setText(R.string.deleteAllImg);

                    Button btn_back2 = dialog5.findViewById(R.id.btn_delete_back);
                    Button btn_ok2 = dialog5.findViewById(R.id.btn_delete_ok);

                    btn_back2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog5.dismiss();
                        }
                    });
                    btn_ok2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //xoa tat
                            filePath = null;

                            dbManager = new DBManager(ContentNote.this);

                            if (lstImg != null) {
                                for (int i = 0; i < lstImg.size(); i++) {
                                    File dfile = new File(lstImg.get(i));
                                    dfile.delete();
                                    adapter_image.delete(i);
                                }
                            }
                            if (editer == 1) {
                            } else {
                                dbManager.DeleteImage(idkey);
                            }
                            pathSave = "";
                            pathImgSaved = "";
                            lstNewImg.clear();
                            lstImg.clear();
                            dialog5.dismiss();
                        }
                    });

                    dialog5.show();
                }



                break;
            case R.id.menu_stick:
                final Dialog dialog4 = new Dialog(ContentNote.this, R.style.mydialogstyle);
                dialog4.setContentView(R.layout.dialog_save);

                final EditText edt_save_pass1 = dialog4.findViewById(R.id.edt_save_pass);
                Button btn_back11 = dialog4.findViewById(R.id.btn_save_back);
                Button btn_save1 = dialog4.findViewById(R.id.btn_save_ok);

                btn_back11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog4.dismiss();
                    }
                });

                btn_save1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note = new Note();
                        note.setNtitlle(cn_tittle.getText().toString());
                        if (cn_tittle.getText().toString().length() < 1) {
                            note.setNtitlle(getResources().getString(R.string.no_title));
                        }

                        note.setNcontent(cn_content.getText().toString());

                        String[] datetime = cn_datetime.getText().toString().split(" ");

                        note.setNdate(datetime[0]);
                        note.setNtime(datetime[1]);
                        if (edt_save_pass1.getText().toString().length() > 0) {
                            note.setNpass(edt_save_pass1.getText().toString());
                            note.setLocked(1);
                        } else {
                            note.setNpass("");
                            note.setLocked(0);
                        }
                        try {
                            if (lstImg.size() > 1) {
                                pathImgSaved = lstImg.get(0);
                                for (int i = 1; i < lstImg.size(); i++) {
                                    pathImgSaved += "-----" + lstImg.get(i);
                                    note.setNImgName(pathImgSaved);
                                }
                            } else {
                                if (lstImg.size() == 1) {
                                    note.setNImgName(lstImg.get(0));
                                } else {
                                    note.setNImgName("");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        note.setSaved(1);
                        if (lstRecord.size() > 1) {
                            pathSaved = lstRecord.get(0);
                            for (int i = 1; i < lstRecord.size(); i++) {
                                pathSaved += "-----" + lstRecord.get(i);
                                note.setRecord(pathSaved);
                            }
                        } else {
                            if (lstRecord.size() == 1) {
                                note.setRecord(lstRecord.get(0));
                            } else {
                                note.setRecord("");
                            }
                        }
                        if (cn_content.getText().toString().length() > 0) {
                            //Update or add with key
                            if (editer == 1) {
                                dbManager.Add_Note(note);

                                Note n= dbManager.GetLast();
                                note.setId(n.getId());

                                Intent returnIntent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(VietNote.KEY_NOTED, note);
                                returnIntent.putExtra(VietNote.KEY_RESULT_ADD,bundle);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            } else {

                                dbManager.UP_Note(note, idkey);

                                Intent returnIntent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putInt(VietNote.POS_EDIT,pos);
                                note.setId(idkey);
                                bundle.putSerializable(VietNote.KEY_NOTED, note);
                                returnIntent.putExtra(VietNote.RESULT_EDIT,bundle);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(ContentNote.this, getResources().getString(R.string.contentisempty), Toast.LENGTH_LONG).show();
                            dialog4.dismiss();
                        }
                    }
                });
                dialog4.show();
                break;


            case R.id.menu_share_image:
                if (lstImg != null) {
                    if (lstImg.size() > 0) {
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        Intent intentShare = new Intent();
                        intentShare.setAction(Intent.ACTION_SEND_MULTIPLE);
                        for (int i = 0; i < lstImg.size(); i++) {
                            Uri uPath = Uri.parse(lstImg.get(i).toString());
                            uris.add(uPath);
                        }
                        intentShare.putExtra(Intent.EXTRA_STREAM, uris);
                        intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intentShare.setType("image/*");
                        startActivity(Intent.createChooser(intentShare, getResources().getString(R.string.share_img)));
                    }
                }
                break;
            case R.id.menu_record:
                if(lstRecord.size()>=6){
                    final Dialog dialog = new Dialog(ContentNote.this, R.style.mydialogstyle);
                    dialog.setContentView(R.layout.dialog_uri);
                    TextView txtTitle=dialog.findViewById(R.id.title_many);
                    TextView txt=dialog.findViewById(R.id.pls);

                    txtTitle.setText(R.string.manyRcd);
                    txt.setText(R.string.pls_record);

                    Button btnBack=dialog.findViewById(R.id.btn_b);
                    Button btnOK=dialog.findViewById(R.id.btn_o);

                    btnBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }else {
                    if (checkPermission(ContentNote.this)) {
                        ll_record1.setVisibility(View.VISIBLE);
                        btn_start_record.setVisibility(View.VISIBLE);
                        btn_end_record.setVisibility(View.GONE);
                    } else {
                        ActivityCompat.requestPermissions( ContentNote.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,}, VietNote.KEY_PERMISSIONS);
                    }
                }

                break;
            case R.id.menu_record_delete:
                if (lstRecord.size() > 0) {
                    final Dialog dialog6 = new Dialog(ContentNote.this, R.style.mydialogstyle);
                    dialog6.setContentView(R.layout.dialog_delete);

                    TextView title_delete1 = dialog6.findViewById(R.id.title_delete);
                    title_delete1.setText(R.string.deleteAllRecord);

                    Button btn_back21 = dialog6.findViewById(R.id.btn_delete_back);
                    Button btn_ok21 = dialog6.findViewById(R.id.btn_delete_ok);

                    btn_back21.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog6.dismiss();
                        }
                    });

                    btn_ok21.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbManager = new DBManager(ContentNote.this);

                            if (lstRecord != null) {
                                for (int i = 0; i < lstRecord.size(); i++) {
                                    File dfile = new File(lstRecord.get(i));
                                    dfile.delete();
                                    adapterRecord.delete(i);
                                }
                            }


                            if (editer == 1) {
                                //Chua luu trong database
                            } else {
                                dbManager.DeleteRecord(idkey);

                            }

                            pathSave = "";
                            lstRecord.clear();
                            newRecord.clear();

                            dialog6.dismiss();
                        }
                    });
                    dialog6.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        mediaPlayer.stop();
        final Dialog dialog4 = new Dialog(ContentNote.this, R.style.mydialogstyle);
        dialog4.setContentView(R.layout.dialog_save);

        final EditText edt_save_pass1 = dialog4.findViewById(R.id.edt_save_pass);
        Button btn_back11 = dialog4.findViewById(R.id.btn_save_back);
        Button btn_save1 = dialog4.findViewById(R.id.btn_save_ok);
        TextView txtSaveTitle =dialog4.findViewById(R.id.txtSaveTitle);

        txtSaveTitle.setText(R.string.savechanged);
        btn_back11.setText(R.string.No);
        btn_save1.setText(R.string.Yes);

        btn_back11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newRecord != null) {
                    for (int i = 0; i < newRecord.size(); i++) {
                        File dfile = new File(newRecord.get(0));
                        dfile.delete();
                    }
                }
                if (lstNewImg != null) {
                    for (int i = 0; i < lstNewImg.size(); i++) {
                        File dfile = new File(lstNewImg.get(0));
                        dfile.delete();
                    }
                }

                dialog4.dismiss();
                finish();
            }
        });

        btn_save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note();
                note.setNtitlle(cn_tittle.getText().toString());
                if (cn_tittle.getText().toString().length() < 1) {
                    note.setNtitlle(getResources().getString(R.string.no_title));
                }

                note.setNcontent(cn_content.getText().toString());

                String[] datetime = cn_datetime.getText().toString().split(" ");

                note.setNdate(datetime[0]);
                note.setNtime(datetime[1]);
                if (edt_save_pass1.getText().toString().length() > 0) {
                    note.setNpass(edt_save_pass1.getText().toString());
                    note.setLocked(1);
                } else {
                    note.setNpass("");
                    note.setLocked(0);
                }
                try {
                    if (lstImg.size() > 1) {
                        pathImgSaved = lstImg.get(0);
                        for (int i = 1; i < lstImg.size(); i++) {
                            pathImgSaved += "-----" + lstImg.get(i);
                            note.setNImgName(pathImgSaved);
                        }
                    } else {
                        if (lstImg.size() == 1) {
                            note.setNImgName(lstImg.get(0));
                        } else {
                            note.setNImgName("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                note.setSaved(0);
                if (lstRecord.size() > 1) {
                    pathSaved = lstRecord.get(0);
                    for (int i = 1; i < lstRecord.size(); i++) {
                        pathSaved += "-----" + lstRecord.get(i);
                        note.setRecord(pathSaved);
                    }
                } else {
                    if (lstRecord.size() == 1) {
                        note.setRecord(lstRecord.get(0));
                    } else {
                        note.setRecord("");
                    }
                }
                if (cn_content.getText().toString().length() > 0) {
                    //Update or add with key
                    if (editer == 1) {
                        dbManager.Add_Note(note);
                        Note n= dbManager.GetLast();
                        note.setId(n.getId());
                        Intent returnIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(VietNote.KEY_NOTED, note);
                        returnIntent.putExtra(VietNote.KEY_RESULT_ADD,bundle);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    } else {
                        note.setSaved(sav);
                        dbManager.UP_Note(note, idkey);
                        note.setId(idkey);
                        Intent returnIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt(VietNote.POS_EDIT,pos);
                        bundle.putSerializable(VietNote.KEY_NOTED, note);
                        returnIntent.putExtra(VietNote.RESULT_EDIT,bundle);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }

                } else {
                    Toast.makeText(ContentNote.this, getResources().getString(R.string.contentisempty), Toast.LENGTH_LONG).show();
                    dialog4.dismiss();
                }
            }
        });
        dialog4.show();

        return super.getSupportParentActivityIntent();
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();

        final Dialog dialog4 = new Dialog(ContentNote.this, R.style.mydialogstyle);
        dialog4.setContentView(R.layout.dialog_save);


        final EditText edt_save_pass1 = dialog4.findViewById(R.id.edt_save_pass);
        Button btn_back11 = dialog4.findViewById(R.id.btn_save_back);
        Button btn_save1 = dialog4.findViewById(R.id.btn_save_ok);

        btn_back11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newRecord != null) {
                    for (int i = 0; i < newRecord.size(); i++) {
                        File dfile = new File(newRecord.get(0));
                        dfile.delete();
                    }
                }
                if (lstNewImg != null) {
                    for (int i = 0; i < lstNewImg.size(); i++) {
                        File dfile = new File(lstNewImg.get(0));
                        dfile.delete();
                    }
                }
                dialog4.dismiss();
                finish();
            }
        });

        btn_save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note();
                note.setNtitlle(cn_tittle.getText().toString());
                if (cn_tittle.getText().toString().length() < 1) {
                    note.setNtitlle(getResources().getString(R.string.no_title));
                }

                note.setNcontent(cn_content.getText().toString());

                String[] datetime = cn_datetime.getText().toString().split(" ");

                note.setNdate(datetime[0]);
                note.setNtime(datetime[1]);
                if (edt_save_pass1.getText().toString().length() > 0) {
                    note.setNpass(edt_save_pass1.getText().toString());
                    note.setLocked(1);
                } else {
                    note.setNpass("");
                    note.setLocked(0);
                }
                try {
                    if (lstImg.size() > 1) {
                        pathImgSaved = lstImg.get(0);
                        for (int i = 1; i < lstImg.size(); i++) {
                            pathImgSaved += "-----" + lstImg.get(i);
                            note.setNImgName(pathImgSaved);
                        }
                    } else {
                        if (lstImg.size() == 1) {
                            note.setNImgName(lstImg.get(0));
                        } else {
                            note.setNImgName("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                note.setSaved(0);
                if (lstRecord.size() > 1) {
                    pathSaved = lstRecord.get(0);
                    for (int i = 1; i < lstRecord.size(); i++) {
                        pathSaved += "-----" + lstRecord.get(i);
                        note.setRecord(pathSaved);
                    }
                } else {
                    if (lstRecord.size() == 1) {
                        note.setRecord(lstRecord.get(0));
                    } else {
                        note.setRecord("");
                    }
                }
                if (cn_content.getText().toString().length() > 0) {
                    //Update or add with key
                    if (editer == 1) {
                        dbManager.Add_Note(note);

                        Note n= dbManager.GetLast();
                        note.setId(n.getId());

                        Intent returnIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(VietNote.KEY_NOTED, note);
                        returnIntent.putExtra(VietNote.KEY_RESULT_ADD,bundle);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    } else {
                        Log.e("Id", note.getId() + "");
                        Log.e("Key", idkey + "");
                        note.setSaved(sav);
                        dbManager.UP_Note(note, idkey);
                        Intent returnIntent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putInt(VietNote.POS_EDIT,pos);
                        note.setId(idkey);
                        bundle.putSerializable(VietNote.KEY_NOTED, note);
                        returnIntent.putExtra(VietNote.RESULT_EDIT,bundle);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                } else {
                    Toast.makeText(ContentNote.this, getResources().getString(R.string.contentisempty), Toast.LENGTH_LONG).show();
                    dialog4.dismiss();
                }
            }
        });
        dialog4.show();
        return;
    }
}
