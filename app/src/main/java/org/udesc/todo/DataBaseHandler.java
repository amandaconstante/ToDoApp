package org.udesc.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.udesc.todo.model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    // Define your table and columns
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK = "task";
    private static final String COLUMN_STATUS = "status";

    // Constructor
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your table
        String createTableQuery = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK + " TEXT, " +
                COLUMN_STATUS + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
        // This method will be triggered when the DATABASE_VERSION is incremented
        // You can handle database schema upgrades here
    }

    // Método para inserir uma nova tarefa
    public long insertTask(String task, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_STATUS, status);

        long newRowId = db.insert(TABLE_TASKS, null, values);
        db.close();
        return newRowId;
    }

    // Método para obter todas as tarefas
    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase(); // Alterado para getReadableDatabase
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    task.setTask(cursor.getString(cursor.getColumnIndex(COLUMN_TASK)));
                    task.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        return taskList;
    }

    // Método para atualizar uma tarefa
    public int updateTask(int id, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);

        // Atualizando a linha
        int rowsAffected = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return rowsAffected;
    }

    // Método para excluir uma tarefa
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Método para inserir uma tarefa usando o modelo ToDoModel
    public long insertTask(ToDoModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task.getTask());
        values.put(COLUMN_STATUS, task.getStatus());

        long newRowId = db.insert(TABLE_TASKS, null, values);
        db.close();
        return newRowId;
    }

    public void openDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Pode realizar alguma lógica adicional, se necessário
        db.close();
    }

    // Atualiza as tarefas locais com a lista obtida da API
    public void updateLocalTasks(List<ToDoModel> body) {
        // Lógica para atualizar as tarefas locais no banco de dados
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Inicia uma transação
            db.beginTransaction();

            // Exclui todas as tarefas existentes
            db.delete(TABLE_TASKS, null, null);

            // Insere as novas tarefas
            for (ToDoModel task : body) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TASK, task.getTask());
                values.put(COLUMN_STATUS, task.getStatus());
                db.insert(TABLE_TASKS, null, values);
            }

            // Define a transação como bem-sucedida
            db.setTransactionSuccessful();
        } finally {
            // Finaliza a transação
            db.endTransaction();
            db.close();
        }
    }

    // Atualiza o status de uma tarefa no banco de dados local
    public void updateTaskStatus(int id, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, isChecked ? 1 : 0);

        // Atualiza o status da linha
        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
