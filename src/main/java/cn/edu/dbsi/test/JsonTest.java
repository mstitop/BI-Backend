package cn.edu.dbsi.test;

import cn.edu.dbsi.dataetl.util.DataXJobJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Skye on 2017/8/5.
 */
public class JsonTest {
    public static void main(String[] args) {
/*
        JSONObject obj = new JSONObject();
        obj.put("name","testName");
        obj.put("id",123);
        JSONObject obj2 = new JSONObject();
        obj2.put("colName","");
        obj2.put("colType","String");
        String nullStr = new String();
        obj2.put("testNull",nullStr);
        obj.put("column",obj2);
        JSONArray arr = new JSONArray();
        List<Object> primary_key = new ArrayList<Object>();
        //primary_key.add("test");
       // primary_key.add("xkh");
        JSONObject obj3 = new JSONObject();
        obj3.put("type","inner");
        obj3.put("primary_key",primary_key);
        arr.put(obj3);

        JSONObject obj4 = new JSONObject();
        obj4.put("type","inner");
        obj4.put("primary_key",primary_key);
        arr.put(obj4);
        obj.put("join",arr);
        obj.put("successful",true);
        String json = obj.toString();
        JSONObject cubeObj = new JSONObject();
        cubeObj.put("project","game_inner");
        cubeObj.put("cubeName","test_cube8");
        cubeObj.put("cubeDesc",json);
        System.out.println(cubeObj);
        System.out.println(obj);
        System.out.println(json);

        JSONObject testSucess = new JSONObject(json);
        if (testSucess.getBoolean("successful")){
            System.out.println("yes");
        }
        if (testSucess.getString("exception") != null){
            System.out.println("no");
        }
        */
        /*String kylin_url = "KYLIN_URL=http://10.1.18.211:7070/kylin";
        int index1 = kylin_url.indexOf("//");
        int index2 = kylin_url.lastIndexOf(":");
        System.out.println(index1 + " " + index2);
        String hostname = kylin_url.substring(index1 + 2, index2);
        System.out.println(hostname);
        int index3 = kylin_url.lastIndexOf("/");
        String port = kylin_url.substring(index2 + 1, index3);
        System.out.println(port);*/

        String db = "bi_1.grsfdf";


        String re = db.split("[.]")[1].toUpperCase();
        System.out.printf(re);

        JSONObject transformer = new JSONObject();
        transformer.put("name","dx_filter");
        JSONObject parameter = new JSONObject();
        parameter.put("columnIndex",1);
        List<String> list = new ArrayList<String>();
        list.add("=");
        list.add("null");
        parameter.put("paras",list);
        transformer.put("testNUll",new JSONObject());
        transformer.put("parameter",parameter);

        System.out.println(transformer);

        System.out.println(transformer.toString());

        JSONObject transformer2 = new JSONObject(transformer.toString());
        JSONObject pa = transformer2.getJSONObject("parameter");
        pa.put("testPara","test");


        System.out.println(transformer2);
        if (transformer2.isNull("testNUll")){
            System.out.println("yes");
        }else {
            System.out.println("no");
        }
        DataXJobJson dataXJobJson = new DataXJobJson();
        //String temple = dataXJobJson.getTemplate("Mysql");
        //System.out.println(temple);

       // JSONObject templeJ =  new JSONObject(temple);
       // System.out.println(templeJ.toString());

        List<Object> primary_key = new ArrayList<Object>();
        primary_key.add("test");
        primary_key.add("xkh");
        JSONObject obj3 = new JSONObject();
        obj3.put("type","inner");
        obj3.put("primary_key",primary_key);

        System.out.println(obj3);
        JSONArray arr = obj3.getJSONArray("primary_key");
        System.out.println(arr.getString(0));
    }
}
