package com.example.notesmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Note> noteList = new ArrayList<>();
    private ListView listView;
    private NoteAdapter noteAdapter;
    private String fileName = "notes.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewNotes);

        // todo list mit gespeicherten Notes fuellen

        // Adapter setzen
        noteAdapter = new NoteAdapter(this, R.layout.list_item_layout, noteList);
        listView.setAdapter(noteAdapter);

        // listView registrieren fuer context menu
        registerForContextMenu(listView);

        // Notes laden
        try {
            loadNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveNotes();
    }

    // Action Bar Menue erzeugen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO Auf Action Bar Menue Auswahl reagieren
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);

        switch (id) {
            case R.id.save:
                saveNotes();
                break;

            case R.id.createNote:
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setPositiveButton("OK", (dialog, which) -> handleDialog(dialogView))
                        .setNegativeButton("CANCEL", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleDialog(final View vDialog) {
        // Datum und Zeit auslesen
        DatePicker datePicker = vDialog.findViewById(R.id.datePicker);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        TimePicker timePicker = vDialog.findViewById(R.id.timePicker);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute);

        // Name auslesen
        EditText nameView = vDialog.findViewById(R.id.name);
        String name = nameView.getText().toString();

        addNote(new Note(timestamp, name));
    }

    private Note handleEditDialog(final View vDialog, int pos) {
        // Datum und Zeit auslesen
        DatePicker datePicker = vDialog.findViewById(R.id.datePicker);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        TimePicker timePicker = vDialog.findViewById(R.id.timePicker);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        LocalDateTime timestamp = LocalDateTime.of(year, month, day, hour, minute);

        // Name auslesen
        EditText nameView = vDialog.findViewById(R.id.name);
        String name = nameView.getText().toString();

        return new Note(timestamp, name);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    // todo auf context menue reagieren
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final int pos;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (info != null) {
            pos = info.position;
        }
        else {
            pos = -1;
        }

        switch (id) {
            case R.id.edit:
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);

                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setPositiveButton("OK", (dialog, which) -> handleEditDialog(dialogView, pos))
                        .setNegativeButton("CANCEL", null)
                        .show();
                break;

            case R.id.details:
                break;

            case R.id.delete:
                removeNote(pos);
                saveNotes();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void loadNotes() throws IOException {
        FileInputStream fis = openFileInput(fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        String line;

        while ((line = in.readLine()) != null) {
            String[] attributes = line.split(";");

            LocalDateTime dateTime = LocalDateTime.parse(attributes[0].replaceAll(" ", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String name = attributes[1];

            addNote(new Note(dateTime, name));
        }

        in.close();
    }

    private void saveNotes() {
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fos));

            noteList.forEach(note -> {
                out.print(note.toCsvString() + "\n");
            });

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addNote(Note note) {
        noteList.add(note);
        noteAdapter.notifyDataSetChanged();
    }

    private void removeNote(int pos) {
        noteList.remove(pos);
        noteAdapter.notifyDataSetChanged();
    }
}
