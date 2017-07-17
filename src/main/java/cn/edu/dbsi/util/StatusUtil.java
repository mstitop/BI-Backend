package cn.edu.dbsi.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/7/15.
 */
public class StatusUtil {

    public static ResponseEntity<?> error(String code, String message){
        Map<String,Object> map = new HashMap<String, Object>();
        Map<String,Object> error = new HashMap<String, Object>();
        error.put("code",code);
        error.put("message",message);
        map.put("error",error);
        return new ResponseEntity<Object>(map, HttpStatus.valueOf(400));
    }

    public static ResponseEntity<?> updateOk(){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",true);
        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

    public static ResponseEntity<?> querySuccess(Object obj){
        return new ResponseEntity<Object>(obj,HttpStatus.OK);
    }
}
