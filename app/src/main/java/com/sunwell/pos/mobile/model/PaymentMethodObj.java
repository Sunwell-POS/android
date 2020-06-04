package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 11/7/17.
 */

public class PaymentMethodObj implements Serializable
{
    public static final int DISC_TYPE_PERCENTAGE = 0;
    public static final int DISC_TYPE_AMOUNT = 1;

    private String systemId;
    private String name;
    private String memo;
    private Integer discType;
    private Double discValue;
    private Double minPayment;
    private Double maxPayment;
    private Boolean sysbuiltin;
    private Boolean status;
    private Boolean hasDisc;
    private Date dueDate;
    private PaymentMethod parent;

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String _systemId)
    {
        systemId = _systemId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String _name)
    {
        name = _name;
    }

    public Boolean getSysbuiltin()
    {
        return sysbuiltin;
    }

    public void setSysbuiltin(Boolean _sysbuiltin)
    {
        sysbuiltin = _sysbuiltin;
    }

    public Boolean getStatus()
    {
        return status;
    }

    public void setStatus(Boolean _status)
    {
        status = _status;
    }

    public Boolean getHasDisc()
    {
        return hasDisc;
    }

    public void setHasDisc(Boolean _hasDisc)
    {
        hasDisc = _hasDisc;
    }

    public Double getDiscValue()
    {
        return discValue;
    }

    public void setDiscValue(Double _discValue)
    {
        discValue = _discValue;
    }

    public Integer getDiscType()
    {
        return discType;
    }

    public void setDiscType(Integer _discType)
    {
        discType = _discType;
    }

    public Double getMinPayment()
    {
        return minPayment;
    }

    public void setMinPayment(Double _minPayment)
    {
        minPayment = _minPayment;
    }

    public Double getMaxPayment()
    {
        return maxPayment;
    }

    public void setMaxPayment(Double _maxPayment)
    {
        maxPayment = _maxPayment;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date _dueDate)
    {
        dueDate = _dueDate;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String _memo)
    {
        memo = _memo;
    }

    public PaymentMethod getParent()
    {
        return parent;
    }

    public void setParent(PaymentMethod _parent)
    {
        parent = _parent;
    }

    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof PaymentMethodObj))
            return false;

        PaymentMethodObj pm = (PaymentMethodObj) _obj;
        if(pm.getSystemId() != null && systemId == null)
            return  false;
        else if (pm.getSystemId() == null && systemId != null)
            return false;
        if(pm.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }


}
