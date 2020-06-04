package com.sunwell.pos.mobile.model;

/**
 * Created by sunwell on 10/30/17.
 */

public class AccessRight {

    public static final int TASK_VIEW_PRODUCT = 100;
    public static final int TASK_ADD_PRODUCT = 101;
    public static final int TASK_EDIT_PRODUCT = 102;
    public static final int TASK_VIEW_CATEGORY = 110;
    public static final int TASK_ADD_CATEGORY = 111;
    public static final int TASK_EDIT_CATEGORY = 112;

    public static final int TASK_VIEW_STOCK = 120;
    public static final int TASK_ADD_STOCK = 121;
    public static final int TASK_ADD_ADJUSTMENT = 122;
    public static final int TASK_VIEW_STAFF = 130;
    public static final int TASK_ADD_STAFF = 131;
    public static final int TASK_EDIT_STAFF = 131;

    public static final int TASK_VIEW_STAFF_TYPE = 140;
    public static final int TASK_ADD_STAFF_TYPE = 141;
    public static final int TASK_EDIT_STAFF_TYPE = 141;
    public static final int TASK_VIEW_CUSTOMER = 150;
    public static final int TASK_ADD_CUSTOMER = 151;
    public static final int TASK_EDIT_CUSTOMER = 152;
    public static final int TASK_DELETE_CUSTOMER = 153;

    public static final int TASK_INPUT_INVOICE = 160;
    public static final int TASK_VOID_INVOICE = 161;
    public static final int TASK_PRINT_INVOICE = 162;
    public static final int TASK_VIEW_PAYMENT = 170;
    public static final int TASK_ADD_PAYMENT = 171;
    public static final int TASK_EDIT_PAYMENT = 172;
    public static final int TASK_VIEW_REPORT = 400;

    public static final int TASK_VIEW_PREFERENCE = 700;
    public static final int TASK_EDIT_PREFERENCE = 701;;

    private Integer systemId;
    private String taskName;
    private String taskDisplayName;
    private String default1;
    private Boolean status;

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDisplayName() {
        return taskDisplayName;
    }

    public void setTaskDisplayName(String taskDisplayName) {
        this.taskDisplayName = taskDisplayName;
    }

    public String getDefault1() {
        return default1;
    }

    public void setDefault1(String default1) {
        this.default1 = default1;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object _obj) {
        if(! (_obj instanceof AccessRight))
            return false;

        AccessRight ac = (AccessRight) _obj;
        if(ac.getSystemId() != null && systemId == null)
            return  false;
        else if (ac.getSystemId() == null && systemId != null)
            return false;
        if(ac.getSystemId().equals(systemId))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return taskDisplayName;
    }
}
