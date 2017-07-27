package com.quanjing.util.framework;

import java.io.Serializable;


public class PageRequest implements Serializable {

		
	/**
	 * 页号码,页码从1开始
	 */
	private int pageNum=1;
	/**
	 * 分页大小
	 */
	private Integer pageSize=10;
	
	private Long cursorIndex;
	
	private int direction=1;//1:（>cursorIndex） 2:（<cursorIndex）
	
	private boolean reasonable=false;
	
	/**
	 * 排序的多个列,如: username desc
	 */
	private String sortColumns;
	
		
	public boolean getReasonable() {
		return reasonable;
	}


		public PageRequest() {
			this(1,10);
		}
				
		
		public PageRequest(int pageNumber, int pageSize) {
			this(pageNumber,pageSize,null);
		}
				
		
		public PageRequest(int pageNumber, int pageSize,String sortColumns) {
			this.pageNum = pageNumber;
			this.pageSize = pageSize;
			//setSortColumns(sortColumns);
		}
		
		
		public int getPageNum() {
			return pageNum;
		}

		public void setPageNum(int pageNumber) {
			this.pageNum = pageNumber;
		}

		public Integer getPageSize() {
			return pageSize;
		}

		public void setPageSize(Integer pageSize) {
			if(pageSize!=null&&pageSize>5000){
				pageSize=5000;
			}
			this.pageSize = pageSize;
		}
		
		public String getSortColumns() {
			return sortColumns;
		}
		
		public Long getCursorIndex() {
			return cursorIndex;
		}


		public void setCursorIndex(Long cursorIndex) {
			this.cursorIndex = cursorIndex;
		}


		public int getDirection() {
			return direction;
		}


		public void setDirection(int direction) {
			this.direction = direction;
		}


		public void setSortColumns(String sortColumns) {
			this.sortColumns = sortColumns;
		}


		private void checkSortColumnsSqlInjection(String sortColumns) {
			if(sortColumns == null) return;
			if(sortColumns.indexOf("'") >= 0 || sortColumns.indexOf("\\") >= 0) {
				throw new IllegalArgumentException("sortColumns:"+sortColumns+" has SQL Injection risk");
			}
		}
}
