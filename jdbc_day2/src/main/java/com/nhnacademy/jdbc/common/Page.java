package com.nhnacademy.jdbc.common;

import java.util.List;

public class Page<T> {
    //todo#1  Page class, content: 페이징 처리된 content , totalCount: 전체 행 갯수
    private final List<T> content;
    private final long totalCount;

    public Page(List<T> content, long totalCount) {
        this.content = content;
        this.totalCount = totalCount;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalCount() {
        return totalCount;
    }

}