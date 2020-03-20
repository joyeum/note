package com.example.note;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.valueOf;


public class WriteNoteActivity extends AppCompatActivity {
    private EditText noteText, noteSourceWriter, noteSourceName, noteTitle;

    FileOutputStream fos;


    File filesLocate, notesLocate;

    SharedPreferences sharedPref;
    String lastCodeSPName;
    Button detailButton;
    boolean isNoteDetailed;

    public static Intent createIntent(Context context) {
        return new Intent(context, WriteNoteActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        noteText = (EditText) findViewById(R.id.noteContent);

        noteTitle = (EditText) findViewById(R.id.noteTitle);
        noteSourceName = (EditText) findViewById(R.id.noteSourceName);
        noteSourceWriter = (EditText) findViewById(R.id.noteSourceWriter);

        //filesLocate = new File(getExternalFilesDir(null).getAbsolutePath());
        //외부저장소 위치
        filesLocate = this.getFilesDir();
        notesLocate = new File(filesLocate.getAbsolutePath()+"/notes");

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        lastCodeSPName = "lastCode";
        isNoteDetailed = false;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ///toolbar.setNavigationIcon(R.drawable.ic_back_black_24dp);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endWriting();
            }
        });*/


        // Spinner
        Spinner sourceSpinner = (Spinner)findViewById(R.id.noteSourceType);
        ArrayAdapter sourceAdapter = ArrayAdapter.createFromResource(this,
                R.array.note_sources, android.R.layout.simple_spinner_item);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(sourceAdapter);
        Button detailButton = (Button)findViewById(R.id.detailButton);
        detailButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNoteDetailed = !isNoteDetailed;
                applyDetailed();
            }
        });
        applyDetailed();


    }
    private void applyDetailed(){
        noteTitle.setVisibility(noteTitle.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        noteSourceName.setVisibility(noteSourceName.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        noteSourceWriter.setVisibility(noteSourceWriter.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        ///detailButton.setText("항목 닫기");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.writenote_menu, menu);
        return true;

    }
    private void saveNoteXML(){

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_save) {
            String noteTextData = noteText.getText().toString();
            int fileCode = makeNewCode();
            String fileName = "note" + valueOf(fileCode) +".txt";
            saveNoteTXT(fileName, noteTextData);
            System.out.println("화면꺼져라!");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void cancelWriting(){
        System.out.println("화면꺼져라!");
 //       Intent intent = new Intent(this, MainActivity.class);
   //     startActivity(intent);
        finish();
    }
    private int makeNewCode(){
        int newCode = loadLastCode() + 1;
        saveLastCode(newCode);
        return newCode ;
    }
    private void saveLastCode(int code){

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(lastCodeSPName, code);
        editor.commit();
    }
    private int loadLastCode(){
        int defaultValue = 0;
        int code = sharedPref.getInt(lastCodeSPName, defaultValue);

        return code;
    }



    public void saveNoteTXT(String fileName, String fileData) {

        System.out.println(fileData);
///        OutputStreamWriter osw = new OutputStreamWriter(fw, "UTF-8");

        try {
            File dir = new File(notesLocate, fileName);

            fos = new FileOutputStream(dir);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos,"UTF-8"));
            writer.write(fileData);
            System.out.println("save to"+ dir);

            writer.flush();

            writer.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
            System.out.println("fail!");
        }

    }

}
