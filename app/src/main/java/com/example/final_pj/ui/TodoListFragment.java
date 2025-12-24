package com.example.final_pj.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.AddTodoActivity;
import com.example.final_pj.R;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.Todo;
import com.example.final_pj.data.TodoRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodoListFragment extends Fragment {

    private RecyclerView rvTodos;
    private TextView tvNoTodo;
    private FloatingActionButton fabAdd;
    private TodoAdapter todoAdapter;
    private TodoRepository todoRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        todoRepository = new TodoRepository(requireActivity().getApplication());

        rvTodos = view.findViewById(R.id.rv_todos);
        tvNoTodo = view.findViewById(R.id.tv_no_todo_hint);
        fabAdd = view.findViewById(R.id.fab_add_todo);

        rvTodos.setLayoutManager(new LinearLayoutManager(getContext()));
        todoAdapter = new TodoAdapter();
        rvTodos.setAdapter(todoAdapter);

        todoAdapter.setOnTodoItemClickListener(new TodoAdapter.OnTodoItemClickListener() {
            @Override
            public void onStatusChanged(Todo todo, boolean isDone) {
                todo.setDone(isDone);
                todoRepository.update(todo);
            }

            @Override
            public void onDeleteClick(Todo todo) {
                todoRepository.delete(todo);
                loadTodos(); // Re-load after delete
            }
        });

        fabAdd.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddTodoActivity.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodos();
    }

    private void loadTodos() {
        todoRepository.getAllTodos(todos -> {
            todoAdapter.setTodos(todos);
            tvNoTodo.setVisibility(todos.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}