package com.crm.entities;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Invoice {
    private Long id;
    private String externalId;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime due_at;
    private Long clientId;
    private String integrationInvoiceId;
    private String integrationType;
    private String sourceId;
    private String sourceType;
    private Long offerId;

    private Client client;
    private List<InvoiceLine> invoiceLines = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private Offer offer;

    public boolean isSent() {
        return sentAt != null;
    }

    public boolean canUpdateInvoice() {
        return !isSent();
    }
}
