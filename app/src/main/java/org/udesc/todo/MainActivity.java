package org.udesc.todo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.udesc.todo.adapter.ToDoAdapter;
import org.udesc.todo.model.ToDoModel;
import org.udesc.todo.util.ApiCallback;
import org.udesc.todo.util.ApiHandler;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private ApiHandler apiHandler;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Use the application context here
        apiHandler = new ApiHandler(getApplicationContext());

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        fab = findViewById(R.id.fab);

        setupItemTouchHelper(); // Método para configurar o ItemTouchHelper

        // Chame a API para obter as tarefas e atualizar o RecyclerView
        fetchTasksFromApi();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
    }

    private void fetchTasksFromApi() {
        apiHandler.getTasks(new ApiCallback<List<ToDoModel>>() {
            @Override
            public void onResponse(List<ToDoModel> taskList) {
                Collections.reverse(taskList);
                tasksAdapter.setTasks(taskList);
            }

            @Override
            public void onSuccess(List<ToDoModel> data) {

            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Erro ao obter tarefas da API: " + errorMessage);
                Toast.makeText(MainActivity.this, "Erro ao obter tarefas da API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        // Atualize esta parte para usar o ApiHandler para obter as tarefas da API após adicionar ou editar
        fetchTasksFromApi();
    }
}
