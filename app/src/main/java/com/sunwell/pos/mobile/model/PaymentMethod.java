package com.sunwell.pos.mobile.model;

import java.io.Serializable;

/**
 * Created by sunwell on 11/7/17.
 */

public class PaymentMethod implements Serializable
{
    private String systemId;
    private String name;
    private String memo;

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

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String _memo)
    {
        memo = _memo;
    }

    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof PaymentMethod))
            return false;

        PaymentMethod pm = (PaymentMethod) _obj;

        if(pm.getSystemId() != null && systemId == null)
            return  false;
        else if (pm.getSystemId() == null && systemId != null)
            return false;
        else if(pm.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
