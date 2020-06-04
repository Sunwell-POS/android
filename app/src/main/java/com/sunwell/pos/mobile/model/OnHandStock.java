package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunwell on 12/12/17.
 */

public class OnHandStock implements Serializable
{
    private Product product;
    private Warehouse warehouse;
    private Date lastInputDate;
    private Double qty;


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

    public Date getLastInputDate()
    {
        return lastInputDate;
    }

    public void setLastInputDate(Date _lastInputDate)
    {
        lastInputDate = _lastInputDate;
    }

    public Double getQty()
    {
        return qty;
    }

    public void setQty(Double _qty)
    {
        qty = _qty;
    }

    @Override
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof OnHandStock))
            return false;

        OnHandStock ohs = (OnHandStock) _obj;
        if(ohs.getProduct() != null && product == null)
            return  false;
        else if (ohs.getProduct() == null && product != null)
            return false;
        else if (!ohs.getProduct().equals(product))
            return false;
        else if(ohs.getWarehouse() != null && warehouse == null)
            return  false;
        else if (ohs.getWarehouse() == null && warehouse != null)
            return false;
        else if (!ohs.getWarehouse().equals(warehouse))
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        return product.getName() + " in " + warehouse.getName();
    }


}
