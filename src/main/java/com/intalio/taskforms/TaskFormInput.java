package com.intalio.taskforms;

/** 
 * Holds info for one task form input.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormInput {
    private String name;
    private String value;
    private String refType;
    private boolean booleanRefType;

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
        if(refType != null) {
            this.booleanRefType = this.refType.equals("Boolean");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBooleanRefType() {
        return booleanRefType;
    }

    public void setBooleanRefType(boolean booleanRefType) {
        this.booleanRefType = booleanRefType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
