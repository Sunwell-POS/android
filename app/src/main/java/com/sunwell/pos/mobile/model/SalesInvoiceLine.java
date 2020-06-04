package com.sunwell.pos.mobile.model;

import android.util.Log;

import com.sunwell.pos.mobile.util.Util;

import java.io.Serializable;

/**
 * Created by sunwell on 11/2/17.
 */

public class SalesInvoiceLine implements Serializable
{
    public static final int DISC_TYPE_PERCENTAGE = 0;
    public static final int DISC_TYPE_MONEY = 1;

    private String systemId;
    private String metric;
    private Integer discType;
    private Double price;
    private Double discValue;
    private Double qty;
    private Boolean hasdiscount;
    private Product product;

    public String getSystemId()
    {
        return systemId;
    }

    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public Double getPrice()
    {
        return price;
    }

    public void setPrice(Double price)
    {
        this.price = price;
    }

    public String getMetric()
    {
        return metric;
    }

    public void setMetric(String metric)
    {
        this.metric = metric;
    }

    public Boolean getHasdiscount()
    {
        return hasdiscount;
    }

    public void setHasdiscount(Boolean hasdiscount)
    {
        this.hasdiscount = hasdiscount;
    }

    public Double getDiscValue()
    {
        return discValue;
    }

    public void setDiscValue(Double discValue)
    {
        this.discValue = discValue;
    }

    public Integer getDiscType()
    {
        return discType;
    }

    public void setDiscType(Integer discType)
    {
        this.discType = discType;
    }

    public Double getQty()
    {
        return qty;
    }

    public void setQty(Double qty)
    {
        this.qty = qty;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public double getSubTotal()
    {
        return qty * price;
    }

    public double getRealDiscValue()
    {
        double disc = 0;
        if (discType == SalesInvoiceLine.DISC_TYPE_PERCENTAGE) {
            disc = (discValue / 100) * getSubTotal();
            Log.d(Util.APP_TAG, "DISC: " + disc + " disc type: " + discType);
        }
        else
            disc = discValue;

        return disc;
    }

    public double getTotal()
    {
        return getSubTotal() - getRealDiscValue();
//        double subTotal = qty * price;
//        double disc = 0;
//        double total = 0;
//        if(discType == SalesInvoiceLine.DISC_TYPE_PERCENTAGE)
//            disc = (disc/100) * subTotal;
//        else
//            disc = discValue;
//
//        total = subTotal - disc;
//        return total;
    }

    @Override
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof SalesInvoiceLine))
            return false;

        SalesInvoiceLine sil = (SalesInvoiceLine) _obj;
        if(sil.getSystemId() != null && systemId == null)
            return  false;
        else if (sil.getSystemId() == null && systemId != null)
            return false;
        if (sil.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }


    @Override
    public String toString() {
        return product.getName();
    }
}
