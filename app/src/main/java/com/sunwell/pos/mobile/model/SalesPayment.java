package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 11/7/17.
 */

public class SalesPayment implements Serializable
{
    private String systemId;
    private String cardNumber;
    private String nameCardHolder;
    private String memo;
    private Double amount;
    private Date paidDate;
    private SalesInvoice parent;
    private PaymentMethod paymentMethod;

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String _systemId)
    {
        systemId = _systemId;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String _cardNumber)
    {
        cardNumber = _cardNumber;
    }

    public String getNameCardHolder()
    {
        return nameCardHolder;
    }

    public void setNameCardHolder(String _nameCardHolder)
    {
        nameCardHolder = _nameCardHolder;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double _amount)
    {
        amount = _amount;
    }

    public Date getPaidDate()
    {
        return paidDate;
    }

    public void setPaidDate(Date _paidDate)
    {
        paidDate = _paidDate;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String _memo)
    {
        memo = _memo;
    }

    public PaymentMethod getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod _paymentMethod)
    {
        paymentMethod = _paymentMethod;
    }

    public SalesInvoice getParent()
    {
        return parent;
    }

    public void setParent(SalesInvoice _parent)
    {
        parent = _parent;
    }

    @Override
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof SalesPayment))
            return false;

        SalesPayment sp = (SalesPayment) _obj;
        if(sp.getSystemId() != null && systemId == null)
            return  false;
        else if (sp.getSystemId() == null && systemId != null)
            return false;
        if (sp.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return systemId;
    }
}
