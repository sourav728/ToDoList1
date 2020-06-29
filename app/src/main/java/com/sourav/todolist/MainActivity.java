package com.sourav.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button button_add;
    EditText edittext_search;

    private MyDatabase myDatabase;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<Todo> todoArrayList = new ArrayList<>();
    private List<Todo> selecttodoList = new ArrayList<>();
    private int size;
    public static final int NEW_TODO_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        loadAllTodos();
    }

    private void initView() {
        myDatabase = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, MyDatabase.DB_NAME).fallbackToDestructiveMigration().build();
        checkIfAppLaunchedFirstTime();
        recyclerView = findViewById(R.id.recyclerView);
        button_add = findViewById(R.id.button_add);
        edittext_search = findViewById(R.id.edittext_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, TodoNoteActivity.class), NEW_TODO_REQUEST_CODE);
            }
        });

        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<Todo> filterdNames = new ArrayList<>();
        for (Todo s : selecttodoList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())||
                    s.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        recyclerViewAdapter.filterList(filterdNames);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadAllTodos() {
        new AsyncTask<String, Void, List<Todo>>() {
            @Override
            protected List<Todo> doInBackground(String... params) {
                return myDatabase.daoAccess().fetchAllTodos();
            }

            @Override
            protected void onPostExecute(List<Todo> todoList) {
                size = todoList.size();
                selecttodoList.addAll(todoList);
                recyclerViewAdapter.updateTodoList(todoList);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchTodoByIdAndInsert(int id) {
        new AsyncTask<Integer, Void, Todo>() {
            @Override
            protected Todo doInBackground(Integer... params) {
                return myDatabase.daoAccess().fetchTodoListById(params[0]);

            }

            @Override
            protected void onPostExecute(Todo todoList) {
                selecttodoList.add(todoList);
                recyclerViewAdapter.addRow(todoList);
            }
        }.execute(id);

    }

    private void checkIfAppLaunchedFirstTime() {
        final String PREFS_NAME = "SharedPrefs";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("firstTime", true)) {
            settings.edit().putBoolean("firstTime", false).apply();
            buildDummyTodos();
        }
    }

    private void buildDummyTodos() {
        Todo todo = new Todo();
        todo.name = "T-Mobile";
        todo.description = "Headquarters: Germany";

        todoArrayList.add(todo);

        todo = new Todo();
        todo.name = "Google";
        todo.description = "Headquarters: Usa";

        todoArrayList.add(todo);
        insertList(todoArrayList);
    }

    @SuppressLint("StaticFieldLeak")
    private void insertList(List<Todo> todoList) {
        new AsyncTask<List<Todo>, Void, Void>() {
            @Override
            protected Void doInBackground(List<Todo>... params) {
                myDatabase.daoAccess().insertTodoList(params[0]);
                return null;
            }
            @Override
            protected void onPostExecute(Void voids) {
                super.onPostExecute(voids);
            }
        }.execute(todoList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == NEW_TODO_REQUEST_CODE) {
                long id = data.getLongExtra("id", -1);
                Toast.makeText(getApplicationContext(), "Row inserted..", Toast.LENGTH_SHORT).show();
                fetchTodoByIdAndInsert((int) id);
            }

        } else {
            Toast.makeText(getApplicationContext(), "No action done..", Toast.LENGTH_SHORT).show();
        }
    }

}
