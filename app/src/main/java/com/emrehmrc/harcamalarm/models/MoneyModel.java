package com.emrehmrc.harcamalarm.models;

public class MoneyModel {
    private  int index;
    private  int money;
    private boolean inOrOut;

    public boolean isInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(boolean inOrOut) {
        this.inOrOut = inOrOut;
    }

    public MoneyModel(int index, int money) {
        this.index = index;
        this.money = money;
    }

    public MoneyModel(int index, Integer money, boolean inOrOut) {
        this.index = index;
        this.money = money;
        this.inOrOut = inOrOut;
    }

    public int getIndex() {

        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
