package org.robovm.samples.contractr.android;

import org.robovm.samples.contractr.core.Task;

/**
 * Created by henric on 2014-09-27.
 */
public class AddTaskFragment extends AbstractTaskFragment {

    public static AddTaskFragment newInstance() {
        return new AddTaskFragment();
    }

    public AddTaskFragment() {}

    @Override
    protected String getTitle() {
        return "Add task";
    }

    @Override
    protected void onSave() {
        Task task = saveViewValuesToTask(taskModel.create(client));
        taskModel.save(task);
        super.onSave();
    }

}
