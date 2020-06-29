package com.sourav.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TodoNoteActivity extends AppCompatActivity {

    EditText inTitle, inDesc;
    Button btnDone,btnCancel;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_note);
        initVIew();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Todo todo = new Todo();
                todo.name = inTitle.getText().toString();
                todo.description = inDesc.getText().toString();
                if (TextUtils.isEmpty(inTitle.getText().toString())){
                    Toast.makeText(TodoNoteActivity.this, "Please Add Title!!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(inDesc.getText().toString())){
                    Toast.makeText(TodoNoteActivity.this, "Please Add Description!!", Toast.LENGTH_SHORT).show();
                }else {
                    insertRow(todo);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initVIew(){
        inTitle = findViewById(R.id.inTitle);
        inDesc = findViewById(R.id.inDescription);
        btnDone = findViewById(R.id.btnDone);
        btnCancel = findViewById(R.id.btnCancel);
        myDatabase = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, MyDatabase.DB_NAME).build();
    }

    @SuppressLint("StaticFieldLeak")
    private void insertRow(Todo todo) {
        new AsyncTask<Todo, Void, Long>() {
            @Override
            protected Long doInBackground(Todo... params) {
                return myDatabase.daoAccess().insertTodo(params[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);
                Intent intent = getIntent();
                intent.putExtra("isNew", true).putExtra("id", id);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.execute(todo);

    }
}
