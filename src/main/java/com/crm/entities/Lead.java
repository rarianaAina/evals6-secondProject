package com.crm.entities;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class Lead {
    private Long id;
    private String externalId;
    private String title;
    private String description;
    private Long statusId;
    private Long userAssignedId;
    private Long userCreatedId;
    private Long clientId;
    private String result;
    private LocalDate deadline;
    private Long invoiceId;

    private Status status;
    private User assignedUser;
    private User creator;
    private Client client;
    private List<Offer> offers;
    private Invoice invoice;

    // Constante pour le statut fermé
    public static final String LEAD_STATUS_CLOSED = "closed";

    public Lead() {
        this.externalId = UUID.randomUUID().toString();
    }

    public boolean isClosed() {
        return this.status != null && LEAD_STATUS_CLOSED.equalsIgnoreCase(this.status.getTitle());
    }

    public boolean canConvertToOrder() {
        return this.invoice == null;
    }

    public Invoice convertToOrder() {
        if (!canConvertToOrder()) {
            return null;
        }

        Invoice newInvoice = new Invoice();
        newInvoice.setStatus("draft");
        newInvoice.setClientId(this.clientId);
        newInvoice.setExternalId(UUID.randomUUID().toString());

        this.invoiceId = newInvoice.getId();
        this.statusId = Status.typeOfLead(List.of(this.status))
                .stream()
                .filter(s -> "Closed".equalsIgnoreCase(s.getTitle()))
                .findFirst()
                .map(Status::getId)
                .orElse(null);

        return newInvoice;
    }
}
