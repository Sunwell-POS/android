package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunwell on 10/30/17.
 */

public class UserGroup implements Serializable
{
    private String systemId;
    private String name;
    private String memo;
    private Boolean sysbuiltin;
    private List<AccessRight> accessRights;

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean isSysbuiltin()
    {
        return sysbuiltin;
    }

    public void setSysbuiltin(Boolean sysbuiltin)
    {
        this.sysbuiltin = sysbuiltin;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public List<AccessRight> getAccessRights()
    {
        return accessRights;
    }

    public void setAccessRights(List<AccessRight> accessRights)
    {
        this.accessRights = accessRights;
    }

    @Override
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof UserGroup))
            return false;

        UserGroup ug = (UserGroup) _obj;
        if(ug.getSystemId() != null && systemId == null)
            return  false;
        else if (ug.getSystemId() == null && systemId != null)
            return false;
        if (ug.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
