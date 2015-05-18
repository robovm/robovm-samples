package org.robovm.samples.contractr.android;


public class EditTaskFragment extends AbstractTaskFragment {

    public static EditTaskFragment newInstance() {
        EditTaskFragment fragment = new EditTaskFragment();
        return fragment;
    }

    public EditTaskFragment() {
    }

    @Override
    protected String getTitle() {
        return "Edit task";
    }

    @Override
    public void onResume() {
        updateViewValuesWithTask(task);
        super.onResume();
    }

    @Override
    protected void onSave() {
        taskModel.save(saveViewValuesToTask(task));
        super.onSave();
    }
}
