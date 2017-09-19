package cn.edu.dbsi.test;

import cn.edu.dbsi.dataetl.util.JobConfig;
import cn.edu.dbsi.util.HttpConnectDeal;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Skye on 2017/8/6.
 */
public class KylinTest {
    public static void main(String[] args){
        /*JSONObject cubeObj = new JSONObject();
        cubeObj.put("project","game_inner");
        cubeObj.put("cubeName","test_cube8");
        cubeObj.put("cubeDescData","{  \"name\": \"test_cube8\",  \"model_name\": \"test_game_model\",  \"dimensions\": [    {      \"name\": \"STARTTIME\",      \"table\": \"KYLIN_FLAT_DB.FACT_GAME\",      \"column\": \"STARTTIME\"    },    {      \"name\": \"USERID\",      \"table\": \"KYLIN_FLAT_DB.FACT_GAME\",      \"column\": \"USERID\"    },    {      \"name\": \"GAME\",      \"table\": \"KYLIN_FLAT_DB.FACT_GAME\",      \"column\": \"GAME\"   },    {      \"name\": \"TYPEID\",      \"table\": \"KYLIN_FLAT_DB.FACT_GAME\",      \"column\": \"TYPEID\" },    {      \"name\": \"DURATION\",      \"table\": \"KYLIN_FLAT_DB.FACT_GAME\",      \"column\": \"DURATION\"    },    {      \"name\": \"LOCATIONID\",      \"table\": \"KYLIN_FLAT_DB.DIM_LOCATION\",      \"derived\": [        \"LOCATIONID\"      ]    }  ],  \"measures\": [    {      \"name\": \"_COUNT_\",      \"function\": {        \"expression\": \"COUNT\",        \"parameter\": {          \"type\": \"constant\",          \"value\": \"1\",          \"next_parameter\": null        },        \"returntype\": \"bigint\"      },      \"dependent_measure_ref\": null    }  ],  \"rowkey\": {    \"rowkey_columns\": [      {        \"column\": \"STARTTIME\",        \"encoding\": \"dict\",        \"isShardBy\": false      },      {        \"column\": \"USERID\",        \"encoding\": \"dict\",        \"isShardBy\": false      },      {        \"column\": \"GAME\",        \"encoding\": \"dict\",        \"isShardBy\": false      },      {        \"column\": \"TYPEID\",        \"encoding\": \"dict\",        \"isShardBy\": false      },      {        \"column\": \"DURATION\",        \"encoding\": \"dict\",        \"isShardBy\": false      },      {        \"column\": \"LOCATIONID\",        \"encoding\": \"dict\",        \"isShardBy\": false      }    ]  },  \"hbase_mapping\": {    \"column_family\": [      {        \"name\": \"F1\",        \"columns\": [          {            \"qualifier\": \"M\",            \"measure_refs\": [              \"_COUNT_\"            ]          }        ]      }    ]  },  \"aggregation_groups\": [    {      \"includes\": [        \"STARTTIME\",        \"USERID\",        \"GAME\",        \"TYPEID\",        \"DURATION\",        \"LOCATIONID\"      ],      \"select_rule\": {        \"hierarchy_dims\": [],        \"mandatory_dims\": [],        \"joint_dims\": []      }    }  ],  \"notify_list\": [],  \"status_need_notify\": [    \"ERROR\",    \"DISCARDED\",    \"SUCCEED\"  ],  \"partition_date_start\": 0,  \"partition_date_end\": 3153600000000,  \"auto_merge_time_ranges\": [    604800000,    2419200000  ],  \"retention_range\": 0,  \"engine_type\": 2,  \"storage_type\": 2,  \"override_kylin_properties\": {}}");
        String response = HttpConnectDeal.postJson2Kylin("http://10.1.18.211:7070/kylin/api/cubes",cubeObj);
        System.out.println(response);*/


        JSONObject modelObj = new JSONObject();
        List list = new ArrayList();
        modelObj.put("list",list);
        modelObj.put("project","game_inner");
        modelObj.put("modelName","test_model5");
        modelObj.put("modelDescData","{\"name\": \"test_model5\",  \"owner\": \"ADMIN\",  \"description\": \"\",  \"fact_table\": \"DEFAULT.FACT_GAME\",  \"lookups\": [    {      \"table\": \"DEFAULT.DIM_LOCATION\",      \"join\": {        \"type\": \"inner\",        \"primary_key\": [          \"LOCATIONID\"        ],        \"foreign_key\": [          \"LOCATIONID\"        ]      }    }  ],  \"dimensions\": [    {      \"table\": \"DEFAULT.FACT_GAME\",      \"columns\": [        \"STARTTIME\",        \"USERID\",        \"GAME\",        \"TYPEID\",        \"DURATION\",        \"LOCATIONID\"      ]    },    {      \"table\": \"DEFAULT.DIM_LOCATION\",      \"columns\": [        \"LOCATIONID\",        \"LOCATION\"      ]    }  ],  \"metrics\": [    \"STARTTIME\",    \"USERID\",    \"GAME\"  ],  \"filter_condition\": \"\",  \"partition_desc\": {   \"partition_date_start\": 0,    \"partition_date_format\": \"yyyy-MM-dd\",    \"partition_time_format\": \"HH:mm:ss\",    \"partition_type\": \"APPEND\",    \"partition_condition_builder\": \"org.apache.kylin.metadata.model.PartitionDesc$DefaultPartitionConditionBuilder\"  },  \"capacity\": \"MEDIUM\"}");
       /* String response = HttpConnectDeal.postJson2Kylin("http://10.1.18.211:7070/kylin/api/models",modelObj);
        if (response == ""){
            System.out.println("yes");
        }
        JSONObject obj = new JSONObject(response);
        System.out.println(obj);
        System.out.println("response: "+response);*/

        //String content = HttpConnectDeal.postJson2Kylin("http://10.1.18.211:7070/kylin/api/tables/default.sample_07/BI_test", new JSONObject());
        //System.out.println(content);
//        System.out.println(modelObj);
//        int a = 1;
//        try {
//
//
//        if(modelObj.getJSONArray("li").isNull(0)){
//            System.out.println("yes");
//        }
//        }catch (JSONException e){
//            a = 0;
//        }
//        System.out.println(a);
//        System.out.println("done");
        //JSONArray jsonArray = modelObj.getJSONArray("li");
        //System.out.println(jsonArray.length());
//        String kylinBuildCubeApi = "http://10.1.18.211:7070/kylin/" + "/api/cubes/" + "test2" + "/rebuild";
          JobConfig jobConfig = new JobConfig();
//        JSONObject buildJson = new JSONObject();
//        buildJson.put("startTime","0");
//        buildJson.put("endTime","3153600000000");
//        buildJson.put("buildType","BUILD");
//        String buildResponse = HttpConnectDeal.putJson2Kylin(jobConfig,kylinBuildCubeApi,buildJson);

        String kylinGetCubeApi = "http://10.1.18.211:7070/kylin/" + "/api/cubes/" + "test2";
        String buildResponse = HttpConnectDeal.getFromKylin(jobConfig,kylinGetCubeApi);

    }
}
