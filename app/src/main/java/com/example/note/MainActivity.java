package com.example.note;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private ArrayList<NoteData> noteDataList;
    private NoteAdapter noteAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    File filesLocate, notesLocate;

    SharedPreferences sharedPref;
    String lastCodeSPName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.notesList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        noteDataList = new ArrayList<>();

        noteAdapter = new NoteAdapter(noteDataList);
        recyclerView.setAdapter(noteAdapter);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        lastCodeSPName = "lastCode";

        //filesLocate = new File(getExternalFilesDir(null).getAbsolutePath());
        //외부저장소 위치
        filesLocate = this.getFilesDir();
        notesLocate = new File(filesLocate.getAbsolutePath() + "/notes");


        if (!notesLocate.exists()) {
            notesLocate.mkdir();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        //    actionBar.setDisplayShowTitleEnabled(false); //이름지우는거
        System.out.println(notesLocate);

        updateNoteList();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("it's restarted");
        System.out.println(noteDataList.size());
//        recyclerView.setAdapter(noteAdapter);
//        noteAdapter.notifyDataSetChanged();




        if(isNewNoteSaved()){
            updateNoteList();
            noteAdapter.notifyDataSetChanged();


            ///updateNote(noteId)
        }
        else {}
        /*
        updateNoteListTime();
         */
    }
    private boolean isNewNoteSaved(){

        return (noteDataList.size()>notesLocate.list().length);
    }

    private boolean isNoteFile(String fileName) {
        return (fileName.substring(0, 4).equals("note") && fileName.contains(".txt"));
    }

    private int getNoteCode(String fileName) {
        return Integer.valueOf(fileName.substring(4, fileName.lastIndexOf(".")));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateNoteList() {
        File[] notes = notesLocate.listFiles();

        if (notesLocate.list().length > 0) {
            if (!sharedPref.contains(lastCodeSPName)) {
                ArrayList<Integer> notesCodeList = new ArrayList<>();
                for (int i = 0; i < notes.length; i++) {
                    String fileName = notes[i].getName();
                    if (isNoteFile(fileName)) {
                        addNote(notes[i]);
                        notesCodeList.add(getNoteCode(fileName));
                    }
                }
                System.out.println(notesCodeList);
                saveLastCode(Collections.max(notesCodeList));
            } else {
                for (int i = 0; i < notes.length; i++) {
                    String fileName = notes[i].getName();
                    if (isNoteFile(fileName)) {
                        addNote(notes[i]);
                    }
                }
            }
            noteAdapter.notifyDataSetChanged();
        } else {
            saveLastCode(0);
            System.out.println("저장된 노트가 없습니다");

        }


    }

    public void updateNote(int noteId) {
    }//노트하나를 수정했을 때 들어가는 코드


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addNote(File note) {
        NoteData noteData = new NoteData(getNoteCode(note.getName()));

        StringBuffer strBuffer = new StringBuffer();
        try {
            InputStream is = new FileInputStream(note);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            line = reader.readLine();
            noteData.setNoteTitle(line);
            strBuffer.append(line);
            while ((line = reader.readLine()) != null) {
                strBuffer.append("\n" + line);
            }
            System.out.println(strBuffer);
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        noteData.setNoteContent(strBuffer.toString());
        LocalDateTime lastModDate = LocalDateTime.ofInstant(new Date(note.lastModified()).toInstant(), ZoneId.systemDefault());
        noteData.setNoteDate(lastModDate);

//        noteData.setNoteTime(lastModDate.toString());

        noteDataList.add(noteData);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            Toast.makeText(getApplicationContext(), "메모작성하러갑시다", Toast.LENGTH_LONG).show();
            onWriteMemo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveLastCode(int code) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(lastCodeSPName, code);
        editor.commit();
    }

    public void onWriteMemo() {

        Intent intent = WriteNoteActivity.createIntent(MainActivity.this);

        startActivity(intent);

    }

    class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.CustomViewHolder> {

        private ArrayList<NoteData> noteDataList;

        public NoteAdapter(ArrayList<NoteData> noteDataList) {
            this.noteDataList = noteDataList;
        }

        @NonNull
        @Override
        public NoteAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, parent, false);
            NoteAdapter.CustomViewHolder holder = new NoteAdapter.CustomViewHolder(view);
            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull final NoteAdapter.CustomViewHolder holder, final int position) {
            holder.noteTitle.setText(noteDataList.get(position).getNoteTitle());
            holder.noteTime.setText(noteDataList.get(position).getNoteTime());
            holder.noteContent.setText(noteDataList.get(position).getNoteContent());

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteData noteData = (NoteData) noteDataList.get((Integer)holder.itemView.getTag());

                    System.out.println(holder.itemView.getTag());
                    Intent intent = ViewNoteActivity.createIntent(MainActivity.this);
                    intent.putExtra("noteData", noteData);
                    startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            holder.itemView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    deleteNote(holder.getAdapterPosition());
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return (null != noteDataList ? noteDataList.size() : 0);
        }

        public void deleteNote(int position) {
            try {
                noteDataList.remove(position);
                notifyItemRemoved(position);

            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }

        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            protected TextView noteTitle;
            protected TextView noteTime;
            protected TextView noteContent;
            protected int noteId;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                this.noteTitle = (TextView) itemView.findViewById(R.id.noteTitle);
                this.noteTime = (TextView) itemView.findViewById(R.id.noteTime);
                this.noteContent = (TextView) itemView.findViewById(R.id.noteContent);
            }
        }
    }
}
