package com.jstechnologies.paperback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jstechnologies.paperback.Adapters.NotesAdapter;
import com.jstechnologies.paperback.Database.NoteDataBase;
import com.jstechnologies.paperback.Models.Note;
import com.jstechnologies.paperback.ui.CreateNoteActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_CODE_ADDNOTE=1;
    public static int REQUES_CODE_EDITNOTE=2;
    ImageView add,addCamera,addImage,addWebLink;
    FloatingActionButton fabAdd;
    RecyclerView recyclerView;
    NotesAdapter adapter;
    EditText search;
    List<Note>models= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitViews();
        LoadNotes();
    }

    //Initializing View components
    public void InitViews()
    {

        add=findViewById(R.id.add);
        addCamera=findViewById(R.id.add_camera);
        addImage=findViewById(R.id.add_image);
        addWebLink=findViewById(R.id.add_weblink);
        fabAdd=findViewById(R.id.fab_addnote);
        search=findViewById(R.id.search_text);
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        adapter=new NotesAdapter(getApplicationContext(),models);
        adapter.setItemClicklistener(itemClickListener);
        recyclerView.setAdapter(adapter);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,CreateNoteActivity.class),REQUEST_CODE_ADDNOTE);
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,CreateNoteActivity.class),REQUEST_CODE_ADDNOTE);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


    }
    void filter(String text){
        List<Note> temp = new ArrayList();
        for(Note d: models){
            if(d.getTitle().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }
    public void LoadNotes()
    {
        NoteDataBase.getDatabase(this).GetAllNotes(new NoteDataBase.DataBaseFetchOperationListener() {
            @Override
            public void onSuccess(List<Note> notes) {
                if(notes!=null && notes.size()>0)
                {
                    models.clear();
                    models.addAll(notes);
                    Toast.makeText(getApplicationContext(),notes.size()+" notes found",Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getApplicationContext(),"No Saved notes found...",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String reason) {

            }
        });
    }
    NotesAdapter.ItemClickListener itemClickListener= new NotesAdapter.ItemClickListener() {
        @Override
        public void onClick(Note note,int position) {
        Intent intent= new Intent(MainActivity.this,CreateNoteActivity.class);
        intent.putExtra("note",note);
        intent.putExtra("POSITION",position);
        startActivityForResult(intent,REQUES_CODE_EDITNOTE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUES_CODE_EDITNOTE && resultCode==RESULT_OK && data!=null)
        {

            NoteDataBase.getDatabase(this).GetAllNotes(new NoteDataBase.DataBaseFetchOperationListener() {
                @Override
                public void onSuccess(List<Note> notes) {
                    int pos=data.getIntExtra("POSITION",-1);
                  if(pos>-1) {
                      models.remove(pos);
                      models.add(pos,notes.get(pos));
                      adapter.notifyItemChanged(pos);
                      recyclerView.smoothScrollToPosition(pos);
                  }
                }

                @Override
                public void onFailure(String reason) {

                }
            });

        }
        if(requestCode==REQUEST_CODE_ADDNOTE && resultCode==RESULT_OK)
        {

            NoteDataBase.getDatabase(this).GetAllNotes(new NoteDataBase.DataBaseFetchOperationListener() {
                @Override
                public void onSuccess(List<Note> notes) {
                    models.add(0,notes.get(0));
                    adapter.notifyItemInserted(0);
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(String reason) {

                }
            });

        }
    }


}
