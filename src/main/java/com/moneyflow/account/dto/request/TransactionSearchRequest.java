package com.moneyflow.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "交易明細搜尋請求參數")
public class TransactionSearchRequest {

	@Schema(description = "帳本 ID (必填)", example = "14", required = true)
    private Long bookId;

    @Schema(description = "分類 ID", example = "2")
    private Long categoryId;

    @Schema(description = "關鍵字搜尋 (名稱或備註)", example = "星巴克")
    private String keywords;

    @Schema(description = "交易類型", example = "EXPENSE", allowableValues = {"EXPENSE", "INCOME"})
    private String type;

    @Schema(description = "開始日期", example = "2026-02-01")
    private LocalDate startDate;

    @Schema(description = "結束日期", example = "2026-02-28")
    private LocalDate endDate;

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

    
    
}