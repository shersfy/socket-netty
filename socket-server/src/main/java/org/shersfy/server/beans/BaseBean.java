package org.shersfy.server.beans;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class BaseBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
