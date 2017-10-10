package cn.edu.dbsi.olaprecommend.util;

import cn.edu.dbsi.olaprecommend.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OlapRecommendUtil {

    //α,β,γ参数设置
    private static double ALPHA = 0.8, BETA = 0.1, GAMA = 0.1;

    /**
     * 获取查询语句对应的各个成员
     *
     * @param query
     * @return
     */
    public static List<Set<String>> getMembers(String query) {
        Set<String> gbsSet = new LinkedHashSet<String>();
        Set<String> measureSet = new LinkedHashSet<String>();
        Set<String> preSet = new LinkedHashSet<String>();
        List<Set<String>> returnList = new ArrayList<Set<String>>();

        String pattern = "[^\\.]\\[[\\u4e00-\\u9fa5a-zA-Z-0-9]*\\]\\.\\[[\\u4e00-\\u9fa5a-zA-Z-0-9]*\\]";

        String pattern2 = "\\[[\\u4e00-\\u9fa5a-zA-Z-0-9]*\\](\\.\\[[\\u4e00-\\u9fa5a-zA-Z-0-9]*\\]){0,}";

        String[] querySplit = query.split("WHERE");
        String queryPart2 = "";
        if (querySplit.length == 2){
            queryPart2 += querySplit[1];
        }
        if (querySplit[0].contains("[~FILTER]")) {
            String filterTemp = querySplit[0].split("\\[~FILTER\\]")[1];
            System.out.println("filterTemp: " + filterTemp);
            String filter = "";
            if (filterTemp.contains("SET")) {
                filter = filterTemp.split("SET")[0];
                querySplit[0] = filterTemp.substring(filterTemp.indexOf("SET"));
            } else {
                filter = filterTemp.split("SELECT")[0];
                querySplit[0] = filterTemp.substring(filterTemp.indexOf("SELECT"));
            }
            queryPart2 += filter;
        }
        System.out.println("querySplit0: " + querySplit[0]);
        System.out.println("queryPart2: " + queryPart2);
        // 获取  group by  和 measure 子句
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(querySplit[0]);
        while (m.find()) {
            String field = m.group(0).substring(1);
            String tempField = field.replace("[", "");
            tempField = tempField.replace("]", "");

            if (tempField.split("\\.").length != 2)
                continue;

            if (tempField.contains("Measures")) {
                measureSet.add(tempField.substring(9));
            } else {
                gbsSet.add(tempField);
            }
            System.out.println(tempField);
        }

        System.out.println("------------------");

        // 获取 predicate 子句
        Pattern r2 = Pattern.compile(pattern2);
        Matcher m1 = r2.matcher(queryPart2);
        while (m1.find()) {
            String field = m1.group(0);
            String tempField = field.replace("[", "");
            tempField = tempField.replace("]", "");
            String[] tempStrs = tempField.split("\\.");
            if (tempStrs.length != 3) {
                continue;
            } else if (tempStrs[1].equals(tempStrs[2])) {
                continue;
            }

            preSet.add(tempField);
            System.out.println(tempField);
        }

        returnList.add(gbsSet);
        returnList.add(measureSet);
        returnList.add(preSet);

        return returnList;
    }

    public static String getCubeName(String query) {
        String cubeName = "";
        int start = query.indexOf("FROM") + 4;
        start = query.indexOf("[", start) + 1;
        int end = query.indexOf("]", start);

        cubeName = query.substring(start, end);

        return cubeName;
    }

    public static Query getQuery(List<Set<String>> mdxMembers) {
        Set<String> gbsSet = mdxMembers.get(0);
        Set<String> measureSet = mdxMembers.get(1);
        Set<String> preSet = mdxMembers.get(2);
        Query query = new Query();
        for (String gbsStr : gbsSet) {
            query.addGroupByElement(gbsStr.split("\\.")[0].trim(), gbsStr.split("\\.")[1].trim());
        }
        for (String measStr : measureSet) {
            Measure meas = new Measure(measStr.trim());
            query.addMeasure(meas);
        }
        for (String preStr : preSet) {
            SelectionPredicate selectionPredicate = new SelectionPredicate(preStr.split("\\.")[0].trim(), preStr.split("\\.")[1].trim(), preStr.split("\\.")[2].trim(), false, false);
            query.addSelectionPredicate(selectionPredicate);
        }

        return query;

    }

    public static Double getSimilarity(List<Set<String>> firstMdxMembers, List<Set<String>> secondMdxMembers, Cube cube) {
        Query query1 = getQuery(firstMdxMembers);
        Query query2 = getQuery(secondMdxMembers);
        double similarity = 0;
        similarity = ALPHA * gbs(query1, query2, cube) + BETA * sel(query1, query2, cube) + GAMA * meas(query1, query2);
        System.out.println("similarity: " + similarity);
        return similarity;
    }

    public static double sel(Query query1, Query query2, Cube cube) {

        Map<String, Integer> dimensinTypeMap = new HashMap<String, Integer>();
        Map<Integer, String> reverseTypeMap = new HashMap<Integer, String>();

        List<Dimension> dimensions = cube.getDimensions();
        for (int i = 0; i < dimensions.size(); i++) {
            dimensinTypeMap.put(dimensions.get(i).getName(), i);
            reverseTypeMap.put(i, dimensions.get(i).getName());
        }
        int DIMENSION_LENGTH = dimensions.size();

        double result = 0;
        Query q1 = query1;
        Query q2 = query2;
        SelectionPredicate[] sel1 = new SelectionPredicate[DIMENSION_LENGTH];
        SelectionPredicate[] sel2 = new SelectionPredicate[DIMENSION_LENGTH];

        List<SelectionPredicate> pre1 = q1.getPredicates();
        int count = 0;
        Integer pos = DIMENSION_LENGTH;
        for (int i = 0; i < sel1.length; i++) {
            if (count < pre1.size()) {
                String hierachy = pre1.get(count).getHierarchy();
                pos = dimensinTypeMap.get(hierachy);
            } else {
                pos = DIMENSION_LENGTH;
            }
            if (pos != i)
                sel1[i] = new SelectionPredicate(reverseTypeMap.get(i), reverseTypeMap.get(i), "", true, true);
            else
                sel1[i] = pre1.get(count++);
        }
        count = 0;
        pos = DIMENSION_LENGTH;
        List<SelectionPredicate> pre2 = q2.getPredicates();
        for (int i = 0; i < sel2.length; i++) {
            if (count < pre2.size()) {
                String hierachy = pre2.get(count).getHierarchy();
                pos = dimensinTypeMap.get(hierachy);
            } else {
                pos = DIMENSION_LENGTH;
            }
            if (pos != i)
                sel2[i] = new SelectionPredicate(reverseTypeMap.get(i), reverseTypeMap.get(i), "", true, true);
            else
                sel2[i] = pre2.get(count++);
        }

        for (int i = 0; i < sel2.length; i++) {
            double distance = 0;
            String hierarchyName = sel1[i].getHierarchy();
            if (sel1[i].getElement().equals("") && sel2[i].getElement().equals(""))
                distance = 0;
            else if (!(sel1[i].getElement().equals("")) || !(sel2[i].getElement().equals(""))) {

                if ((q1.getVisibility(hierarchyName)) &&
                        (q2.getVisibility(hierarchyName))) {
                    Hierarchy tempHie = cube.findHierarchy(hierarchyName);
                    int position_1 = tempHie.findPosition(q1.findLevel(sel1[i].getHierarchy()));
                    int position_2 = tempHie.findPosition(q2.findLevel(sel2[i].getHierarchy()));

                    distance = Math.abs(position_1 - position_2);
                }
                if (!(sel1[i].getElement().equals("")) && !(sel2[i].getElement().equals(""))) {

                } else {
                    distance++;
                }
            }
            if (distance != 0)
                distance += 1;
            Hierarchy tempHie = cube.findHierarchy(hierarchyName);
            result += distance / (tempHie.getLevelCount());
        }
        return (1 - result / q1.getGroupBySet().size());
    }

    public static double gbs(Query q1, Query q2, Cube cube) {
        double result = 0;
        for (GroupByElement gb2 : q2.getGroupBySet()) {
            double distance = 0;
            String hierarchyName = gb2.getHierarchy();

            if ((q1.getVisibility(hierarchyName)) &&
                    (q2.getVisibility(hierarchyName))) {
                Hierarchy tempHie = cube.findHierarchy(hierarchyName);
                int position_1 = tempHie.findPosition(q1.findLevel(hierarchyName));
                int position_2 = tempHie.findPosition(q2.findLevel(hierarchyName));

                distance = Math.abs(position_1 - position_2);
            }
            Hierarchy tempHie = cube.findHierarchy(hierarchyName);
            if (tempHie.getLevelCount() == 1)
                result += distance / (tempHie.getLevelCount());
            else
                result += distance / (tempHie.getLevelCount() - 1);
        }
        return (1 - result / q1.getGroupBySet().size());
    }

    private static double meas(Query query1, Query query2) {
        List<Measure> list1 = query1.getMeasures();
        List<Measure> list2 = query2.getMeasures();

        list1.removeAll(list2);
        list1.addAll(list2);
        double union = list1.size();
        list1 = query1.getMeasures();
        list2 = query2.getMeasures();

        if (union == 0.0)
            union = 1.0;
        list1.retainAll(list2);
        return list2.size() / union;
    }
}
