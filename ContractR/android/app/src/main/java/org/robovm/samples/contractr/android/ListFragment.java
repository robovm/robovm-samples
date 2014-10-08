package org.robovm.samples.contractr.android;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.google.inject.Inject;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public abstract class ListFragment extends RoboFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @Inject protected LayoutInflater inflater;
    @InjectView(android.R.id.list) protected AbsListView mListView;

    public ListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    protected abstract void onEdit(int row);

    protected abstract void onAdd();

    protected abstract void onDelete(int row);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            onAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openDialog(DialogFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        fragment.show(ft, "dialog");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.work, menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onEdit(position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        onDelete(position);
        return true;
    }

}
