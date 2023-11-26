package org.udesc.todo.util;

import org.udesc.todo.model.ToDoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            onFailure("Erro na resposta da API: " + response.message());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure("Erro na chamada à API: " + t.getMessage());
    }

    public abstract void onResponse(List<ToDoModel> taskList);

    // Métodos abstratos para serem implementados nas classes que estenderem esta
    public abstract void onSuccess(T data);

    public abstract void onFailure(String errorMessage);
}
