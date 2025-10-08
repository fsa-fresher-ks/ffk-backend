package fsa.fresher.pos.api.dto;

import java.util.List;

public class PageMetadataDto {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<String> sort;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public List<String> getSort() { return sort; }
    public void setSort(List<String> sort) { this.sort = sort; }
}
