package com.sourav.todolist;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    long insertTodo(Todo todo);

    @Insert
    void insertTodoList(List<Todo> todoList);

    @Query("SELECT * FROM " + MyDatabase.TABLE_NAME_TODO)
    List<Todo> fetchAllTodos();

    @Query("SELECT * FROM " + MyDatabase.TABLE_NAME_TODO + " WHERE todo_id = :todoId")
    Todo fetchTodoListById(int todoId);

}
