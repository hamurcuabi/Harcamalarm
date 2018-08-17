package com.emrehmrc.harcamalarm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.emrehmrc.harcamalarm.DialogListener;
import com.emrehmrc.harcamalarm.R;
import com.emrehmrc.harcamalarm.database.DataBase;
import com.emrehmrc.harcamalarm.models.ExpenseModel;
import com.emrehmrc.harcamalarm.utils.Methodes;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class RecyclerExpenseAdapter extends RecyclerView.Adapter<RecyclerExpenseAdapter.ExpenseViewHolder> {
    private static final String TAG = "RecyclerExpenseAdapter";
    private Context mContext;
    private List<ExpenseModel> list;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    DialogListener dialogListener;

    public RecyclerExpenseAdapter(Context mContext, List<ExpenseModel> list,DialogListener listener) {
        this.mContext = mContext;
        this.list = list;
        dialogListener=listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_ONE:
                view = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.recycle_row_expense, parent, false);
                return new ExpenseViewHolder(view);
            case TYPE_TWO:
                view = LayoutInflater
                        .from(mContext)
                        .inflate(R.layout.recycle_row_expense_next, parent, false);
                return new ExpenseViewHolder(view);
        }
        return null;
    }
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: ");
        switch (position%2){
            case 0:return TYPE_ONE;
            case 1:return TYPE_TWO;
            default:return TYPE_ONE;
        }

    }
    @Override
    public void onBindViewHolder(@NonNull final ExpenseViewHolder holder, final int position) {
        final ExpenseModel expenseModel=list.get(position);
        Picasso.get()
                .load(expenseModel.getImgId())
                .resize(48,48)
                .into(holder.imageView);
        holder.txtDesc.setText(expenseModel.getDescp());
        if (expenseModel.getAmount() > 0)
            holder.txtAmount.setText( Methodes.ColorString( expenseModel.getAmount(),
                   mContext.getResources().getColor(R.color.mti_default_primary)));
        else if (expenseModel.getAmount() < 0)
            holder.txtAmount.setText(Methodes.ColorString(expenseModel.getAmount(),
                    Color.RED));
        else if  (expenseModel.getAmount() == 0)
            holder.txtAmount.setText( Methodes.ColorString( expenseModel.getAmount(),
                    Color.GRAY));


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }



    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView txtDesc;
        protected TextView txtAmount;
        protected CardView cardView;

        public ExpenseViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.imgType);
            this.txtDesc = view.findViewById(R.id.txtDescp);
            this.txtAmount = view.findViewById(R.id.txtAmount);
            this.cardView=view.findViewById(R.id.card_row);


        }


    }
}
