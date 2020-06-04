package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 1/4/18.
 */

public class StockCardItem implements Serializable
{
    private Date date;
    private Double register;
    private Double picked ;
    private Double balance;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date _date)
    {
        date = _date;
    }

    public Double getRegister()
    {
        return register;
    }

    public void setRegister(Double _register)
    {
        register = _register;
    }

    public Double getPicked()
    {
        return picked;
    }

    public void setPicked(Double _picked)
    {
        picked = _picked;
    }

    public Double getBalance()
    {
        return balance;
    }

    public void setBalance(Double _balance)
    {
        balance = _balance;
    }
}
