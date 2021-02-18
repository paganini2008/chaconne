package indi.atlantis.framework.jobhub.console.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import indi.atlantis.framework.jobhub.model.PageQuery;

/**
 * 
 * PageBean
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class PageBean<T> implements Serializable {

	private static final long serialVersionUID = 8336451943582276136L;

	private static final int PAGE_SIZE = 20;

	private int rows; // total records
	private int size = PAGE_SIZE; // rows per page
	private int page; // page no

	private int totalPages;
	private int showPages; // show page number per page

	private int start;
	private int end;

	private int[][] showArray;

	private int firstPage;

	private int previousPage;

	private int nextPage;

	private int endPage;

	private List<Integer> pageNos = new ArrayList<Integer>();

	private List<T> results;

	public PageBean() {
		rows = 0;
		page = 0;
		totalPages = 0;
		showPages = 5;
	}

	public void refresh() {
		if (this.page == 0) {
			this.page = 1;
		}
		if (this.size == 0) {
			this.totalPages = 0;
		} else {
			this.totalPages = (rows + size - 1) / size;
		}
		this.start = (this.page - 1) * this.size;
		this.end = (this.page) * this.size;
		if (this.rows < this.size) {
			this.end = this.rows;
		}
		int first = 0;
		int previous = 0;
		int commonBegin = 0;
		int commonEnd = 0;
		int last = 0;
		int next = 0;

		if (this.page < 1) {
			this.page = 1;
		} else if (this.page > this.totalPages) {
			this.page = this.totalPages;
		}

		if (this.totalPages < this.showPages) {
			commonBegin = 1;
			commonEnd = this.totalPages;
		} else {
			commonBegin = ((this.page - 2) < 1) ? 1 : (this.page - 2);
			commonEnd = (commonBegin + this.showPages - 1) > this.totalPages ? this.totalPages : (commonBegin + this.showPages - 1);

			if ((commonEnd - commonBegin) < (this.showPages - 1)) {
				commonBegin = commonEnd - this.showPages + 1;
			}
		}

		if (this.totalPages > 1) {
			if (this.page == 1) {
				next = 1;
				if ((this.totalPages - commonEnd) > 0)
					last = 1;
			} else if (this.page == this.totalPages) {
				previous = 1;
				if ((commonBegin - 1) > 0)
					first = 1;
			} else {
				next = 1;
				previous = 1;
				if ((this.totalPages - commonEnd) > 0)
					last = 1;
				if ((commonBegin - 1) > 0)
					first = 1;
			}
		}

		int len = first + previous + (commonEnd - commonBegin + 1) + last + next;
		if (this.rows > 0) {
			len = len + 1;
		}
		showArray = new int[len][2];
		int arrayindex = 0;

		if (this.rows > 0) {
			showArray[arrayindex][0] = this.rows;
			showArray[arrayindex][1] = -1;
			arrayindex++;
		}

		if (first == 1) {
			showArray[arrayindex][0] = 1;
			showArray[arrayindex][1] = 1;
			arrayindex++;
		}
		if (previous == 1) {
			showArray[arrayindex][0] = this.page - 1;
			showArray[arrayindex][1] = 2;
			arrayindex++;
		}
		if (last == 1) {
			showArray[showArray.length - 2][0] = this.totalPages;
			showArray[showArray.length - 2][1] = 4;
		}
		if (next == 1) {
			showArray[showArray.length - 1][0] = this.page + 1;
			showArray[showArray.length - 1][1] = 5;
		}

		for (int i = commonBegin; i <= commonEnd; i++, arrayindex++) {
			showArray[arrayindex][0] = i;
			showArray[arrayindex][1] = 3;
			if (this.page == i) {
				showArray[arrayindex][1] = 0;
			}
		}

		setProperties();

	}

	private void setProperties() {
		for (int[] array : showArray) {
			int value = array[0];
			switch (array[1]) {
			case 1:
				firstPage = value;
				break;
			case 2:
				previousPage = value;
				break;
			case 3:
				pageNos.add(value);
				break;
			case 4:
				endPage = value;
				break;
			case 5:
				nextPage = value;
				break;
			}
		}
		if (!pageNos.isEmpty()) {
			int index = pageNos.indexOf(page - 1);
			if (index != -1) {
				pageNos.add(index + 1, page);
			}
		}
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getShowPages() {
		return showPages;
	}

	public void setShowPages(int showPages) {
		this.showPages = showPages;
	}

	public int[][] getShowArray() {
		return showArray;
	}

	public void setShowArray(int[][] showArray) {
		this.showArray = showArray;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public List<Integer> getPageNos() {
		return pageNos;
	}

	public void setPageNos(List<Integer> pageNos) {
		this.pageNos = pageNos;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getShowSize() {
		return results != null ? results.size() : 0;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public static boolean compareStartAndLimit(Integer start, Integer limit) {
		if (start == null || limit == null) {
			return false;
		}
		return (start >= 0) && (limit > 0) && (start < start + limit);
	}

	public boolean hasNextPage() {
		return (page + 1) <= getTotalPages();
	}

	public boolean hasPreviousPage() {
		return (page - 1 >= 1);
	}

	public static <T> PageBean<T> wrap(PageQuery<T> pageQuery) {
		PageBean<T> pageBean = new PageBean<T>();
		pageBean.setPage(pageQuery.getPage());
		pageBean.setSize(pageQuery.getSize());
		pageBean.setRows(pageQuery.getRows());
		pageBean.refresh();
		pageBean.setResults(pageQuery.getContent());
		return pageBean;
	}
}
