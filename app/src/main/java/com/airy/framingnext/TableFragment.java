package com.airy.framingnext;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airy.framingnext.data.DataCenter;
import com.airy.framingnext.data.DatePeriod;
import com.airy.framingnext.data.Todo;
import com.airy.framingnext.databinding.FragmentTableBinding;
import com.airy.framingnext.view.TableAdapter;
import com.airy.framingnext.view.TodoAdapter;

import java.util.List;

public class TableFragment extends Fragment {
    private FragmentTableBinding binding;
    private TableAdapter adapter;
    private Context context;
    private DataCenter center;
    private static final int MESSAGE_UPDATE_VIEW = 1;
    private static final int MESSAGE_EDIT_CONTENT = 2;
    private static final String TAG = "TableFragment";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_UPDATE_VIEW:
                    adapter.refreshView((List<DatePeriod>) msg.obj);
                    break;
                case MESSAGE_EDIT_CONTENT:
                    EditAndTodoListObject temp = (EditAndTodoListObject) msg.obj;
                    DatePeriod curr = temp.datePeriod;
                    List<Todo> todoList = temp.list;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_table_item_edit, null);
                    EditText edit = view.findViewById(R.id.item_edit_edit);
                    edit.setText(curr.content);
                    RecyclerView todoListView = view.findViewById(R.id.item_edit_todo_list);
                    TodoAdapter todoAdapter = new TodoAdapter(todoList);
                    todoListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    todoListView.setAdapter(todoAdapter);
                    builder.setView(view)
                            .setTitle("修改内容")
                            .setPositiveButton("确认", (dialog, which) -> {
                                curr.content = edit.getText().toString();
                                adapter.refreshItemByDatePeriod(curr);
                                new Thread(() -> center.updateDatePeriod(curr)).start();
                            });
                    builder.show();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTableBinding.inflate(inflater, container, false);
        assert container != null;
        context = container.getContext();
        new Thread(() -> {
            center = DataCenter.getInstance(context);
            adapter = new TableAdapter(center.getDatePeriodList(), curr -> {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Todo> todoList = center.getTodoList();
                        Message message = new Message();
                        message.what = MESSAGE_EDIT_CONTENT;
                        message.obj = new EditAndTodoListObject(todoList, curr);
                        handler.sendMessage(message);
                    }
                }).start();
            }, context);
            boolean isNewDay = center.isDatePassed();;
            getActivity().runOnUiThread(() -> {
                newDayCheck(isNewDay, center);
                binding.fragmentTable.setAdapter(adapter);
                binding.fragmentTable.setLayoutManager(new GridLayoutManager(context, center.getDatePeriodDayCount() + 1));
            });
        }).start();
        return binding.getRoot();
    }

    private void newDayCheck(boolean isNewDay, DataCenter center) {
        if (isNewDay){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("啊！新的一天");
            dialog.setMessage("是否更新计划表并将未完成项目加入到待办队列中？");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", (dialog1, which) -> {
                new Thread(() -> {
                    center.updateTable(true);
                    Message message = new Message();
                    message.what = MESSAGE_UPDATE_VIEW;
                    message.obj = center.getDatePeriodList();
                    handler.sendMessage(message);
                }).start();
            });
            dialog.setNeutralButton("仅更新", (dialog1, which) -> new Thread(() -> {
                center.updateTable(false);
                Message message = new Message();
                message.what = MESSAGE_UPDATE_VIEW;
                message.obj = center.getDatePeriodList();
                handler.sendMessage(message);
            }).start());
            dialog.setNegativeButton("不操作", null);
            dialog.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

class EditAndTodoListObject{
    List<Todo> list;
    DatePeriod datePeriod;

    public EditAndTodoListObject(List<Todo> list, DatePeriod curr) {
        this.list = list;
        this.datePeriod = curr;
    }
}
