package com.emrehmrc.harcamalarm;

import com.emrehmrc.harcamalarm.models.ExpenseModel;

public interface DialogListener {
    public void onDialogClosed(ExpenseModel model);
    public void onDelete();
}
