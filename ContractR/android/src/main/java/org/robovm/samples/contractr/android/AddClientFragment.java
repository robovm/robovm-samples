package org.robovm.samples.contractr.android;

import android.app.Fragment;

import org.robovm.samples.contractr.core.Client;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AddClientFragment extends AbstractClientFragment {


    public AddClientFragment() {
        // Required empty public constructor
    }

    @Override
    protected String getTitle() {
        return "Add client";
    }

    public static AddClientFragment newInstance() {
        return new AddClientFragment();
    }

    @Override
    protected void onSave() {
        Client client = saveViewValuesToClient(clientModel.create());
        clientModel.save(client);
        super.onSave();
    }
}
