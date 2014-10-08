package org.robovm.samples.contractr.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;
import org.robovm.samples.contractr.android.adapter.TaskListAdapter;

import org.robovm.samples.contractr.core.ClientModel;
import org.robovm.samples.contractr.core.Task;
import org.robovm.samples.contractr.core.TaskModel;

import java.text.NumberFormat;
import java.util.Locale;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class WorkFragment extends RoboFragment implements View.OnClickListener {

    private static final int START_COLOR = ContractRApplication.HIGHLIGHT_COLOR;
    private static final int STOP_COLOR = Color.argb(255, 255, 0, 0);

    @Inject private LayoutInflater inflater;
    private Task task;

    @InjectView(R.id.startStopButton) Button startStopButton;
    @InjectView(R.id.currentTaskLabel) TextView currentTaskLabel;
    @InjectView(R.id.timerLabel) TextView timerLabel;
    @InjectView(R.id.amountEarned) TextView amountEarned;

    private boolean showing = false;
    @Inject private TaskModel taskModel;
    @Inject private ClientModel clientModel;

    public static WorkFragment newInstance() {
        WorkFragment fragment = new WorkFragment();
        return fragment;
    }
    public WorkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        showing = true;
        updateUIComponents();
        tick();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startStopButton.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        showing = !hidden;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work, container, false);
    }

    @Override
    public void onClick(View v) {
        Task workingTask = taskModel.getWorkingTask();
        if (workingTask == null) {
           final TaskListAdapter adapter = new TaskListAdapter(taskModel, clientModel, inflater, true);
           new AlertDialog.Builder(getActivity())
                    .setTitle("Select task")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            task = (Task) adapter.getItem(which);
                            start(task);
                        }
                    }).create().show();
        } else {
            stop();
        }
    }

    private void updateUIComponents() {
        Task task = taskModel.getWorkingTask();
        int startStopColor;
        String startStopTitle = null;
        String currentTaskText = null;
        if (task == null) {
            startStopTitle = "Start Work";
            startStopColor = START_COLOR;
            currentTaskText = "None";
            startStopButton.setBackground(getResources().getDrawable(R.drawable.start_button));
        } else {
            startStopTitle = "Stop Work";
            startStopColor = STOP_COLOR;
            currentTaskText = task.getClient().getName() + " - " + task.getTitle();
            startStopButton.setBackground(getResources().getDrawable(R.drawable.stop_button));
        }

        startStopButton.setText(startStopTitle);
        startStopButton.setTextColor(startStopColor);
        currentTaskLabel.setText(currentTaskText);

    }

    private void start(Task task) {
        taskModel.startWork(task);
        updateUIComponents();
        tick();
    }

    private void stop() {
        taskModel.stopWork();
        updateUIComponents();
        tick(); // Resets timer to 00:00:00
    }

    private void tick() {
        if (!showing) {
            return;
        }
        Task task = taskModel.getWorkingTask();
        if (task != null) {
            timerLabel.setText(task.getTimeElapsed());
            amountEarned.setText(task.getAmountEarned(Locale.US));
            final Handler handler = new Handler();
            Runnable update = new Runnable() {
                @Override
                public void run() {
                    tick();
                }
            };

            handler.postDelayed(update, 1000);
        } else {
            timerLabel.setText("00:00:00");
            amountEarned.setText(NumberFormat.getCurrencyInstance(Locale.US).format(0));
        }
    }

}
