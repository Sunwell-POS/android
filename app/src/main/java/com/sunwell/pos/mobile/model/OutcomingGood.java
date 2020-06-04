package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 12/13/17.
 */

public class OutcomingGood implements Serializable
{
    private String systemId;
    private Product product;
    private Warehouse warehouse;
    private Date outcomingDate;
    private String refId;
    private String memo;
    private Integer refType;
    private Double qty;
    private Double unitPrice;

    public OutcomingGood() {

    }

    public OutcomingGood(Product _p, Warehouse _w) {
        product = _p;
        warehouse = _w;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String _systemId)
    {
        systemId = _systemId;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product _product)
    {
        product = _product;
    }

    public Warehouse getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(Warehouse _warehouse)
    {
        warehouse = _warehouse;
    }

    public Date getOutcomingDate()
    {
        return outcomingDate;
    }

    public void setOutcomingDate(Date _outcomingDate)
    {
        outcomingDate = _outcomingDate;
    }

    public String getRefId()
    {
        return refId;
    }

    public void setRefId(String _refId)
    {
        refId = _refId;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String _memo)
    {
        memo = _memo;
    }

    public Integer getRefType()
    {
        return refType;
    }

    public void setRefType(Integer _refType)
    {
        refType = _refType;
    }

    public Double getQty()
    {
        return qty;
    }

    public void setQty(Double _qty)
    {
        qty = _qty;
    }

    public Double getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(Double _unitPrice)
    {
        unitPrice = _unitPrice;
    }

    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof OutcomingGood))
            return false;

        OutcomingGood ic = (OutcomingGood) _obj;
        if(ic.getSystemId() != null && systemId == null)
            return  false;
        else if (ic.getSystemId() == null && systemId != null)
            return false;
        if(ic.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return product.getName();
    }
}
