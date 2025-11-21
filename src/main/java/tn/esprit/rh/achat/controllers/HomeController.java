package tn.esprit.rh.achat.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home controller with API information
 */
@RestController
@CrossOrigin("*")
@Tag(name = "Application Info", description = "Application information and health check endpoints")
@RequestMapping("/")
public class HomeController {

    /**
     * Get API information
     * @return Map with application details and Swagger UI link
     */
    @GetMapping
    @Operation(summary = "Get Application Information", description = "Returns application information and Swagger UI link")
    public Map<String, Object> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Achat DevOps Application");
        info.put("version", "1.0");
        info.put("status", "Running");
        info.put("description", "Purchase Management System API");
        info.put("swagger-ui", "/SpringMVC/swagger-ui.html");
        info.put("api-docs", "/SpringMVC/v3/api-docs");
        info.put("endpoints", new String[]{
            "/SpringMVC/produit - Product Management",
            "/SpringMVC/stock - Stock Management", 
            "/SpringMVC/fournisseur - Supplier Management",
            "/SpringMVC/facture - Invoice Management",
            "/SpringMVC/operateur - Operator Management",
            "/SpringMVC/reglement - Payment Management",
            "/SpringMVC/categorieProduit - Product Category Management",
            "/SpringMVC/secteurActivite - Business Sector Management"
        });
        return info;
    }

    /**
     * Health check endpoint
     * @return Simple status message
     */
    @GetMapping("/status")
    @Operation(summary = "Check API Status", description = "Simple health check endpoint")
    public Map<String, String> status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Application is running successfully!");
        return status;
    }
}

