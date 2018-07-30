package org.shersfy.server.beans;

public class Result extends BaseBean{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private int code = ResultCode.OK;
    private String msg;
    private Object model;
    
    public Result() {
    }
    
    public Result(Object model) {
        super();
        this.model = model;
    }
    
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getModel() {
        return model;
    }
    public void setModel(Object model) {
        this.model = model;
    }
}
