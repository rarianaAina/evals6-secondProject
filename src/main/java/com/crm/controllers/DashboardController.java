package com.crm.controllers;

import com.crm.services.*;
import com.crm.entities.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final ClientService clientService;
    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final SettingService settingService;

    private final OffersService offersService;

    public DashboardController(
            ClientService clientService,
            PaymentService paymentService,
            InvoiceService invoiceService,
            ProjectService projectService,
            TaskService taskService,
            SettingService settingService,
            OffersService offersService) {
        this.clientService = clientService;
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.settingService = settingService;
        this.offersService = offersService;
    }

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> data = new HashMap<>();

        // Récupération des données depuis les services
        var clients = clientService.getAllClients();
        var payments = paymentService.getAllPayments();
        var invoices = invoiceService.getAllInvoices();
        var projects = projectService.getAllProjects();
        var tasks = taskService.getAllTasks();
        var offers = offersService.getAllOffers();


        // Calcul des totaux
        data.put("totalClients", clients.size());
        data.put("totalProjects", projects.size());
        data.put("totalTasks", tasks.size());
        data.put("totalInvoices", invoices.size());
        data.put("totalPayments", payments.size());
        data.put("totalOffers", offers.size());

        System.out.println("Offres : " + offers);
        // Calcul des métriques financières
        BigDecimal totalInvoiceAmount = invoices.stream()
                .flatMap(invoice -> invoice.getInvoiceLines().stream())
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaymentAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("totalInvoiceAmount", totalInvoiceAmount);
        data.put("totalPaymentAmount", totalPaymentAmount);
        data.put("outstandingAmount", totalInvoiceAmount.subtract(totalPaymentAmount));

        // Regrouper les projets par statut et compter
        Map<String, Long> projectStatusCount = projects.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().getTitle(), Collectors.counting()));

        // Ajouter les données pour le graphique
        data.put("projectStatusCount", projectStatusCount);

        // Regrouper les projets par statut et compter
        Map<String, Long> taskStatusCount = tasks.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().getTitle(), Collectors.counting()));

        // Ajouter les données pour le graphique
        data.put("tasksStatusCount", taskStatusCount);

        // Ajouter les listes complètes pour les graphiques
        data.put("projects", projects);
        data.put("tasks", tasks);

        // Ajouter le taux de remise global
        data.put("globalDiscountRate", settingService.getGlobalDiscountRate());

        model.addAttribute("data", data);
        return "dashboard";
    }

    @GetMapping("/list/{type}")
    public String list(@PathVariable String type, Model model) {
        List<?> items = switch (type) {
            case "clients" -> clientService.getAllClients();
            case "payments" -> paymentService.getAllPayments();
            case "invoices" -> invoiceService.getAllInvoices();
            case "projects" -> projectService.getAllProjects();
            case "offers" -> offersService.getAllOffers();
            case "tasks" -> taskService.getAllTasks();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };

        model.addAttribute("items", items);
        model.addAttribute("type", type);
        return "list";
    }

    @GetMapping("/details/{type}/{id}")
    public String details(@PathVariable String type, @PathVariable Long id, Model model) {
        Map<String, Object> data = new HashMap<>();

        switch (type) {
            case "client" -> {
                var client = clientService.getClientById(id);
                data.put("client", client);
                data.put("displayFields", clientService.getClientDisplayFields(client));
            }
            case "payment" -> {
                var payment = paymentService.getPaymentById(id);
                var clientName = payment.getInvoice().getClient().getCompany_name();
                data.put("clientName", clientName);
                data.put("payment", payment);
                data.put("canModify", true);
            }
            case "invoice" -> {
                var invoice = invoiceService.getInvoiceById(id);
                data.put("invoice", invoice);
                data.put("discountedAmount", invoiceService.calculateDiscountedAmount(invoice));
            }
            case "offers" -> {
                var offers = offersService.getOfferById(id);
                data.put("offers", offers);
            }
            case "project" -> {
                var project = projectService.getProjectById(id);
                data.put("project", project);
            }
            case "task" -> {
                var task = taskService.getTaskById(id);
                data.put("task", task);
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }

        model.addAttribute("type", type);
        model.addAttribute("data", data);
        return "details";
    }

    @PostMapping("/payment/{id}")
    public String updatePayment(@PathVariable Long id, @RequestParam BigDecimal amount) {
        paymentService.updatePaymentAmount(id, amount);
        return "redirect:/dashboard/details/payment/" + id;
    }

    @DeleteMapping("/payment/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/dashboard/list/payments";
    }

    @PostMapping("/settings/discount")
    public String updateGlobalDiscountRate(@RequestParam Double rate) {
        settingService.updateGlobalDiscountRate(rate / 100.0); // Convert from percentage to decimal
        return "redirect:/dashboard";
    }
}