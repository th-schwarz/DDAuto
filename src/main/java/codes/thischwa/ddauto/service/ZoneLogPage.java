package codes.thischwa.ddauto.service;

import java.util.List;

public class ZoneLogPage {

	private int total;
	
	private int totalPage;
	
	private int page;
	
	private int pageSize;
	
	private String queryStringPrev;

	private String queryStringNext;
	
	private List<ZoneLogItem> items;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<ZoneLogItem> getItems() {
		return items;
	}

	public void setItems(List<ZoneLogItem> data) {
		this.items = data;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getQueryStringPrev() {
		return queryStringPrev;
	}

	public void setQueryStringPrev(String queryStringPrev) {
		this.queryStringPrev = queryStringPrev;
	}

	public String getQueryStringNext() {
		return queryStringNext;
	}

	public void setQueryStringNext(String queryStringNext) {
		this.queryStringNext = queryStringNext;
	}
	
}
