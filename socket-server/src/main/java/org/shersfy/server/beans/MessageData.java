package org.shersfy.server.beans;

/**
 * 消息结构
 * @author py
 * 2018年7月6日
 */
public class MessageData extends BaseBean{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int code;
    private String data;

    public MessageData() {
        super();
    }
    
    public MessageData(String data) {
        super();
        this.data = data;
    }
    
    public MessageData(int code, String data) {
        super();
        this.code = code;
        this.data = data;
    }
    
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
}
