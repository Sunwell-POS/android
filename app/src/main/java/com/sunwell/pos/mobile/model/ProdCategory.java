package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 10/18/17.
 */

public class ProdCategory implements Serializable
{

    private String systemId;
    private String name;
    private String bgColor;
    private Boolean default1;
    private Date createdAt;
    private Date updatedAt;


    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean isDefault1() {
        return default1;
    }

    public void setDefault1(Boolean default1) {
        this.default1 = default1;
    }

    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof ProdCategory))
            return false;

        ProdCategory ctgr = (ProdCategory) _obj;
        if(ctgr.getSystemId() != null && systemId == null)
            return  false;
        else if (ctgr.getSystemId() == null && systemId != null)
            return false;
        if(ctgr.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
