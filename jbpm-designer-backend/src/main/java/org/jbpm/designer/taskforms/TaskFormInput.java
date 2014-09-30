package org.jbpm.designer.taskforms;

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
    private boolean integerRefType;
    private boolean dateRefType;
    private boolean floatRefType;

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
        if(refType != null) {
            this.booleanRefType = this.refType.equals("Boolean");
            this.integerRefType = this.refType.equals("Integer");
            this.dateRefType = this.refType.equals("Date");
            this.dateRefType = this.refType.equals("Float");
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
    
    public boolean isIntegerRefType() {
        return integerRefType;
    }

    public void setIntegerRefType(boolean integerRefType) {
        this.integerRefType = integerRefType;
    }

    public boolean isDateRefType() {
        return dateRefType;
    }

    public void setDateRefType(boolean dateRefType) {
        this.dateRefType = dateRefType;
    }
    
    public boolean isFloatRefType() {
        return floatRefType;
    }

    public void setFloatRefType(boolean floatRefType) {
        this.floatRefType = floatRefType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
