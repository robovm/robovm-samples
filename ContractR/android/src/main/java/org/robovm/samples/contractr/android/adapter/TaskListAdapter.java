package org.robovm.samples.contractr.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {

    private TaskModel taskModel;
    private ClientModel clientModel;
    private LayoutInflater inflater;
    private boolean unfinishedOnly;

    public TaskListAdapter(TaskModel taskModel, ClientModel clientModel, LayoutInflater inflater, boolean unfinishedOnly) {
        this.taskModel = taskModel;
        this.clientModel = clientModel;
        this.inflater = inflater;
        this.unfinishedOnly = unfinishedOnly;
    }


    @Override
    public int getCount() {
        return taskModel.countUnfinished() + clientModel.count();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof Task;
    }

    public View getTaskView(Task t, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null || !convertView.getTag().equals("task")) {
            view = inflater.inflate(org.robovm.samples.contractr.android.R.layout.task_row, parent, false);
            view.setTag("task");
        }

        TextView text = (TextView) view.findViewById(org.robovm.samples.contractr.android.R.id.taskTitle);
        TextView timeWorked = (TextView) view.findViewById(org.robovm.samples.contractr.android.R.id.timeElapased);
        text.setText(t.getTitle());
        timeWorked.setText(t.getTimeElapsed());

        return view;
    }

    public View getClientSeparatorView(Client c, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null || !convertView.getTag().equals("client")) {
            view = inflater.inflate(org.robovm.samples.contractr.android.R.layout.client_separator_row, parent, false);
            view.setTag("client");
        }
        TextView clientName = (TextView) view.findViewById(org.robovm.samples.contractr.android.R.id.clientName);
        clientName.setText(c.getName());
        return view;
    }

    public Object getItem(int position) {
        int rowNr = 0;
        for (int i = 0; i < clientModel.count(); i++) {
            Client c = clientModel.get(i);
            List<Task> tasks = taskModel.getForClient(c, false);
            if (position <= rowNr + tasks.size()) {
                if (position - rowNr == 0) {
                    return c;
                }
                Task t = tasks.get(position - rowNr - 1);
                return t;
            }
            rowNr += tasks.size() + 1;
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object row = getItem(position);
        if (row instanceof  Client) {
            return getClientSeparatorView((Client) row, convertView, parent);
        } else if (row instanceof Task) {
            return getTaskView((Task) row, convertView, parent);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
