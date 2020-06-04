package com.sunwell.pos.mobile.model;

import java.io.Serializable;

/**
 * Created by sunwell on 12/12/17.
 */

public class Warehouse implements Serializable
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
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof Warehouse))
            return false;

        Warehouse wrh = (Warehouse) _obj;
        if(wrh.getSystemId() != null && systemId == null)
            return  false;
        else if (wrh.getSystemId() == null && systemId != null)
            return false;
        if (wrh.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
