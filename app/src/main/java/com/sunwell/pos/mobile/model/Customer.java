package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 10/31/17.
 */

public class Customer implements Serializable
{
    private String systemId;
    private String name;
    private String memberNo;
    private String address;
    private String province;
    private String nation;
    private String phone;
    private String email;
    private String memo;
    private Double disc;
    private Boolean sysbuiltin;
    private Boolean isMale;
    private Date birthdate;

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

    public Boolean getIsMale() {
        return isMale;
    }

    public void setIsMale(Boolean male) {
        isMale = male;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getDisc() {
        return disc;
    }

    public void setDisc(Double disc) {
        this.disc = disc;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof Customer))
            return false;

        Customer cust = (Customer) _obj;
        if(cust.getSystemId() != null && systemId == null)
            return  false;
        else if (cust.getSystemId() == null && systemId != null)
            return false;
        if(cust.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public Boolean getSysbuiltin()
    {
        return sysbuiltin;
    }

    public void setSysbuiltin(Boolean _sysbuiltin)
    {
        sysbuiltin = _sysbuiltin;
    }
}
