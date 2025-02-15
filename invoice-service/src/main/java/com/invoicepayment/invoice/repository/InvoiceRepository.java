package com.invoicepayment.invoice.repository;

import com.invoicepayment.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i JOIN i.items li WHERE " +
            "LOWER(i.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(li.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Invoice> searchByCustomerOrItemDescription(@Param("keyword") String keyword);
}
