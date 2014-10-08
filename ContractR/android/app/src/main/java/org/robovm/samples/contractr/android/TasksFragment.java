package org.robovm.samples.contractr.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.inject.Inject;
import org.robovm.samples.contractr.android.adapter.TaskListAdapter;

import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

public class TasksFragment extends ListFragment {

    @Inject TaskModel taskModel;
    @Inject ClientModel clientModel;

    private TaskListAdapter mAdapter;

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        return fragment;
    }

    public TasksFragment() {
    }

    @Override
    protected void onAdd() {
        AddTaskFragment f = AddTaskFragment.newInstance();
        openDialog(f);
    }

    @Override
    protected void onEdit(int row) {
        EditTaskFragment f = EditTaskFragment.newInstance();
        Task task = (Task) mAdapter.getItem(row);
        f.setTask(task);
        openDialog(f);
    }


    protected void onDelete(final int row) {
        final Task task = (Task) mAdapter.getItem(row);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to delete " + task.getTitle() + "?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        taskModel.delete(task);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Task deleted", Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new TaskListAdapter(taskModel, clientModel, inflater, false);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    public void taskSaved() {
        mAdapter.notifyDataSetChanged();
    }



}
