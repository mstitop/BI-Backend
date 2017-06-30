package cn.edu.dbsi.model;

import java.util.List;

public class SchemaMeasure {
    private Integer id;

    private String name;

    private String fieldName;

    private String aggregator;

    private String formatStyle;

    private Integer measureGroupId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName == null ? null : fieldName.trim();
    }

    public String getAggregator() {
        return aggregator;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator == null ? null : aggregator.trim();
    }

    public String getFormatStyle() {
        return formatStyle;
    }

    public void setFormatStyle(String formatStyle) {
        this.formatStyle = formatStyle == null ? null : formatStyle.trim();
    }

    public Integer getMeasureGroupId() {
        return measureGroupId;
    }

    public void setMeasureGroupId(Integer measureGroupId) {
        this.measureGroupId = measureGroupId;
    }

}