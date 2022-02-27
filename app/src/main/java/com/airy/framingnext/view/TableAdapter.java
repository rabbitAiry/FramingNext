package com.airy.framingnext.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airy.framingnext.R;
import com.airy.framingnext.data.DatePeriod;
import com.airy.framingnext.utils.DatePeriodUtil;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableHolder> {
    private List<DatePeriod> list;
    private TableEditListener listener;
    private Context context;
    private static final String TAG = "TableAdapter";

    public interface TableEditListener {
        void onItemLongClickedListener(DatePeriod curr);
    }

    public TableAdapter(List<DatePeriod> list, TableEditListener listener, Context context) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshView(List<DatePeriod> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void refreshItemByDatePeriod(DatePeriod curr){
        for (int i = 0; i <list.size(); i++) {
            if(curr == list.get(i))notifyItemChanged(i);
        }
    }

    @NonNull
    @Override
    public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_content, parent, false);
        return new TableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        DatePeriod curr = list.get(adapterPosition);
        holder.content.setText(curr.content);

        if (curr.type != DatePeriodUtil.TYPE_WORK_DAY && curr.type != DatePeriodUtil.TYPE_REST_DAY) {
            holder.itemView.setOnLongClickListener(v -> {
                listener.onItemLongClickedListener(curr);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class TableHolder extends RecyclerView.ViewHolder {
        TextView content;

        public TableHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.table_content);
        }
    }
}
