package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.SaikuMDX;
import cn.edu.dbsi.model.SchemaDimension;
import cn.edu.dbsi.model.SchemaDimensionAttribute;
import cn.edu.dbsi.olaprecommend.model.Cube;
import cn.edu.dbsi.olaprecommend.model.Dimension;
import cn.edu.dbsi.olaprecommend.model.Hierarchy;
import cn.edu.dbsi.olaprecommend.model.Level;
import cn.edu.dbsi.olaprecommend.util.OlapRecommendUtil;
import cn.edu.dbsi.service.CubeInfoServiceI;
import cn.edu.dbsi.service.SaikuMDXServiceI;
import cn.edu.dbsi.service.SchemaDimensionAttributeServiceI;
import cn.edu.dbsi.service.SchemaDimensionServiceI;
import cn.edu.dbsi.util.HttpConnectDeal;
import cn.edu.dbsi.util.StatusUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping(value = "/rest")
public class OlapRecommendController {
    @Autowired
    private SaikuMDXServiceI saikuMDXServiceI;
    @Autowired
    private CubeInfoServiceI cubeInfoServiceI;
    @Autowired
    private SchemaDimensionServiceI schemaDimensionServiceI;
    @Autowired
    private SchemaDimensionAttributeServiceI schemaDimensionAttributeServiceI;

    @RequestMapping(value = "/olap/recommend", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> recommendOlap(@RequestBody Map<String, Object> json) {
        Map<String, Object> result = new HashMap<String, Object>();
        int lastKey = saikuMDXServiceI.selectLastPrimaryKey();
        SaikuMDX saikuMDX = saikuMDXServiceI.selectByPrimaryKey(lastKey);

        List<Set<String>> lastMdxMembers = OlapRecommendUtil.getMembers(saikuMDX.getMdx());
        String lastCubeName = OlapRecommendUtil.getCubeName(saikuMDX.getMdx());

        // get cube
        int schemaId;
        try{
            schemaId = cubeInfoServiceI.getSchemaIdByCubeName(lastCubeName);
        }catch (Exception e){
            e.printStackTrace();
            return StatusUtil.error("",lastCubeName + " has not been saved ");
        }
        List<SchemaDimension> schemaDimensions = schemaDimensionServiceI.getAllDimensionBySchemaId(schemaId);
        Map<String,List<String>> cubeMap  = new HashMap<String, List<String>>();
        for (int j = 0; j < schemaDimensions.size(); j++) {

        //for (SchemaDimension schemaDimension;schemaDimensions){
            List<SchemaDimensionAttribute>  schemaDimensionAttributes =  schemaDimensionAttributeServiceI.getAllAttributeByDimensionId(schemaDimensions.get(j).getId());
            List<String> attributes = new ArrayList<String>();
            for (SchemaDimensionAttribute schemaDimensionAttribute:schemaDimensionAttributes){
                attributes.add(schemaDimensionAttribute.getName());
            }
            cubeMap.put(schemaDimensions.get(j).getName(),attributes);
        }

        Cube cube = createCube(lastCubeName,cubeMap);


        JSONObject obj = new JSONObject(json);
        JSONArray mdxNames = obj.getJSONArray("saikuFilePaths");

        Map<String,String> candidateMdxMap = new HashMap<String, String>();
        Map<String,Double> similarityMap = new LinkedHashMap<String, Double>();

        for (int i = 0;i < mdxNames.length();i++){
            String filePath = mdxNames.getString(i);
            // 获取文件对应的 mdx 语句
            Map<String,String> map = new HashMap<String, String>();
            map.put("file",filePath);
            map.put("formatter","flattened");
            String mdx;
            try {
                String response = HttpConnectDeal.post("http://10.1.18.205:8080/saiku/rest/saiku/api/query/getmdx", map);
                JSONObject jsonObject = new JSONObject(response);
                mdx = jsonObject.getString("mdx");
            }catch (Exception e){
                e.printStackTrace();
                return StatusUtil.error("","get saiku mdx from [" + filePath + "] error");
            }
            String cubeName = OlapRecommendUtil.getCubeName(mdx);
            if (!cubeName.equals(lastCubeName))
                continue;

            List<Set<String>> mdxMembers = OlapRecommendUtil.getMembers(mdx);
            // 求相似度，存储


            double similarity = OlapRecommendUtil.getSimilarity(mdxMembers,lastMdxMembers,cube);
            BigDecimal bigDecimal = new BigDecimal(similarity);
            similarity = bigDecimal.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

            candidateMdxMap.put(filePath,mdx);
            similarityMap.put(filePath,similarity);
        }
        //这里将map.entrySet()转换成list
        List<Map.Entry<String,Double>> list = new ArrayList<Map.Entry<String,Double>>(similarityMap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
            //降序排序
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        List<Map<String,Object>> tempList = new ArrayList<Map<String,Object>>();
        int count = 0;
        for(Map.Entry<String,Double> mapping:list){
            Map<String,Object> temp = new HashMap<String,Object>();
            System.out.println(mapping.getKey()+" : "+mapping.getValue());
            temp.put("filePath",mapping.getKey());
            temp.put("mdx",candidateMdxMap.get(mapping.getKey()));
            temp.put("similarity",mapping.getValue());
            tempList.add(temp);
            count++;
            if(count > 5)
                break;
        }
        result.put("recommendMdxs",tempList);
        return StatusUtil.querySuccess(result);
    }

    private Cube createCube(String cubeName,Map<String,List<String>> cubeMap){
        Cube cube = new Cube(cubeName);
        if (cubeMap.size() > 0){
            for (Map.Entry<String,List<String>> entry:cubeMap.entrySet()){
                Dimension dimension = new Dimension(entry.getKey());
                Hierarchy hierarchy = new Hierarchy(entry.getKey());
                for (String string:entry.getValue()){
                    Level level = new Level(string);
                    hierarchy.addLevel(level);
                }
                dimension.addHierarchy(hierarchy);
                cube.addDimension(dimension);
            }
        }
        return cube;
    }

}
