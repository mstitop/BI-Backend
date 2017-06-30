package cn.edu.dbsi.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/29.
 */
public class JsonUtil {

    /**
     * 1.
     * 将JavaBean转换成JSONObject（通过Map中转）
     */
    public static JSONObject toJSON(Object bean) {
        return new JSONObject(toMap(bean));
    }

    /**
     * 1.1
     * 将Javabean转换为Map
     * 逻辑：1.根据Javabean获取它所有的方法，并且遍历它，当为get方法时，调用这个方法获取value
     *     2.通过get方法名获取key，将key和value放入map中
     */
    public static Map toMap(Object javaBean) {
        Map result = new HashMap();
        Method[] methods = javaBean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("get")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    Object value = method.invoke(javaBean, (Object[]) null);//不用反射 你没办法调用getName、getAge等方法    反射get方法参数：(Object[]) null)  即为空的表示形式
                    result.put(field, null == value ? "" : value.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 2.
     * JSONObject到JavaBean
     */
    public static void toJavaBean(Object javabean, String jsonString)
            throws ParseException, JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);//json对象
        Map map = toMap(jsonObject.toString());//将json对象转换成map
        toJavaBean(javabean, map);//将map转换成javabean
    }

    /**
     * 2.1
     * 将Json对象转换成Map
     * 逻辑：就是遍历json对象的keys，再由key获取对应value，将其放入map中！
     */
    public static Map toMap(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();//直接获取jsonObject键的迭代器
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();//1.
            value = jsonObject.getString(key);//2.
            result.put(key, value);
        }
        return result;
    }
    /**
     * 2.2
     * 将Map转换成Javabean
     * 逻辑：1.根据要转化的javabean获取方法并且遍历它，当是set方法时，调用其方法，
     *     2.参数通过map获取，key从这个set方法中截取
     */
    public static Object toJavaBean(Object javabean, Map map) {
        Method[] methods = javabean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.getName().startsWith("set")) {
                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(javabean, new Object[] {map.get(field)});//不用反射 你没办法调用setName、setAge等方法    反射set方法的参数：new Object[] {map.get(field)}
                }
            } catch (Exception e) {
            }
        }
        return javabean;
    }
}
