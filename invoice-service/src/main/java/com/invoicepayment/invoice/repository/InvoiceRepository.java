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
            "LOWER(i.customerName) LIKE LOWER(CONCAT('%', :customerName, '%')) OR " +
            "LOWER(li.description) LIKE LOWER(CONCAT('%', :itemDescription, '%'))")
    List<Invoice> searchByCustomerOrItemDescription(@Param("customerName") String customerName,
                                                    @Param("itemDescription") String itemDescription);
}
