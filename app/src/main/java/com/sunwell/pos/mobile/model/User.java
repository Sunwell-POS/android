package com.sunwell.pos.mobile.model;

import java.io.Serializable;

/**
 * Created by sunwell on 10/17/17.
 */

public class User implements Serializable
{
    private String systemId;
    private String email;
    private String userKey;
    private String name;
    private String password;
    private String phone;
    private String img;
    private String imgData;
    private Boolean active;
    private Boolean status;
    private UserGroup userGroup;

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getUserKey()
    {
        return userKey;
    }

    public void setUserKey(String userKey)
    {
        this.userKey = userKey;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getImg()
    {
        return img;
    }

    public void setImg(String _img)
    {
        this.img = img;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String img) {
        this.imgData = img;
    }


    public Boolean isActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    public Boolean isStatus()
    {
        return status;
    }

    public void setStatus(Boolean status)
    {
        this.status = status;
    }

    public UserGroup getUserGroup()
    {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup)
    {
        this.userGroup = userGroup;
    }

    @Override
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof User))
            return false;

        User u = (User) _obj;
        if(u.getSystemId() != null && systemId == null)
            return  false;
        else if (u.getSystemId() == null && systemId != null)
            return false;
        if (u.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
