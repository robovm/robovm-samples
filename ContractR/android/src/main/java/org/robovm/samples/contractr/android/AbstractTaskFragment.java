package org.robovm.samples.contractr.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ToggleButton;

import com.google.inject.Inject;
import org.robovm.samples.contractr.android.adapter.ClientListAdapter;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import java.text.NumberFormat;
import java.util.Locale;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;

public abstract class AbstractTaskFragment extends RoboDialogFragment implements AdapterView.OnItemSelectedListener {

    @Inject LayoutInflater inflater;
    @InjectView(R.id.clientPicker) protected Spinner clientPicker;
    @InjectView(R.id.title) protected EditText titleTextField;
    @InjectView(R.id.notes) protected EditText notesTextField;
    @InjectView(R.id.finished) protected ToggleButton finishedToggle;
    @InjectView(R.id.action_ok) Button okButton;
    @InjectView(R.id.action_cancel) Button cancelButton;

    @Inject
    TaskModel taskModel;
    @Inject
    ClientModel clientModel;

    protected Task task;
    protected Client client;
    protected SpinnerAdapter mAdapter;

    NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    TaskFragmentListener taskFragmentListener;

    public AbstractTaskFragment() {}

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViewValuesWithTask(task);
    }

    protected abstract String getTitle();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(getTitle());
        return inflater.inflate(R.layout.fragment_edit_task, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ClientListAdapter(clientModel, inflater);
        clientPicker.setAdapter(mAdapter);
        clientPicker.setOnItemSelectedListener(this);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            taskFragmentListener = (TaskFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TaskFragmentListener");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        client = clientModel.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        client = null;
    }

    protected void onSave() {
        dismiss();
        taskFragmentListener.taskSaved();
    }

    protected void updateSaveButtonEnabled() {
        String title = titleTextField.getText().toString();
        title = title == null ? "" : title.trim();
        boolean canSave = !title.isEmpty() && task != null;
//        getNavigationItem().getRightBarButtonItem().setEnabled(canSave);
    }
    protected void updateViewValuesWithTask(Task task) {
        client = task == null ? null : task.getClient();
        int selectedRow = 0;
        if (client != null) {
            for (int i = 0; i < clientModel.count(); i++) {
                if (clientModel.get(i).equals(client)) {
                    selectedRow = i;
                    break;
                }
            }
        }
        clientPicker.setSelection(selectedRow);
        //clientTextField.setText(task == null ? "" : task.getClient().getName());
        titleTextField.setText(task == null ? "" : task.getTitle());
        notesTextField.setText(task == null ? "" : task.getNotes());
        finishedToggle.setChecked(task == null ? false : task.isFinished());
        updateSaveButtonEnabled();
    }

    protected Task saveViewValuesToTask(Task task) {
        String title = titleTextField.getText().toString();
        title = title == null ? "" : title.trim();
        String notes = notesTextField.getText().toString();
        notes = notes == null ? "" : notes.trim();

        Client client = clientModel.get((int) clientPicker.getSelectedItemPosition());
        task.setClient(client);
        task.setTitle(title);
        task.setNotes(notes);
        task.setFinished(finishedToggle.isChecked());

        return task;
    }

    public interface TaskFragmentListener {
        void taskSaved();
    }
}