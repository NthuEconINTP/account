package com.moneyflow.account.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moneyflow.account.auth.security.SecurityUtil;
import com.moneyflow.account.domain.entity.Book;
import com.moneyflow.account.domain.entity.Category;
import com.moneyflow.account.domain.entity.Transaction;
import com.moneyflow.account.domain.entity.TransactionVO;
import com.moneyflow.account.domain.repository.BookRepository;
import com.moneyflow.account.domain.repository.CategoryRepository;
import com.moneyflow.account.domain.repository.TransactionRepository;
import com.moneyflow.account.dto.request.TransactionSearchRequest;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    BookRepository bookRepository;
    
    @Autowired
	CategoryRepository categoryRepository;
    
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction create(Transaction tx) {
    	// 1. 基礎非空檢查 (必填欄位)
        if (tx == null) {
            throw new RuntimeException("新增失敗：交易資料不可為空");
        }
        if (tx.getBookId() == null) {
            throw new RuntimeException("新增失敗：必須提供帳本 ID");
        }
        if (tx.getCategoryId() == null) {
            throw new RuntimeException("新增失敗：必須提供分類 ID");
        }
        if (tx.getAmount() == null) {
            throw new RuntimeException("新增失敗：必須提供金額");
        }
        
        // compareTo 回傳: -1 (小於), 0 (等於), 1 (大於)
        if (tx.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("新增失敗：交易金額必須大於 0");
        }
        
//        if (tx.getType() == null) {
//            throw new RuntimeException("新增失敗：必須提供交易類型");
//        }  //  決定靠 tx.setType(category.getType());來決定
        if (tx.getTransactionDate() == null) {
            throw new RuntimeException("新增失敗：必須提供交易日期");
        }

        if (tx.getName() != null && tx.getName().length() > 200) {
            throw new RuntimeException("新增失敗：交易名稱不可超過 200 字");
        }  
        
        Long currentUserId = SecurityUtil.getCurrentUserId();
        
        // 2. 檢查帳本歸屬權 (防止越權)
        Book book = bookRepository.findById(tx.getBookId())
            .orElseThrow(() -> new RuntimeException("新增失敗：帳本不存在"));
        
        if (!book.getUserId().equals(currentUserId)) {
            throw new RuntimeException("新增失敗：您沒有權限操作此帳本");
        }

        if (!book.getIsActive()) {
            throw new RuntimeException("新增失敗：該帳本已停用");
        }
        
        // 3. 檢查分類歸屬權 (確保分類屬於該帳本)
        Category category = categoryRepository.findById(tx.getCategoryId())
            .orElseThrow(() -> new RuntimeException("新增失敗：分類不存在"));
        
        if (!category.getBookId().equals(tx.getBookId())) {
            throw new RuntimeException("新增失敗：該分類不屬於所選帳本");
        }
        
        if (!category.getIsActive()) {
            throw new RuntimeException("新增失敗：該分類已停用");
        }
        
     
        // 3. 系統自動填充欄位
        LocalDateTime now = LocalDateTime.now();
        
        tx.setType(category.getType());//亮點，強制交易類別一定要等於分類的type
        tx.setUserId(currentUserId);
        tx.setCreatedAt(now);
        tx.setLastUpdatedAt(now);
        tx.setIsActive(true);

        // 4. 執行儲存
        return transactionRepository.save(tx);
    }

    public Transaction softDelete(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!tx.getIsActive()) {
            return tx;
        }
        tx.setIsActive(false);
        tx.setLastUpdatedAt(LocalDateTime.now());
        tx.setUserId(SecurityUtil.getCurrentUserId());
        return transactionRepository.save(tx);
    }

    public List<Transaction> softDelete(List<Long> ids) {
        List<Transaction> list = transactionRepository.findAllById(ids);
        Long userId = SecurityUtil.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        for (Transaction tx : list) {
            if (tx.getIsActive()) {
                tx.setIsActive(false);
                tx.setLastUpdatedAt(now);
                tx.setUserId(userId);
            }
        }
        return transactionRepository.saveAll(list);
    }

//    public Page<Transaction> findByCondition(Transaction searchTx, Pageable pageable) {
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withIgnoreNullValues()
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                .withIgnoreCase();
//        Example<Transaction> example = Example.of(searchTx, matcher);
//        return transactionRepository.findAll(example, pageable);
//    }
//    


    public Page<TransactionVO> findByCondition(TransactionSearchRequest req, Pageable pageable) {
        // 1. 基礎 SQL
        String baseSql = " FROM tb_transactions t " +
                         " LEFT JOIN tb_categories c ON t.category_id = c.id " +
                         " WHERE t.is_active = true ";

        StringBuilder whereSql = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        // 2. 動態條件拼接
        if (req.getBookId() != null) {
            whereSql.append(" AND t.book_id = :bookId ");
            params.addValue("bookId", req.getBookId());
        }
        if (req.getCategoryId() != null) {
            whereSql.append(" AND t.category_id = :categoryId ");
            params.addValue("categoryId", req.getCategoryId());
        }
        if (req.getType() != null) {
            whereSql.append(" AND t.transaction_type = :type ");
            params.addValue("type", req.getType());
        }
        if (req.getKeywords() != null && !req.getKeywords().isBlank()) {
            whereSql.append(" AND (t.name LIKE :kw OR t.note LIKE :kw) ");
            params.addValue("kw", "%" + req.getKeywords() + "%");
        }
        if (req.getStartDate() != null) {
            whereSql.append(" AND t.transaction_date >= :startDate ");
            params.addValue("startDate", req.getStartDate());
        }
        if (req.getEndDate() != null) {
            whereSql.append(" AND t.transaction_date <= :endDate ");
            params.addValue("endDate", req.getEndDate());
        }
   
        // 1. 查詢總筆數
        String countSql = "SELECT COUNT(*) " + baseSql + whereSql.toString();
        Long total = jdbcTemplate.queryForObject(countSql, params, Long.class);

        // 2. 查詢資料 (固定排序，不理會前端傳什麼)
        // 建議固定用「交易日期」或「建立時間」降冪，這對記帳軟體最直覺
        StringBuilder dataSql = new StringBuilder("SELECT t.*, c.name AS categoryName ");
        dataSql.append(baseSql).append(whereSql);
        dataSql.append(" ORDER BY t.transaction_date DESC, t.id DESC "); // 保底排序，確保分頁不亂
        dataSql.append(" LIMIT :limit OFFSET :offset ");
        
        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());

        List<TransactionVO> list = jdbcTemplate.query(
            dataSql.toString(),
            params,
            new BeanPropertyRowMapper<>(TransactionVO.class)
        );

        return new PageImpl<>(list, pageable, total != null ? total : 0L);
    }

//	public Page<Transaction> findByCondition(Transaction searchTx, Pageable pageable) {
//		// TODO Auto-generated method stub
//		return null;
//	}
    
}
