package com.sunwell.pos.mobile.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunwell on 10/23/17.
 */
public class Product implements Serializable
{
    private String systemId;
    private String name;
    private String metric;
    private String img;
    private String imgData;
    private String barCode;
    private String description;
    private Double price;
    private Double stockMin;
    private Boolean hasDiscount;
    private Boolean status;
    private Boolean hasStock;

    public Product() {

    }

    public Product(String _systemId) {
        systemId = _systemId;
    }

    private List<ProdCategory> categories;

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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getHasStock() {
        return hasStock;
    }

    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
    }

    public Double getStockMin() {
        return stockMin;
    }

    public void setStockMin(Double stockMin) {
        this.stockMin = stockMin;
    }

    public Boolean getHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(Boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String img) {
        this.imgData = img;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProdCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ProdCategory> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object _obj)
    {
        if (!(_obj instanceof Product))
            return false;

        Product p = (Product) _obj;
        if(p.getSystemId() != null && systemId == null)
            return  false;
        else if (p.getSystemId() == null && systemId != null)
            return false;
        if (p.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
