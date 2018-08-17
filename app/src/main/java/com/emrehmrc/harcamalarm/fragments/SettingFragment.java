package com.emrehmrc.harcamalarm.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.emrehmrc.harcamalarm.DialogListener;
import com.emrehmrc.harcamalarm.PrefManager;
import com.emrehmrc.harcamalarm.R;
import com.emrehmrc.harcamalarm.database.DataBase;
import com.emrehmrc.harcamalarm.utils.Methodes;

public class SettingFragment extends DialogFragment {

    private static DialogListener listener;
    private SwitchCompat switchCompat;
    private TextView txtCheck;
    private PrefManager prefManager;
    private ImageButton btnsetting;
    private ImageButton btnDel;
    private ImageButton exit;
    public static SettingFragment newInstance(DialogListener dialogListener) {

        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        listener=dialogListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchCompat=view.findViewById(R.id.expense_type_switch);
        txtCheck=view.findViewById(R.id.expense_type_tv);
        btnsetting=view.findViewById(R.id.btnSaveSetting);
        btnDel=view.findViewById(R.id.btnDeldata);
        exit=view.findViewById(R.id.btnExit);
        prefManager=new PrefManager(getActivity());
        switchCompat.setChecked(prefManager.isFirstTimeLaunch());
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    prefManager.setFirstTimeLaunch(isChecked);
                    if(isChecked)txtCheck.setText(Methodes.ColorString("Giriş Ekranını Göster",R
                            .color.colorPrimaryDark));
                    else txtCheck.setText(Methodes.ColorString("Giriş Ekranını Gösterme",R
                            .color.dot_dark_screen1));

            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBase dataBase=new DataBase(getActivity());
                dataBase.deleteAll();
                listener.onDelete();
                dismiss();
            }
        });
        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getActivity().finish();
            }
        });
    }
}
