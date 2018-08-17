package com.emrehmrc.harcamalarm.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.emrehmrc.harcamalarm.DialogListener;
import com.emrehmrc.harcamalarm.R;
import com.emrehmrc.harcamalarm.adapters.SpinnerAdapter;
import com.emrehmrc.harcamalarm.database.DataBase;
import com.emrehmrc.harcamalarm.models.ExpenseModel;
import com.emrehmrc.harcamalarm.models.SpinnerItemModel;
import com.emrehmrc.harcamalarm.utils.Methodes;
import com.emrehmrc.harcamalarm.utils.SingletonCurrentValues;
import com.emrehmrc.harcamalarm.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AddExpenseFragment extends DialogFragment {

    private static DialogListener listener;
    private EditText edtDesp;
    private EditText edtAmount;
    private Button btnDate;
    private Spinner spnTypes;
    private ImageButton btnSave;
    private DataBase dataBase;
    private SingletonCurrentValues currentValues;
    private SwitchCompat switchCompat;
    private TextView txtCheck;
    private int amount;
    private boolean ischecked;

    public AddExpenseFragment() {

    }

    public static AddExpenseFragment newInstance(int year, int month, int day, DialogListener dialogListener) {
        AddExpenseFragment frag = new AddExpenseFragment();
        String oldstring = year + "-" + (month + 1) + "-" + day;//   "2011-01-18";
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(oldstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newstring = new SimpleDateFormat("EEE, d MMM yyyy").format(date);
        Bundle args = new Bundle();
        args.putString("title", newstring);
        frag.setArguments(args);
        listener = dialogListener;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_expense_edit, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtDesp = view.findViewById(R.id.descp_edittext);
        btnDate = view.findViewById(R.id.date_button);
        btnDate.setText(getArguments().get("title").toString());
        btnSave = view.findViewById(R.id.btn_save);
        edtAmount = view.findViewById(R.id.amount_edittext);
        spnTypes = view.findViewById(R.id.spn_types);
        switchCompat = view.findViewById(R.id.expense_type_switch);
        txtCheck = view.findViewById(R.id.expense_type_tv);
        dataBase = new DataBase(getActivity());
        switchCompat.setChecked(true);
        ischecked=true;
        List<String> items = Arrays.asList(getResources().getStringArray(R.array.spinner_items));
        final ArrayList<SpinnerItemModel> list = new ArrayList<>();
        list.add(new SpinnerItemModel(Util.IMG_PERSONEL, items.get(0)));
        list.add(new SpinnerItemModel(Util.IMG_FOOD, items.get(1)));
        list.add(new SpinnerItemModel(Util.IMG_TRAVEL, items.get(2)));
        list.add(new SpinnerItemModel(Util.IMG_EDU, items.get(3)));
        list.add(new SpinnerItemModel(Util.IMG_HEALTH, items.get(4)));
        list.add(new SpinnerItemModel(Util.IMG_FAMILY, items.get(5)));
        list.add(new SpinnerItemModel(Util.IMG_SALARY, items.get(6)));
        list.add(new SpinnerItemModel(Util.IMG_SHOPPING, items.get(7)));
        list.add(new SpinnerItemModel(Util.IMG_ACTIVITY, items.get(8)));
        list.add(new SpinnerItemModel(Util.IMG_BANK, items.get(9)));
        list.add(new SpinnerItemModel(Util.IMG_BUSSINES, items.get(10)));
        list.add(new SpinnerItemModel(Util.IMG_DEBT, items.get(11)));

        currentValues = SingletonCurrentValues.getInstance();
        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), R.id.txtDescp, list);
        spnTypes.setAdapter(adapter);
        spnTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edtDesp.setText(list.get(position).getDescp());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtAmount.getText().toString().trim().isEmpty()){
                    edtAmount.setError(Methodes.ColorString("Milktar Giriniz!",R.color.dot_dark_screen1));
                }
                else {
                    amount= Integer.parseInt(edtAmount.getText().toString());
                    if(ischecked)amount=Math.abs(amount);
                    else amount=0-amount;
               ExpenseModel model=new ExpenseModel("("+amount+"):"+edtDesp.getText().toString(),
                            ((SpinnerItemModel) spnTypes.getSelectedItem()).getImageId(),
                            amount,
                            currentValues.getYear(),
                            currentValues.getMonth(),
                            currentValues.getDay());
                    listener.onDialogClosed(model);
                    dismiss();
                }



            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtCheck.setText(Methodes.ColorString("GİRİŞ", R.color.colorPrimaryDark));

                } else {
                    txtCheck.setText(Methodes.ColorString("ÇIKIŞ", R.color.dot_dark_screen1));

                }
                ischecked=isChecked;
            }
        });

    }
}