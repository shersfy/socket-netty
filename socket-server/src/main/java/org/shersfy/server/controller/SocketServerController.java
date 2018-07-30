package org.shersfy.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
public class SocketServerController extends BaseController{

    private JSONObject status;
    
    public SocketServerController() {
        this.status = new JSONObject();
        this.status.put("status", "UP");
    }

    /**
     * 健康监测
     * @return
     */
    @RequestMapping(value="/health.json", method=RequestMethod.GET)
    @ResponseBody
    public JSONObject health(){
        return status;
    }

}
