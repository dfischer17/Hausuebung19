package com.example.notesmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class NoteAdapter extends BaseAdapter {
    private int layoutId;
    private List<Note> noteList;
    private LayoutInflater inflater;

    public NoteAdapter(Context ctx, int layoutId, List<Note> noteList) {
        this.layoutId = layoutId;
        this.noteList = noteList;
        this.inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = noteList.get(position);
        View listitem = (convertView == null) ?
                inflater.inflate(this.layoutId, null) : convertView;

        ((TextView) listitem.findViewById(R.id.date)).setText(note.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ((TextView) listitem.findViewById(R.id.name)).setText(note.getName());

        return listitem;
    }
}
