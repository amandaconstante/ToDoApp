package org.udesc.todo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Aqui, você pode inicializar e configurar os elementos de interface do usuário para edição
        // Certifique-se de receber os dados do item que será editado, por meio de Intent ou outro meio

        // Exemplo de como obter dados da Intent
        Intent intent = getIntent();
        if (intent != null) {
            int taskId = intent.getIntExtra("TASK_ID", 0);
            String taskText = intent.getStringExtra("TASK_TEXT");

            // Agora você tem o ID e o texto da tarefa, use esses dados conforme necessário
        }
    }
}
