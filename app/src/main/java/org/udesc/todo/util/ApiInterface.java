package org.udesc.todo.util;

import org.udesc.todo.model.ToDoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("tasks")
    Call<List<ToDoModel>> getTasks();

    @POST("tasks")
    Call<Void> createTask(@Body ToDoModel task);

    @PUT("tasks/{id}")
    Call<Void> updateTask(@Path("id") int id, @Body String taskText);

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(@Path("id") int id);

    @PUT("tasks/{id}")
    Call<Void> updateTaskStatus(@Path("id") int id, @Body boolean status);
}

