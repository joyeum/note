package com.example.note;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class NoteData implements Serializable {
    // private int noteImage;
    private int noteId;
    private LocalDateTime noteDate;
    private String noteTitle;
    private String noteTime;
    private String noteContent;

    public NoteData(int noteId){
        this.noteId = noteId;
    }

    public NoteData(int noteId, String noteTitle, String noteTime, String noteContent) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteDate = noteDate;
        this.noteContent = noteContent;
    }

    public LocalDateTime getNoteDate(){return noteDate;}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setNoteDate(LocalDateTime noteDate) {
        this.noteDate = noteDate;
        String noteTime ;

        LocalDateTime nowDate = LocalDateTime.now();

        if(nowDate.isAfter(noteDate)){
            Period period = noteDate.toLocalDate().until(nowDate.toLocalDate());
            if (period.getYears() >= 1){
                noteTime = Integer.toString(period.getYears()) + "년 전";
            }
            else if (period.getMonths() >= 2) {
                noteTime = Integer.toString(period.getMonths()) + "달 전";
            }
            else {
                int hours = (int) noteDate.until( nowDate, ChronoUnit.HOURS);
                //long hours = ChronoUnit.HOURS.between(nowDate.toLocalDate(),noteDate.toLocalDate());
                //if (hours >= 168) {
                if (hours >= 336){
                    int weeks = (int) noteDate.until( nowDate, ChronoUnit.WEEKS);
                    noteTime = Integer.toString(weeks) + "주 전";

                }
                else if (hours >= 24){
                    noteTime = Integer.toString(period.getDays()) + "일 전";
                }
                else if (hours >= 1){
                    noteTime = Integer.toString(hours) + "시간 전";
                }
                else {
                    int mins = (int) noteDate.until( nowDate, ChronoUnit.MINUTES);
                    if (mins >= 1){ noteTime = Integer.toString(mins) + "분 전"; }
                    else {noteTime = "지금";}
                }
            }



        }
        else{
            noteTime = "지금";
        }


        long betweenDates= nowDate.toLocalDate()
                .toEpochDay() - noteDate.toLocalDate().toEpochDay();

        this.setNoteTime(noteTime);

    }

    public int getNoteId(){return noteId;}

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteTime() {
        return noteTime;
    }

    public void setNoteTime(String noteTime) {
        this.noteTime = noteTime;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}
