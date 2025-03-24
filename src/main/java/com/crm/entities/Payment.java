package com.crm.entities;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;
    private String externalId;
    private String description;
    private BigDecimal amount;
    private String paymentSource;
    private LocalDateTime payment_date;
    private String integrationPaymentId;
    private String integrationType;
    private Long invoiceId;
    
    private Invoice invoice;
}