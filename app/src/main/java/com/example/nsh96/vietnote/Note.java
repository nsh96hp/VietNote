package com.example.nsh96.vietnote;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Note implements Serializable, Comparable<Note> {
    private int id,locked,saved; //==1 co khoa, ==0 ko co khoa
    private String Npass, Ntitlle, Ndate, Ntime,Ncontent;
    private String NImgName, Record;
    public Note(int Id, String ntitlle, String npass, int Locked, String ndate, String ntime, String ncontent, String nImgName,int Saved,String record) {
        id = Id;
        locked = Locked;
        Npass = npass;
        Ntitlle = ntitlle;
        Ndate = ndate;
        Ntime = ntime;
        Ncontent = ncontent;
        NImgName = nImgName;
        saved=Saved;
        Record= record;
    }


    public Note(int locked, int saved, String npass, String ntitlle, String ndate, String ntime, String ncontent, String NImgName, String record) {
        this.locked = locked;
        this.saved = saved;
        Npass = npass;
        Ntitlle = ntitlle;
        Ndate = ndate;
        Ntime = ntime;
        Ncontent = ncontent;
        this.NImgName = NImgName;
        Record = record;
    }

    public Note() {
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", locked=" + locked +
                ", saved=" + saved +
                ", Npass='" + Npass + '\'' +
                ", Ntitlle='" + Ntitlle + '\'' +
                ", Ndate='" + Ndate + '\'' +
                ", Ntime='" + Ntime + '\'' +
                ", Ncontent='" + Ncontent + '\'' +
                ", NImgName='" + NImgName + '\'' +
                ", Record='" + Record + '\'' +
                '}';
    }

    public String getRecord() {
        return Record;
    }

    public void setRecord(String record) {
        Record = record;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNImgName() {
        return NImgName;
    }

    public void setNImgName(String NImgName) {
        this.NImgName = NImgName;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getNpass() {
        return Npass;
    }

    public void setNpass(String npass) {
        Npass = npass;
    }

    public String getNtitlle() {
        return Ntitlle;
    }

    public void setNtitlle(String ntitlle) {
        Ntitlle = ntitlle;
    }

    public String getNdate() {
        return Ndate;
    }

    public void setNdate(String ndate) {
        Ndate = ndate;
    }

    public String getNtime() {
        return Ntime;
    }

    public void setNtime(String ntime) {
        Ntime = ntime;
    }

    public String getNcontent() {
        return Ncontent;
    }

    public void setNcontent(String ncontent) {
        Ncontent = ncontent;
    }

    @Override
    public int compareTo(@NonNull Note o) {//Up Created
        return this.getId()-o.getId();
    }

    public static Comparator<Note> SortDownCreated= new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o2.getId()-o1.getId();
        }
    };

    public static Comparator<Note> SortUpTitle= new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o2.getNtitlle().compareTo(o1.getNtitlle());
        }
    };
    public static Comparator<Note> SortDownTitle= new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return o1.getNtitlle().compareTo(o2.getNtitlle());
        }
    };

    public static Comparator<Note> SortUpAlarm= new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return sosanhngay(o2.getNdate(),o1.getNdate(),o2.getNtime(),o1.getNtime());
        }
    };
    public static Comparator<Note> SortDownAlarm= new Comparator<Note>() {
        @Override
        public int compare(Note o1, Note o2) {
            return -1*sosanhngay(o2.getNdate(),o1.getNdate(),o2.getNtime(),o1.getNtime());
        }
    };

    public static int sosanhngay(String date1,String date2,String time1,String time2) {

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String[] d1=date1.split("/");
        date1=d1[1]+"/"+d1[0]+"/"+d1[2];
        String[] d2=date2.split("/");
        date2=d2[1]+"/"+d2[0]+"/"+d2[2];
        Date dd1=null;
        Date dd2=null;
        try {
            dd1=format.parse(date1+" "+time1);
            dd2=format.parse(date2+" "+time2);

            long diff = dd2.getTime() - dd1.getTime();


           return (int) diff;

        }catch (Exception e){

        }

        return 0;

    }

}
