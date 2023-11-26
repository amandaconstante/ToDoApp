package org.udesc.todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.udesc.todo.model.ToDoModel;
import org.udesc.todo.util.ApiHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DataBaseHandler db;
    private ApiHandler apiHandler;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
        db = new DataBaseHandler(requireContext());
        apiHandler = new ApiHandler(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_task, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            if (task != null) {
                newTaskText.setText(task);
                if (task.length() > 0) {
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }
        }

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                if (finalIsUpdate) {
                    int taskId = bundle.getInt("id");
                    db.updateTask(taskId, text);
                    apiHandler.updateTask(taskId, text, new TaskCallback() {
                        @Override
                        protected void handleResponse(Response<Void> response) {
                            // Lida com a resposta da atualização da tarefa
                        }

                        @Override
                        protected void handleFailure(Throwable t) {
                            // Lida com a falha na atualização da tarefa
                        }
                    });
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    long newRowId = db.insertTask(task);
                    if (newRowId != -1) {
                        apiHandler.createTask(task, new TaskCallback() {
                            @Override
                            protected void handleResponse(Response<Void> response) {
                                // Lida com a resposta da criação da tarefa
                            }

                            @Override
                            protected void handleFailure(Throwable t) {
                                // Lida com a falha na criação da tarefa
                            }
                        });
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
        super.onDismiss(dialog);
    }

    // Implementa um Callback reutilizável
    private abstract class TaskCallback implements Callback<Void> {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            // Trata a resposta
            handleResponse(response);
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            // Trata a falha
            handleFailure(t);
        }

        // Métodos abstratos para serem implementados nas subclasses
        protected abstract void handleResponse(Response<Void> response);
        protected abstract void handleFailure(Throwable t);
    }
}