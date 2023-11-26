package org.udesc.todo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.udesc.todo.EditTaskActivity;
import org.udesc.todo.R;
import org.udesc.todo.model.ToDoModel;
import org.udesc.todo.util.ApiHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> toDoList;
    private final Context context;
    private final ApiHandler apiHandler;
    private WeakReference<Context> contextRef;
    private static final int EDIT_TASK_REQUEST_CODE = 1; // Escolha qualquer número que desejar

    public ToDoAdapter(Context context) {
        this.toDoList = new ArrayList<>();
        this.context = context;
        this.apiHandler = new ApiHandler();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoModel item = toDoList.get(position);

        // Verifica se o item não é nulo
        if (item != null) {
            holder.task.setText(item.getTask());
            holder.task.setChecked(toBoolean(item.getStatus()));

            // Remove o listener antigo para evitar chamadas indesejadas durante o reciclo do RecyclerView
            holder.task.setOnCheckedChangeListener(null);

            // Atualiza o listener da caixa de seleção
            holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Chama o método da API para atualizar o status da tarefa
                    apiHandler.updateTaskStatus(item.getId(), isChecked, new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // Verifica se a resposta não é nula e é bem-sucedida
                            if (response != null && response.isSuccessful()) {
                                // Lógica de sucesso aqui (se necessário)
                            } else {
                                // Lógica de tratamento de resposta nula ou não bem-sucedida
                                // Se a atualização falhar, reverta a alteração no estado da caixa de seleção
                                holder.task.setChecked(!isChecked);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Trata falhas na chamada, se necessário
                            // Exemplo: exibir mensagem de erro
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return toDoList != null ? toDoList.size() : 0;
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<ToDoModel> toDoList) {
        if (toDoList != null) {
            this.toDoList = toDoList;
            notifyDataSetChanged();
        }
    }

    public void deleteItem(int position) {
        ToDoModel item = toDoList.get(position);
        apiHandler.deleteTask(item.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Verifique se a resposta é diferente de nula e se a requisição foi bem-sucedida
                if (response.isSuccessful()) {
                    // Lógica de sucesso aqui
                } else {
                    // Caso a resposta seja nula
                    // Lógica para resposta não bem-sucedida
                    // Exemplo: Exiba mensagens de erro com base no código de resposta
                    if (response.code() == 404) {
                        // Recurso não encontrado
                        showErrorMessage("Recurso não encontrado");
                    } else if (response.code() == 401) {
                        // Não autorizado
                        showErrorMessage("Não autorizado");
                    } else {
                        // Outro código de resposta
                        showErrorMessage("Erro desconhecido");
                    }
                    // Se a exclusão falhar, talvez você queira manter o item na lista
                    // ou atualizar a lista a partir da API novamente para garantir consistência
                    // toDoList.add(position, item); // Adicione o item de volta à lista, se necessário
                    // notifyDataSetChanged(); // Notifique o adapter para atualizar a exibição
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Trate falhas na chamada, se necessário
                // Exemplo: exibir mensagem de erro
                showErrorMessage("Falha na exclusão");
            }
        });
    }

    private void showErrorMessage(String message) {
        // Adicione a lógica para mostrar a mensagem de erro à interface do usuário
        // Exemplo: exibir um Toast, diálogo ou atualizar um TextView
    }


    public Context getContext() {
        return context;
    }

    public void editItem(int position) {
        ToDoModel item = toDoList.get(position);

        // Verifica se o item não é nulo antes de iniciar a atividade de edição
        if (item != null) {
            // Crie um Intent para iniciar a atividade de edição
            Intent editIntent = new Intent(context, EditTaskActivity.class);

            // Passe os dados do item para a atividade de edição
            editIntent.putExtra("TASK_ID", item.getId());
            editIntent.putExtra("TASK_TEXT", item.getTask());

            // Inicie a atividade de edição e aguarde o resultado
            ((Activity) context).startActivityForResult(editIntent, EDIT_TASK_REQUEST_CODE);
        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
