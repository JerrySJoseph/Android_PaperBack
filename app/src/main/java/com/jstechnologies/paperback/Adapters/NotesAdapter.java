package com.jstechnologies.paperback.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jstechnologies.paperback.Models.Note;
import com.jstechnologies.paperback.R;
import com.jstechnologies.paperback.TimeHelper;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {

    List<Note> models;
    Context context;
    ItemClickListener mlistener;
    public NotesAdapter(Context context,List<Note> models) {
        this.models = models;
        this.context = context;
    }

    public void setItemClicklistener(ItemClickListener mlistener) {
        this.mlistener = mlistener;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new  NoteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        holder.Bind(models.get(position),position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void updateList(List<Note> list){
        this.models.clear();
        this.models.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return models.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView title,timestamp,subtitle;
        ImageView img;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            timestamp=itemView.findViewById(R.id.note_timestamp);
            title=itemView.findViewById(R.id.note_title);
            img=itemView.findViewById(R.id.note_img);
            subtitle=itemView.findViewById(R.id.note_subtitle);
            layout=itemView.findViewById(R.id.note_layout);
        }
        public void Bind(final Note note, final int position)
        {
            title.setText(note.getTitle());
            if(note.getSubtitle()==null)
                subtitle.setVisibility(View.GONE);
            timestamp.setText(TimeHelper.getTimeStringFromMillis(note.getTimestamp()));
            subtitle.setText(note.getSubtitle());
            layout.setBackgroundColor(Color.parseColor(note.getColor()));
            if(note.getImagepath()!=null && !note.getImagepath().isEmpty()) {
                Glide.with(context).load(note.getImagepath()).into(img);
                img.setVisibility(View.VISIBLE);
            }
            else
                img.setVisibility(View.GONE);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistener.onClick(note,position);
                }
            });
        }
    }

    public interface ItemClickListener
    {
        void onClick(Note note,int position);
    }
}
