package com.example.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;

public class ViewNoteActivity extends AppCompatActivity {
    TextView noteTitle, noteContent, noteTime;
    FrameLayout noteCanvas;
    NoteData noteData;
    File filesLocate, notesLocate;
    public static Intent createIntent(Context context) {
        return new Intent(context, ViewNoteActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        noteData = (NoteData)intent.getSerializableExtra("noteData");

        setContentView(R.layout.activity_view_note);

        filesLocate = this.getFilesDir();
        notesLocate = new File(filesLocate.getAbsolutePath() + "/notes");

        noteTitle = (TextView) findViewById(R.id.noteTitle);
        noteContent = (TextView) findViewById(R.id.noteContent);
        noteTime = (TextView) findViewById(R.id.noteTime);
        noteTitle.setText(noteData.getNoteTitle());
        noteContent.setText(noteData.getNoteContent());
        noteTime.setText(noteData.getNoteTime());

        FrameLayout noteCanvas = (FrameLayout)findViewById(R.id.noteImage);
        myView canvasView= new myView(noteCanvas.getContext());
        noteCanvas.addView(canvasView);

    }
    public class myView extends View {


        public myView(Context context) {

            super(context);


            // TODO Auto-generated constructor stub

        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //canvas.drawColor(Color.YELLOW);
            TextPaint pnt = new TextPaint();
            pnt.setTextSize(40);
            pnt.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
            pnt.setTextAlign(Paint.Align.CENTER);


            canvas.drawColor(-1);
            String text = noteData.getNoteContent();
            int x = canvas.getWidth() / 2;
            int y = canvas.getHeight() / 2;
            for (String line: text.split("\n")) {
                canvas.drawText(line, x, y, pnt);
                y += pnt.descent() - pnt.ascent();
            }
        }
    }
}
