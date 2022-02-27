package com.airy.framingnext.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airy.framingnext.R;
import com.airy.framingnext.data.Todo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoHolder> {
    private List<Todo> list;

    public TodoAdapter(List<Todo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoHolder holder, int position) {
        holder.content.setText(list.get(holder.getAdapterPosition()).getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TodoHolder extends RecyclerView.ViewHolder{
        TextView content;

        public TodoHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }
    }
}
