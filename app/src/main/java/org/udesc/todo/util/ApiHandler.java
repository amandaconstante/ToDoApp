package org.udesc.todo.util;

import android.content.Context;
import android.util.Log;

import org.udesc.todo.DataBaseHandler;
import org.udesc.todo.model.ToDoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHandler {

    private ApiInterface apiInterface;
    private DataBaseHandler dbHandler;
    private Context context;

    public ApiHandler(Context context) {
        this.apiInterface = ApiClient.getClient().create(ApiInterface.class);
        this.dbHandler = new DataBaseHandler(context);
        this.context = context;
    }

    public ApiHandler() {
        
    }

    // ... Código existente ...

    public void getTasks(Callback<List<ToDoModel>> callback) {
        Call<List<ToDoModel>> call = apiInterface.getTasks();
        call.enqueue(new Callback<List<ToDoModel>>() {
            @Override
            public void onResponse(Call<List<ToDoModel>> call, Response<List<ToDoModel>> response) {
                if (response.isSuccessful()) {
                    // Atualiza o banco de dados local com as tarefas obtidas da API
                    dbHandler.updateLocalTasks(response.body());
                    callback.onResponse(call, response);
                } else {
                    // Trate o erro da API conforme necessário
                    handleApiError(response, "Error fetching tasks");
                    callback.onFailure(call, new Throwable("Error in API response"));
                }
            }

            @Override
            public void onFailure(Call<List<ToDoModel>> call, Throwable t) {
                // Trate a falha da API conforme necessário
                handleApiFailure(t, "Failed to fetch tasks");
                callback.onFailure(call, t);
            }
        });
    }

    private void handleApiFailure(Throwable t, String message) {
        // Trate a falha da API conforme necessário
        Log.e("API_ERROR", message, t);

        if (context != null) {
            // Exemplo: exiba um Toast com a mensagem de erro
            // Toast.makeText(context, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void handleApiError(Response<List<ToDoModel>> response, String message) {
        // Trate o erro da API conforme necessário
        Log.e("API_ERROR", message + ": " + response.code() + " " + response.message());
        try {
            String errorBody = response.errorBody().string();
            Log.e("API_ERROR", "Error body: " + errorBody);

            if (context != null) {
                // Exemplo: exiba um Toast com a mensagem de erro
                // Toast.makeText(context, "API Error: " + errorBody, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("API_ERROR", "Error reading error body: " + e.getMessage());
        }
    }

    public void updateTask(int taskId, String text, Callback<Void> voidCallback) {
        // Atualiza a tarefa no banco de dados local
        dbHandler.updateTask(taskId, text);

        // Chama a API para atualizar a tarefa no servidor
        Call<Void> call = apiInterface.updateTask(taskId, text);
        call.enqueue(voidCallback);
    }


    public void createTask(ToDoModel task, Callback<Void> voidCallback) {
        // Insere a nova tarefa no banco de dados local
        long newRowId = dbHandler.insertTask(task);

        // Se a inserção local foi bem-sucedida, chama a API para criar a tarefa no servidor
        if (newRowId != -1) {
            Call<Void> call = apiInterface.createTask(task);
            call.enqueue(voidCallback);
        } else {
            // Trate o caso em que a inserção local falhou
            voidCallback.onFailure(null, new Throwable("Local insert failed"));
        }
    }

    public void updateTaskStatus(int id, boolean isChecked, Callback<Void> voidCallback) {
        // Atualiza o status da tarefa no banco de dados local
        dbHandler.updateTaskStatus(id, isChecked);

        // Chama a API para atualizar o status da tarefa no servidor
        Call<Void> call = apiInterface.updateTaskStatus(id, isChecked);
        call.enqueue(voidCallback);
    }

    public void deleteTask(int id, Callback<Void> voidCallback) {
        // Remove a tarefa do banco de dados local
        dbHandler.deleteTask(id);

        // Chama a API para excluir a tarefa do servidor
        Call<Void> call = apiInterface.deleteTask(id);
        call.enqueue(voidCallback);
    }

    // ... Outros métodos da API ...

}
