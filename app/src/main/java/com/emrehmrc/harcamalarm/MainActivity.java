package com.emrehmrc.harcamalarm;


import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrehmrc.harcamalarm.adapters.RecyclerExpenseAdapter;
import com.emrehmrc.harcamalarm.database.DataBase;
import com.emrehmrc.harcamalarm.fragments.AddExpenseFragment;
import com.emrehmrc.harcamalarm.fragments.SettingFragment;
import com.emrehmrc.harcamalarm.models.ExpenseModel;
import com.emrehmrc.harcamalarm.utils.Methodes;
import com.emrehmrc.harcamalarm.utils.SingletonCurrentValues;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final float ALPHA_FULL = 2.0f;
    SingletonCurrentValues singletonCurrentValues;
    private DatePickerTimeline timeline;
    private ImageView imgCalendar;
    private ImageView imgSetting;
    private TextView txtTitle;
    private TextView txtTotal;
    private RecyclerView recyclerView;
    private List<ExpenseModel> expenseModels;
    private List<ExpenseModel> expenseModelsAll;
    private RecyclerExpenseAdapter adapter;
    private DataBase dataBase;
    private FloatingActionButton fabAdd;
    private DialogListener dialogListener;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);
        prefManager.setFirstTimeLaunch(false);

        init();
        setClickListeners();

        String oldstring = timeline.getSelectedYear() + "-" + (timeline.getSelectedMonth() + 1)
                + "-" + timeline.getSelectedDay();//
        Log.d(TAG, "Timeline" + oldstring);
        expenseModels = dataBase.getAllExpenseByDate(timeline.getSelectedYear(), (timeline.getSelectedMonth()), timeline.getSelectedDay());
        adapter = new RecyclerExpenseAdapter(getApplicationContext(),
                expenseModels, dialogListener);
        recyclerView.setAdapter(adapter);
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(oldstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newstring = new SimpleDateFormat("EEE, dd MMM yyyy").format(date);
        txtTitle.setText(newstring);
        singletonCurrentValues = SingletonCurrentValues.getInstance();
        singletonCurrentValues.setTextDate(newstring);
        singletonCurrentValues.setYear(timeline.getSelectedYear());
        singletonCurrentValues.setMonth((timeline.getSelectedMonth() + 1));
        singletonCurrentValues.setDay(timeline.getSelectedDay());

    }

    private void setClickListeners() {

        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                Log.d(TAG, "getLabael" + calendar);
                for (int i = 0; i < expenseModelsAll.size(); i++)

                    if (calendar.get(Calendar.YEAR) == expenseModelsAll.get(i).getYear() &&
                            calendar.get(Calendar.MONTH) == expenseModelsAll.get(i).getMonth() &&
                            calendar.get(Calendar.DAY_OF_MONTH) == expenseModelsAll.get(i).getDay()) {
                        int total = dataBase.getExpenseCount(expenseModelsAll.get(i).getYear(),
                                expenseModelsAll.get(i).getMonth(),
                                expenseModelsAll.get(i).getDay());
                        if (total > 0)
                            return Methodes.ColorString(total,
                                    getResources().getColor(R.color.mti_default_primary));
                        else if (total < 0) return Methodes.ColorString
                                (total, Color
                                        .RED);
                        else if (total == 0) return Methodes.ColorString
                                (total, Color
                                        .GRAY);
                    }
                return "";

            }
        });
        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {

                for (int i = 0; i < expenseModelsAll.size(); i++) {

                    if (year == expenseModelsAll.get(i).getYear() && month == expenseModelsAll.get(i).getMonth() && day == expenseModelsAll.get(i).getDay()) {
                        int total = dataBase.getExpenseCount(expenseModelsAll.get(i).getYear(),
                                expenseModelsAll.get(i).getMonth(),
                                expenseModelsAll.get(i).getDay());
                        if (total > 0)
                            txtTotal.setText(Methodes.ColorString(total,
                                    getResources().getColor(R.color.mti_default_primary)));
                        else if (total < 0)
                            txtTotal.setText(Methodes.ColorString
                                    (total, Color
                                            .RED));
                        else if (total == 0)
                            txtTotal.setText(Methodes.ColorString
                                    (total, Color
                                            .GRAY));


                        String oldstring = year + "-" + (month + 1) + "-" + day;//   "2011-01-18";
                        expenseModels = dataBase.getAllExpenseByDate(year, (month), day);
                        adapter = new RecyclerExpenseAdapter(getApplicationContext(),
                                expenseModels, dialogListener);
                        recyclerView.setAdapter(adapter);
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd").parse(oldstring);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String newstring = new SimpleDateFormat("EEE, d MMM yyyy").format(date);
                        txtTitle.setText(newstring);
                        singletonCurrentValues.setTextDate(newstring);
                        singletonCurrentValues.setYear(year);
                        singletonCurrentValues.setMonth((month + 1));
                        singletonCurrentValues.setDay(day);
                        return;

                    } else txtTotal.setText(Methodes.ColorString
                            (0, Color.GRAY));
                    String oldstring = year + "-" + (month + 1) + "-" + day;//   "2011-01-18";
                    DataBase dataBase = new DataBase(getApplicationContext());
                    expenseModels = dataBase.getAllExpenseByDate(year, (month), day);
                    adapter = new RecyclerExpenseAdapter(getApplicationContext(), expenseModels, dialogListener);
                    recyclerView.setAdapter(adapter);
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(oldstring);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String newstring = new SimpleDateFormat("EEE, d MMM yyyy").format(date);
                    txtTitle.setText(newstring);
                    singletonCurrentValues.setTextDate(newstring);
                    singletonCurrentValues.setYear(year);
                    singletonCurrentValues.setMonth((month + 1));
                    singletonCurrentValues.setDay(day);

                }


            }
        });
        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                Integer mYear = c.get(Calendar.YEAR);
                final Integer mMonth = c.get(Calendar.MONTH);
                Integer mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style
                        .DateTime,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //  txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" +
                                // year);
                                timeline.setSelectedDate(year, (monthOfYear), dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
        dialogListener = new DialogListener() {
            @Override
            public void onDialogClosed(ExpenseModel model) {
                dataBase.insertExpense(model);
                expenseModels.add(0, model);
                adapter.notifyItemInserted(0);
                setTotalText(expenseModels);


            }

            @Override
            public void onDelete() {
                setTotalText(expenseModels);
            }
        };
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingDialog();
            }
        });

    }

    private void setTotalText(List<ExpenseModel> expenseModels) {
        int total = 0;
        for (int i = 0; i < expenseModels.size(); i++) {
            total = total + expenseModels.get(i).getAmount();
        }
        if (total > 0)
            txtTotal.setText(Methodes.ColorString(total,
                    getResources().getColor(R.color.mti_default_primary)));
        else if (total < 0)
            txtTotal.setText(Methodes.ColorString
                    (total, Color
                            .RED));
        else if (total == 0)
            txtTotal.setText(Methodes.ColorString
                    (total, Color
                            .GRAY));

    }

    private void init() {
        /*
        İnit materials
         */
        timeline = findViewById(R.id.timeline);
        imgCalendar = findViewById(R.id.imgLeft);
        imgSetting = findViewById(R.id.imgRight);
        txtTitle = findViewById(R.id.toolbar_title);
        txtTotal = findViewById(R.id.txtAmount);
        recyclerView = findViewById(R.id.recycler_expense);
        dataBase = new DataBase(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseModels = new ArrayList<>();
        expenseModelsAll = new ArrayList<>();
        expenseModelsAll = dataBase.getAllExpense();
        adapter = new RecyclerExpenseAdapter(this, expenseModels, dialogListener);
        recyclerView.setAdapter(adapter);
        fabAdd = findViewById(R.id.fabAdd);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //  Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    Bitmap icon;
                    Paint paint = new Paint();

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        paint.setColor(Color.parseColor("#14a895"));

                        RectF background = new RectF(
                                (float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);

                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF iconDest = new RectF(
                                (float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, iconDest, paint);
                    }

                } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    final float alpha = ALPHA_FULL - Math.abs(dY) / (float) viewHolder.itemView.getHeight();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationY(dY);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Toast.makeText(MainActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();


                dataBase.deleteExpense(expenseModels.get(position));
                expenseModels.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, expenseModels.size());
                adapter.notifyItemRemoved(position);
                setTotalText(expenseModels);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void showEditDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        AddExpenseFragment editNameDialogFragment = AddExpenseFragment.newInstance
                (singletonCurrentValues.getYear(), singletonCurrentValues.getMonth(),
                        singletonCurrentValues.getDay(), dialogListener);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    private void showSettingDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        SettingFragment editNameDialogFragment = SettingFragment.newInstance(dialogListener);
        editNameDialogFragment.show(fm, "fragment_setting_name");
    }

}