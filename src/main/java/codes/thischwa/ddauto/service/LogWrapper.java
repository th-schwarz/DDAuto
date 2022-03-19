package codes.thischwa.ddauto.service;

import java.util.List;

public class LogWrapper {

	private int total;
	
	private int totalPage;
	
	private int page;
	
	private List<ZoneUpdateItem> items;

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

	public List<ZoneUpdateItem> getItems() {
		return items;
	}

	public void setItems(List<ZoneUpdateItem> data) {
		this.items = data;
	}
	
	
}
