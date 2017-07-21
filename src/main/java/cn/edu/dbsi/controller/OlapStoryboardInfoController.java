package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.OlapStoryboardElementInfo;
import cn.edu.dbsi.model.OlapStoryboardInfo;
import cn.edu.dbsi.service.OlapStoryboardElementInfoServiceI;
import cn.edu.dbsi.service.OlapStoryboardInfoServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by 郭世明 on 2017/7/17.
 */
@LoginRequired
@RestController
@RequestMapping(value = "/rest")
public class OlapStoryboardInfoController {

    @Autowired
    private OlapStoryboardInfoServiceI olapStoryboardInfoServiceI;

    @Autowired
    private OlapStoryboardElementInfoServiceI olapStoryboardElementInfoServiceI;


    /**
     * 查询全部 OLAP 分析故事板信息
     *
     * @return
     */
    @RequestMapping(value = "/olap-storyboards", method = RequestMethod.GET)
    public ResponseEntity<?> getAllOlapStoryboardInfo() {
        List<OlapStoryboardInfo> list = olapStoryboardInfoServiceI.getAllOlapStoryboardInfo();
        if (list != null) {
            return StatusUtil.querySuccess(list);
        } else {
            return StatusUtil.error("", "获取故事板信息失败！");
        }
    }

    /**
     * 查询单个 OLAP 分析故事板的信息
     *
     * @param storyboardId
     * @return
     */
    @RequestMapping(value = "/olap-storyboard/{storyboardId}", method = RequestMethod.GET)
    public ResponseEntity<?> getOlapStoryboardInfoById(@PathVariable("storyboardId") Integer storyboardId) {
        Map<String,Object> map = new HashMap<String, Object>();
        OlapStoryboardInfo olapStoryboardInfo = olapStoryboardInfoServiceI.getOlapStoryboardInfoById(storyboardId);
        List<OlapStoryboardElementInfo> list = olapStoryboardElementInfoServiceI.getOlapStoryboardElementInfoByStoryBoardId(storyboardId);
        if (list != null) {
            map.put("name",olapStoryboardInfo.getName());
            map.put("items",list);
            return StatusUtil.querySuccess(map);
        } else {
            return StatusUtil.error("", "此故事板信息为空！");
        }
    }

    /**
     * 新增OLAP 分析故事板信息
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/olap-storyboard", method = RequestMethod.POST)
    public ResponseEntity<?> addOlapStoryboardInfo(@RequestBody Map<String, Object> json) {
        JSONObject obj = new JSONObject(json);
        OlapStoryboardInfo olapStoryboardInfo = new OlapStoryboardInfo();
        OlapStoryboardElementInfo olapStoryboardElementInfo = new OlapStoryboardElementInfo();
        String storyBoardName = obj.getString("name");
        olapStoryboardInfo.setName(storyBoardName);
        olapStoryboardInfo.setIsDelete("0");
        olapStoryboardInfoServiceI.addOlapStoryboardInfo(olapStoryboardInfo);
        int lastOlapStoryboardInfoId = olapStoryboardInfoServiceI.getLastOlapStoryboardInfoPrimaryKey();
        olapStoryboardElementInfo.setStoryboardId(lastOlapStoryboardInfoId);
        olapStoryboardElementInfo.setIsDelete("0");
        JSONArray storyboards = obj.getJSONArray("storyboardItems");
        int tag = 0;
        for (Object storyboard : storyboards) {
            JSONObject temp = (JSONObject) storyboard;
            String name = temp.getString("name");
            String saikuId = temp.getString("saikuId");
            String saikuName = temp.getString("saikuName");
            String saikuPath = temp.getString("saikuPath");
            int index = temp.getInt("index");
            int pointX = temp.getInt("pointX");
            int pointY = temp.getInt("pointY");
            int width = temp.getInt("width");
            int height = temp.getInt("height");
            olapStoryboardElementInfo.setName(name);
            olapStoryboardElementInfo.setSaikuId(saikuId);
            olapStoryboardElementInfo.setSaikuName(saikuName);
            olapStoryboardElementInfo.setSaikuPath(saikuPath);
            olapStoryboardElementInfo.setIndex(index);
            olapStoryboardElementInfo.setPointX(pointX);
            olapStoryboardElementInfo.setPointY(pointY);
            olapStoryboardElementInfo.setWidth(width);
            olapStoryboardElementInfo.setHeight(height);
            tag = olapStoryboardElementInfoServiceI.addOlapStoryboardElementInfo(olapStoryboardElementInfo);
        }
        if (tag == 1) {
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "新增故事板失败！");
        }
    }

    /**
     * 删除指定OLAP 分析故事板
     *
     * @param storyboardId
     * @return
     */
    @RequestMapping(value = "/olap-storyboard/{storyboardId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteOlapStoryboardInfoById(@PathVariable("storyboardId") Integer storyboardId) {
        int tag = olapStoryboardInfoServiceI.deleteOlapStoryboardInfo(storyboardId);
        if (tag == 1) {
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "删除故事板失败");
        }
    }

        @RequestMapping(value = "/olap-storyboard/{storyboardId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateOlapStoryboardInfoById(@PathVariable("storyboardId") Integer storyboardId, @RequestBody Map<String, Object> json) {
        OlapStoryboardInfo olapStoryboardInfo = new OlapStoryboardInfo();
        OlapStoryboardElementInfo olapStoryboardElementInfo = new OlapStoryboardElementInfo();
        Set<Integer> set = new HashSet<Integer>();
        olapStoryboardInfo.setId(storyboardId);
        olapStoryboardInfo.setIsDelete("0");
        JSONObject obj = new JSONObject(json);
        String boardName = obj.getString("name");
        olapStoryboardInfo.setName(boardName);
        olapStoryboardElementInfo.setStoryboardId(storyboardId);
        olapStoryboardElementInfo.setIsDelete("0");
        int tag = olapStoryboardInfoServiceI.updateOlapStoryboardInfo(olapStoryboardInfo);
        int tag2 = 0, tag3 = 0, tag4 = 0;
        List<OlapStoryboardElementInfo> dbList = olapStoryboardElementInfoServiceI.getOlapStoryboardElementInfoByStoryBoardId(storyboardId);
        JSONArray storyboardItems = obj.getJSONArray("storyboardItems");
        for (Object item : storyboardItems) {
            JSONObject temp = (JSONObject) item;
            int itemId = temp.getInt("id");
            String name = temp.getString("name");
            String saikuId = temp.getString("saikuId");
            String saikuName = temp.getString("saikuName");
            String saikuPath = temp.getString("saikuPath");
            int index = temp.getInt("index");
            int pointX = temp.getInt("pointX");
            int pointY = temp.getInt("pointY");
            int width = temp.getInt("width");
            int height = temp.getInt("height");
            olapStoryboardElementInfo.setName(name);
            olapStoryboardElementInfo.setSaikuId(saikuId);
            olapStoryboardElementInfo.setSaikuName(saikuName);
            olapStoryboardElementInfo.setSaikuPath(saikuPath);
            olapStoryboardElementInfo.setIndex(index);
            olapStoryboardElementInfo.setPointX(pointX);
            olapStoryboardElementInfo.setPointY(pointY);
            olapStoryboardElementInfo.setWidth(width);
            olapStoryboardElementInfo.setHeight(height);
            if (-1 == itemId) {
                olapStoryboardElementInfo.setId(null);
                tag2 = olapStoryboardElementInfoServiceI.addOlapStoryboardElementInfo(olapStoryboardElementInfo);
            } else {
                olapStoryboardElementInfo.setId(itemId);
                tag3 = olapStoryboardElementInfoServiceI.updateOlapStoryboardElementInfo(olapStoryboardElementInfo);
                set.add(itemId);
            }
        }
        for (OlapStoryboardElementInfo elementInfo : dbList) {
                if (!set.contains(elementInfo.getId())) {
                    tag4 = olapStoryboardElementInfoServiceI.deleteOlapStoryboardElementInfo(elementInfo);
                }
        }
        if (tag == 1 && (tag2 == 1 || tag3 == 1)) {
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "更新故事板失败！");
        }
    }

}
