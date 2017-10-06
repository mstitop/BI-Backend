package cn.edu.dbsi.test;

import cn.edu.dbsi.util.HttpConnectDeal;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("file","/datasources/table.saiku");
        map.put("formatter","flattened");
        String response = HttpConnectDeal.post("http://10.1.18.205:8080/saiku/rest/saiku/api/query/getmdx",map);
        System.out.println(response);
    }
}
