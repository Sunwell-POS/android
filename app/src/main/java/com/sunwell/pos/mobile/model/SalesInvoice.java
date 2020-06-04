package com.sunwell.pos.mobile.model;

import android.util.Log;

import com.sunwell.pos.mobile.util.Util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by sunwell on 11/2/17.
 */

public class SalesInvoice implements Serializable {
    private String systemId;
    private String voidMemo;
    private String miscChargesMemo;
    private String name;
    private String noInvoice;
    private String description;
    private Integer splitCount;
    private Integer discType;
    private Integer miscChargesType;
    private Double discValue;
    private Double discTotal;
    private Double miscChargesValue;
    private Double vat;
    private Boolean paid ;
    private Boolean voided ;
    private Boolean vatInclusive;
    private Date noInvoiceDate;
    private Date voidDate;
    private Customer customer;
    private List<SalesInvoiceLine> salesInvoiceLines;

    public SalesInvoice() {

    }

    public SalesInvoice(String _id) {
        systemId = _id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Integer getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(Integer splitCount) {
        this.splitCount = splitCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoInvoice() {
        return noInvoice;
    }

    public void setNoInvoice(String noInvoice) {
        this.noInvoice = noInvoice;
    }

    public Date getNoInvoiceDate() {
        return noInvoiceDate;
    }

    public void setNoInvoiceDate(Date noInvoiceDate) {
        this.noInvoiceDate = noInvoiceDate;
    }

    public Double getDiscValue() {
        return discValue;
    }

    public void setDiscValue(Double discValue) {
        this.discValue = discValue;
    }

    public Integer getDiscType() {
        return discType;
    }

    public void setDiscType(Integer discType) {
        this.discType = discType;
    }

    public Double getDiscTotal() {
        return discTotal;
    }

    public void setDiscTotal(Double discTotal) {
        this.discTotal = discTotal;
    }

    public Double getMiscChargesValue() {
        return miscChargesValue;
    }

    public void setMiscChargesValue(Double miscChargesValue) {
        this.miscChargesValue = miscChargesValue;
    }

    public Integer getMiscChargesType() {
        return miscChargesType;
    }

    public void setMiscChargesType(Integer miscChargesType) {
        this.miscChargesType = miscChargesType;
    }

    public String getMiscChargesMemo() {
        return miscChargesMemo;
    }

    public void setMiscChargesMemo(String miscChargesMemo) {
        this.miscChargesMemo = miscChargesMemo;
    }

    public Date getVoidDate() {
        return voidDate;
    }

    public void setVoidDate(Date voidDate) {
        this.voidDate = voidDate;
    }

    public String getVoidMemo() {
        return voidMemo;
    }

    public void setVoidMemo(String voidMemo) {
        this.voidMemo = voidMemo;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Boolean getVatInclusive() {
        return vatInclusive;
    }

    public void setVatInclusive(Boolean vatInclusive) {
        this.vatInclusive = vatInclusive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<SalesInvoiceLine> getSalesInvoiceLines() {
        return salesInvoiceLines;
    }

    public void setSalesInvoiceLines(List<SalesInvoiceLine> salesInvoiceLines) {
        this.salesInvoiceLines = salesInvoiceLines;
    }

    public double getSubTotal() {
        double subTotal = 0;

        if(salesInvoiceLines != null && salesInvoiceLines.size() > 0) {
            for (SalesInvoiceLine sil : salesInvoiceLines) {
                subTotal += sil.getSubTotal();
            }
        }

        return subTotal;
    }

    public double getTotalDiscount() {
        double discount = 0;

        if(salesInvoiceLines != null && salesInvoiceLines.size() > 0) {
            for (SalesInvoiceLine sil : salesInvoiceLines) {
                discount += sil.getRealDiscValue();
            }
        }

        return discount;
    }

    public double getServiceAmount() {
        double total = getSubTotal() - getTotalDiscount();
        double svc = 0.05 * total;
        Log.d(Util.APP_TAG, "ST: " + getSubTotal() + " TD: " + getTotalDiscount() + " total: " + total + " svc: " + svc);
        return svc;
    }

    public double getTotal() {
        double total = getSubTotal() - getTotalDiscount();
        double svc = 0.05 * total;
        total += svc;
        double tax = 0.10 * total;
        return total + svc + tax;
    }

    public double getTaxAmount() {
        double total = getSubTotal() - getTotalDiscount();
        double svc = 0.05 * total;
        total += svc;
        double tax = 0.10 * total;
        return tax;
    }


    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof SalesInvoice))
            return false;

        SalesInvoice inv = (SalesInvoice) _obj;
        if(inv.getSystemId() != null && systemId == null)
            return  false;
        else if (inv.getSystemId() == null && systemId != null)
            return false;
        if(inv.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return noInvoice;
    }
}
