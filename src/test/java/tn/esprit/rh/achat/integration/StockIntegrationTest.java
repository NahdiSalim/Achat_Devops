package tn.esprit.rh.achat.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tn.esprit.rh.achat.entities.Produit;
import tn.esprit.rh.achat.entities.Stock;
import tn.esprit.rh.achat.repositories.ProduitRepository;
import tn.esprit.rh.achat.repositories.StockRepository;
import tn.esprit.rh.achat.services.IStockService;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Stock functionality
 * Tests the complete flow from service to repository
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StockIntegrationTest {

    @Autowired
    private IStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProduitRepository produitRepository;

    private Stock stock;

    @BeforeEach
    void setUp() {
        // Clean database
        produitRepository.deleteAll();
        stockRepository.deleteAll();

        // Initialize test data
        stock = new Stock();
        stock.setLibelleStock("Integration Test Stock");
        stock.setQte(100);
        stock.setQteMin(10);
        stock.setProduits(new HashSet<>());
    }

    @Test
    void testCompleteAddStockFlow() {
        // Act
        Stock savedStock = stockService.addStock(stock);

        // Assert
        assertNotNull(savedStock);
        assertNotNull(savedStock.getIdStock());
        assertEquals("Integration Test Stock", savedStock.getLibelleStock());
        assertEquals(100, savedStock.getQte());
        assertEquals(10, savedStock.getQteMin());
    }

    @Test
    void testCompleteRetrieveAllStocksFlow() {
        // Arrange
        stockService.addStock(stock);

        Stock stock2 = new Stock();
        stock2.setLibelleStock("Second Integration Stock");
        stock2.setQte(200);
        stock2.setQteMin(20);
        stockService.addStock(stock2);

        // Act
        List<Stock> stocks = stockService.retrieveAllStocks();

        // Assert
        assertNotNull(stocks);
        assertEquals(2, stocks.size());
    }

    @Test
    void testCompleteUpdateStockFlow() {
        // Arrange
        Stock savedStock = stockService.addStock(stock);
        Long id = savedStock.getIdStock();

        // Modify
        savedStock.setLibelleStock("Updated Integration Stock");
        savedStock.setQte(150);
        savedStock.setQteMin(15);

        // Act
        Stock updatedStock = stockService.updateStock(savedStock);

        // Assert
        assertNotNull(updatedStock);
        assertEquals(id, updatedStock.getIdStock());
        assertEquals("Updated Integration Stock", updatedStock.getLibelleStock());
        assertEquals(150, updatedStock.getQte());
        assertEquals(15, updatedStock.getQteMin());
    }

    @Test
    void testCompleteDeleteStockFlow() {
        // Arrange
        Stock savedStock = stockService.addStock(stock);
        Long id = savedStock.getIdStock();

        // Act
        stockService.deleteStock(id);

        // Assert
        Stock deletedStock = stockService.retrieveStock(id);
        assertNull(deletedStock);
    }

    @Test
    void testCompleteRetrieveStockFlow() {
        // Arrange
        Stock savedStock = stockService.addStock(stock);
        Long id = savedStock.getIdStock();

        // Act
        Stock retrievedStock = stockService.retrieveStock(id);

        // Assert
        assertNotNull(retrievedStock);
        assertEquals(id, retrievedStock.getIdStock());
        assertEquals("Integration Test Stock", retrievedStock.getLibelleStock());
        assertEquals(100, retrievedStock.getQte());
    }

    @Test
    void testStockWithProduitsRelationship() {
        // Arrange
        Stock savedStock = stockService.addStock(stock);

        Produit produit1 = new Produit();
        produit1.setCodeProduit("PROD-INT-001");
        produit1.setLibelleProduit("Integration Product 1");
        produit1.setPrix(100.0f);
        produit1.setStock(savedStock);

        Produit produit2 = new Produit();
        produit2.setCodeProduit("PROD-INT-002");
        produit2.setLibelleProduit("Integration Product 2");
        produit2.setPrix(200.0f);
        produit2.setStock(savedStock);

        // Act
        Produit savedProduit1 = produitRepository.save(produit1);
        Produit savedProduit2 = produitRepository.save(produit2);

        // Flush to ensure products are persisted
        produitRepository.flush();

        // Retrieve products to verify they were saved with stock
        Produit retrievedProduit1 = produitRepository.findById(savedProduit1.getIdProduit()).orElse(null);
        Produit retrievedProduit2 = produitRepository.findById(savedProduit2.getIdProduit()).orElse(null);

        // Assert
        assertNotNull(retrievedProduit1);
        assertNotNull(retrievedProduit2);
        assertNotNull(retrievedProduit1.getStock());
        assertNotNull(retrievedProduit2.getStock());
        assertEquals(savedStock.getIdStock(), retrievedProduit1.getStock().getIdStock());
        assertEquals(savedStock.getIdStock(), retrievedProduit2.getStock().getIdStock());
    }

    @Test
    void testStockQuantityBelowMinimum() {
        // Arrange
        stock.setQte(5); // Below minimum
        stock.setQteMin(10);

        // Act
        Stock savedStock = stockService.addStock(stock);

        // Assert
        assertNotNull(savedStock);
        assertTrue(savedStock.getQte() < savedStock.getQteMin());
    }

    @Test
    void testStockQuantityAboveMinimum() {
        // Arrange
        stock.setQte(50); // Above minimum
        stock.setQteMin(10);

        // Act
        Stock savedStock = stockService.addStock(stock);

        // Assert
        assertNotNull(savedStock);
        assertTrue(savedStock.getQte() > savedStock.getQteMin());
    }

    @Test
    void testStockQuantityEqualMinimum() {
        // Arrange
        stock.setQte(10); // Equal to minimum
        stock.setQteMin(10);

        // Act
        Stock savedStock = stockService.addStock(stock);

        // Assert
        assertNotNull(savedStock);
        assertEquals(savedStock.getQte(), savedStock.getQteMin());
    }

    @Test
    void testMultipleStocksWithDifferentQuantities() {
        // Arrange
        Stock stock1 = new Stock();
        stock1.setLibelleStock("High Stock");
        stock1.setQte(1000);
        stock1.setQteMin(100);

        Stock stock2 = new Stock();
        stock2.setLibelleStock("Low Stock");
        stock2.setQte(5);
        stock2.setQteMin(10);

        Stock stock3 = new Stock();
        stock3.setLibelleStock("Medium Stock");
        stock3.setQte(50);
        stock3.setQteMin(20);

        // Act
        stockService.addStock(stock1);
        stockService.addStock(stock2);
        stockService.addStock(stock3);

        List<Stock> allStocks = stockService.retrieveAllStocks();

        // Assert
        assertEquals(3, allStocks.size());
        
        // Verify each stock
        Stock highStock = allStocks.stream()
                .filter(s -> "High Stock".equals(s.getLibelleStock()))
                .findFirst()
                .orElse(null);
        assertNotNull(highStock);
        assertTrue(highStock.getQte() > highStock.getQteMin());

        Stock lowStock = allStocks.stream()
                .filter(s -> "Low Stock".equals(s.getLibelleStock()))
                .findFirst()
                .orElse(null);
        assertNotNull(lowStock);
        assertTrue(lowStock.getQte() < lowStock.getQteMin());
    }

    @Test
    void testRetrieveStatusStock_MultipleStocksBelowMinimum() {
        // Arrange - Create stocks below minimum
        Stock stock1 = new Stock();
        stock1.setLibelleStock("Low Stock 1");
        stock1.setQte(5);
        stock1.setQteMin(10);
        stockService.addStock(stock1);

        Stock stock2 = new Stock();
        stock2.setLibelleStock("Low Stock 2");
        stock2.setQte(8);
        stock2.setQteMin(15);
        stockService.addStock(stock2);

        // Act
        String statusMessage = stockService.retrieveStatusStock();

        // Assert
        assertNotNull(statusMessage);
        // The message should contain information about low stocks
        // This is a simple check - in a real scenario, you might want more detailed assertions
    }

    @Test
    void testUpdateStockQuantity_IncrementAndDecrement() {
        // Arrange
        Stock savedStock = stockService.addStock(stock);
        Long id = savedStock.getIdStock();

        // Act - Increment
        savedStock.setQte(savedStock.getQte() + 50);
        Stock incrementedStock = stockService.updateStock(savedStock);

        // Assert increment
        assertEquals(150, incrementedStock.getQte());

        // Act - Decrement
        incrementedStock.setQte(incrementedStock.getQte() - 30);
        Stock decrementedStock = stockService.updateStock(incrementedStock);

        // Assert decrement
        assertEquals(120, decrementedStock.getQte());
    }
}

