package com.moneyflow.account.domain.entity;

//這個物件拿來做交易列表展示的
public class TransactionVO extends  Transaction{
	
	private String categoryName; 

    // Getter & Setter
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
	@Override
	public String toString() {
		return "TransactionVO [categoryName=" + categoryName + "]";
	}
    
}
