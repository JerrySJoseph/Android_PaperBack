package com.jstechnologies.paperback.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jstechnologies.paperback.Database.NoteDataBase;
import com.jstechnologies.paperback.Models.Note;
import com.jstechnologies.paperback.R;
import com.jstechnologies.paperback.TimeHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.InputStream;
import java.sql.Time;

import static android.view.View.GONE;

public class CreateNoteActivity extends AppCompatActivity {

    TextView timestamp,link,saveBtn;
    EditText title,subtitle,text;
    LinearLayout bottomsheet_layout,note_layout;
    BottomSheetBehavior bottomSheetBehavior;
    long _timestampcreated=System.currentTimeMillis();
    int colorindex=2;
    String selectednoteColor="#fdbe3b";
    View subindicator;
    Bitmap selectedimage;
    String selectedimagePath;
    String selectedURL;
    AlertDialog dialogAddURL;
    Note model;
    int position;
    public static int PERMISSION_REQ_CODE=3;
    public static int GALLERY_REQ_CODE=4;
    private boolean isEditMode=false;
    boolean saveenabled=false;
    ImageView img1,img2,img3,img4,img5,noteImg,expand,saveimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        InitViews();
        model=(Note)getIntent().getSerializableExtra("note");
        position=getIntent().getIntExtra("POSITION",-1);
        if(model!=null && position>-1)
        {
            isEditMode=true;
            UpdateView(model);

        }

    }
    public void InitViews()
    {
        timestamp=findViewById(R.id.note_datetime);
        title=findViewById(R.id.note_title);
        subtitle=findViewById(R.id.note_subtitle);
        text=findViewById(R.id.note_text);
        bottomsheet_layout=findViewById(R.id.bottom_sheet_layout);
        bottomSheetBehavior=BottomSheetBehavior.from(bottomsheet_layout);
        timestamp.setText(TimeHelper.getTimeStringFromMillis(_timestampcreated));
        subindicator=findViewById(R.id.subcolor_indicator);
        link=findViewById(R.id.note_link);
        noteImg=findViewById(R.id.note_image);
        saveBtn=findViewById(R.id.save_btn);
        saveimg=findViewById(R.id.save_img);
        note_layout=findViewById(R.id.note_layout);
        img1=bottomsheet_layout.findViewById(R.id.imgcolor1);
        img2=bottomsheet_layout.findViewById(R.id.imgcolor2);
        img3=bottomsheet_layout.findViewById(R.id.imgcolor3);
        img4=bottomsheet_layout.findViewById(R.id.imgcolor4);
        img5=bottomsheet_layout.findViewById(R.id.imgcolor5);
        expand=bottomsheet_layout.findViewById(R.id.expand);
        findViewById(R.id.arrow_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate())
                {

                    Note model1= new Note();

                    model1.setTitle(title.getText().toString().trim().isEmpty()?"Unnamed file":title.getText().toString().trim());
                    model1.setSubtitle(subtitle.getText().toString().trim());
                    model1.setNotetext(text.getText().toString().trim());
                    model1.setTimestamp(_timestampcreated);
                    model1.setColor(selectednoteColor);
                    model1.setImagepath(selectedimagePath);
                    model1.setWeblink(selectedURL);
                    if(isEditMode)
                    {
                        model1.setId(model.getId());
                        NoteDataBase.getDatabase(getApplicationContext()).UpdateNote(model1, new NoteDataBase.DataBaseOperationsListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(),"Note Saved Successfully",Toast.LENGTH_SHORT).show();
                                setSaveButton(false);
                                Intent intent= new Intent();
                                intent.putExtra("POSITION",position);
                                setResult(RESULT_OK,intent);
                                finish();
                            }

                            @Override
                            public void onFailure(String reason) {
                                Toast.makeText(getApplicationContext(),"Error: "+reason,Toast.LENGTH_SHORT).show();
                                setSaveButton(true);
                            }
                        });
                    }
                    else
                    {
                        NoteDataBase.getDatabase(getApplicationContext()).SaveNote(model1, new NoteDataBase.DataBaseOperationsListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(),"Note Saved Successfully",Toast.LENGTH_SHORT).show();
                                setSaveButton(false);
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onFailure(String reason) {
                                Toast.makeText(getApplicationContext(),"Error: "+reason,Toast.LENGTH_SHORT).show();
                                setSaveButton(true);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }

                }
            }
        });
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    expand.setImageResource(R.drawable.ic_arrow_down);
                }
                else
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    expand.setImageResource(R.drawable.ic_arrow_up);
                }
            }
        });
        bottomsheet_layout.findViewById(R.id.add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(CreateNoteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },PERMISSION_REQ_CODE);

                }
                else
                    {
                        SelectImage();
                    }


            }
        });
        bottomsheet_layout.findViewById(R.id.add_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                ShowAddURLdialog();


            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                _timestampcreated=System.currentTimeMillis();
                setSaveButton(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        subtitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                _timestampcreated=System.currentTimeMillis();
                setSaveButton(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                _timestampcreated=System.currentTimeMillis();
                setSaveButton(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ChangeSelection(colorindex);
    }

    public void setSaveButton(boolean isEnabled)
    {
        if(isEnabled)
        {
            saveBtn.setVisibility(View.VISIBLE);
            saveimg.setVisibility(GONE);
        }
        else
        {
            saveBtn.setVisibility(View.GONE);
            saveimg.setVisibility(View.VISIBLE);
        }
    }
    public void UpdateView(Note note)
    {
        title.setText(note.getTitle());
        subtitle.setText(note.getSubtitle());
        timestamp.setText(TimeHelper.getTimeStringFromMillis(note.getTimestamp()));
        text.setText(note.getNotetext());
        selectednoteColor=note.getColor();
        selectedimagePath=note.getImagepath();
        if(note.getWeblink()!=null && !note.getWeblink().isEmpty())
        {
            link.setText(note.getWeblink());
            note_layout.setVisibility(View.VISIBLE);
        }
        else
            note_layout.setVisibility(View.GONE);
        if(note.getImagepath()!=null && !note.getImagepath().isEmpty())
            Glide.with(this).load(note.getImagepath()).into(noteImg);
        ChangeSelection(selectednoteColor);
        setSaveButton(false);
    }
    public void ChangeSelection(int index)
    {
        index=index-1;
        if(index>-1)
        {
            ImageView[] imgarr={img1,img2,img3,img4,img5};
            String[] colorarr={"#333333","#fdbe3b","#ff4842","#3a52fc","#000000"};
            for(ImageView img:imgarr)
            {
                img.setImageResource(0);
            }
            imgarr[index].setImageResource(R.drawable.ic_done);
            selectednoteColor=colorarr[index];
            setSubindicatorColor();
            setSaveButton(true);
        }

    }
    public void ChangeSelection(String color)
    {
        String[] colorarr={"#333333","#fdbe3b","#ff4842","#3a52fc","#000000"};
        int i=0,index=-1;
        for(String item:colorarr)
        {
            if(item.equals(color))
                index=i;
            i++;
        }
        if(index>-1)
            ChangeSelection(index+1);


    }
    public boolean Validate()
    {
        boolean valid=true;
        if(title.getText().toString().trim().isEmpty()) {
            valid=false;
            title.setError("Note cannot be saved without Title");
        }
        if(text.getText().toString().trim().isEmpty())
        {
            valid=false;
            text.setError("Note cannot be saved without Contents");
        }
        return valid;
    }
    public void setSubindicatorColor()
    {
        GradientDrawable gradientDrawable= (GradientDrawable) subindicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectednoteColor));
    }
    public void SelectImage()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(gallery.resolveActivity(getPackageManager())!=null)
        startActivityForResult(gallery, GALLERY_REQ_CODE);
    }
    public void ShowAddURLdialog()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(
                R.layout.dialog_add_url,
                (ViewGroup)findViewById(R.id.dialogAddUrlContainer)
        );
        builder.setView(view);
        dialogAddURL=builder.create();
        if(dialogAddURL.getWindow()!=null)
        {
            dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        final EditText inputUrl=view.findViewById(R.id.url_text);
        inputUrl.requestFocus();
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input=inputUrl.getText().toString().trim();
                if(!Patterns.WEB_URL.matcher(input).matches())
                {
                    Toast.makeText(getApplicationContext(),"Enter a Valid URL",Toast.LENGTH_SHORT).show();
                }
                else {
                    selectedURL = input;
                    if(selectedURL!=null && !selectedURL.isEmpty())
                    {
                        link.setText(selectedURL);
                        note_layout.setVisibility(View.VISIBLE);
                        setSaveButton(true);
                    }
                    else
                        note_layout.setVisibility(View.GONE);
                    dialogAddURL.dismiss();
                }
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddURL.dismiss();
            }
        });
        dialogAddURL.show();
    }
    public void imgClick(View view) {
        switch (view.getId())
        {
            case R.id.v1:ChangeSelection(1);break;
            case R.id.v2:ChangeSelection(2);break;
            case R.id.v3:ChangeSelection(3);break;
            case R.id.v4:ChangeSelection(4);break;
            case R.id.v5:ChangeSelection(5);break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_REQ_CODE && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                SelectImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQ_CODE && data!=null && resultCode==RESULT_OK)
        {
            Uri imageuri=data.getData();
            if(imageuri!=null)
            {
                try {
                    selectedimagePath=getPathFromURI(imageuri);
                    InputStream inputStream= getContentResolver().openInputStream(imageuri);
                    selectedimage= BitmapFactory.decodeStream(inputStream);
                    noteImg.setImageBitmap(selectedimage);
                    setSaveButton(true);
                }
                catch (Exception e)
                {
                    Toast.makeText(CreateNoteActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public String getPathFromURI(Uri uri)
    {
        String filepath;
        Cursor cursor= getContentResolver()
                .query(uri,null,null,null,null);
        if(cursor==null)
        {
            filepath=uri.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int index=cursor.getColumnIndex("_data");
            filepath=cursor.getString(index);
            cursor.close();
        }
        return filepath;
    }
}
