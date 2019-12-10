package com.trasier.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceInfo {
    private String id;
    private Long startTimestamp;
    private Long endTimestamp;
    private Map<String, String> labels = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public List<SpanInfo> getSpans() {
        return spans;
    }

    public void setSpans(List<SpanInfo> spans) {
        this.spans = spans;
    }

    private List<SpanInfo> spans = new ArrayList<>();
}