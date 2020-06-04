package com.sunwell.pos.mobile.model;

import java.io.Serializable;

/**
 * Created by sunwell on 1/8/18.
 */

public class StockMutationItem implements Serializable
{

    private Product product;
    private Double beginningBalance ;
    private Double inQty;
    private Double outQty;

    public StockMutationItem() {

    }

//    public StockMutationItemDTO(IncomingGood _ic) {
//        setData (_ic);
//    }
//
//    public void setData(IncomingGood _ic) {
//        systemId = _ic.getSystemId ();
//
//        // bisa null product dan warehousenya karena ada incoming good summary yanghanya punya inQty saja
//
//        if(_ic.getProduct () != null)
//            product = new ProductDTO(_ic.getProduct ());
//
//        if(_ic.getWarehouse () != null)
//            warehouse = new WarehouseDTO(_ic.getWarehouse ());
//        incomingDate = _ic.getIncomingDate ();
//        refType = _ic.getRefType ();
//        refId = _ic.getRefId ();
//        memo = _ic.getMemo ();
//        inQty = _ic.getQty ();
//        outQty = _ic.getUnitPrice ();
//    }

    /**
     * @return the product
     */
    public Product getProduct ()
    {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct (Product product)
    {
        this.product = product;
    }

    /**
     * @return the inQty
     */
    public double getInQty ()
    {
        return inQty;
    }

    /**
     * @param qty the inQty to set
     */
    public void setInQty (double qty)
    {
        this.inQty = qty;
    }

    /**
     * @return the outQty
     */
    public double getOutQty ()
    {
        return outQty;
    }

    /**
     * @param unitPrice the outQty to set
     */
    public void setOutQty (double unitPrice)
    {
        this.outQty = unitPrice;
    }

    /**
     * @return the beginningBalance
     */
    public double getBeginningBalance ()
    {
        return beginningBalance;
    }

    /**
     * @param beginningBalance the beginningBalance to set
     */
    public void setBeginningBalance (double beginningBalance)
    {
        this.beginningBalance = beginningBalance;
    }

    /**
     * @return the beginningBalance
     */
    public double getBalance ()
    {
        return beginningBalance + (inQty - outQty);
    }
}
