package org.robovm.samples.contractr.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import org.robovm.samples.contractr.core.Client;
import org.robovm.samples.contractr.core.ClientModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import roboguice.fragment.RoboDialogFragment;
import roboguice.inject.InjectView;

public abstract class AbstractClientFragment extends RoboDialogFragment {

    @InjectView(R.id.nameTextField) TextView nameTextField;
    @InjectView(R.id.hourlyRate) EditText hourlyRateTextField;
    @InjectView(R.id.action_ok) Button okButton;
    @InjectView(R.id.action_cancel) Button cancelButton;

    @Inject ClientModel clientModel;
    Client client;
    NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.ENGLISH);

    ClientFragmentListener clientFragmentListener;

    public AbstractClientFragment() {}

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViewValuesWithClient(client);
    }

    protected abstract String getTitle();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle(getTitle());
        return inflater.inflate(R.layout.fragment_edit_client, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            clientFragmentListener = (ClientFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ClientFragmentListener");
        }
    }

    protected void onSave() {
        dismiss();
        clientFragmentListener.clientSaved();
    }

    protected void updateSaveButtonEnabled() {
        String name = nameTextField.getText().toString();
        name = name == null ? "" : name.trim();
//        getNavigationItem().getRightBarButtonItem().setEnabled(!name.isEmpty());
    }

    protected void updateViewValuesWithClient(Client client) {
        nameTextField.setText(client == null ? "" : client.getName());
        hourlyRateTextField.setText(formatter.format(client == null ? BigDecimal.ZERO : client.getHourlyRate()));
        updateSaveButtonEnabled();
    }

    protected Client saveViewValuesToClient(Client client) {
        String name = nameTextField.getText().toString();
        name = name == null ? "" : name.trim();

        client.setName(name);
        try {
            client.setHourlyRate(BigDecimal.valueOf(formatter.parse(hourlyRateTextField.getText().toString()).doubleValue()));
        } catch (ParseException e) {

        }
        return client;
    }

    public interface ClientFragmentListener {
        void clientSaved();
    }
}