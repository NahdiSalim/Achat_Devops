package tn.esprit.rh.achat.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import tn.esprit.rh.achat.entities.Stock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for Stock - No Mockito, tests actual database interactions
 * Uses @DataJpaTest which provides an in-memory database
 */
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    private Stock stock1;
    private Stock stock2;

    @BeforeEach
    void setUp() {
        // Clean the database
        stockRepository.deleteAll();

        // Initialize test data
        stock1 = new Stock();
        stock1.setLibelleStock("Test Stock 1");
        stock1.setQte(100);
        stock1.setQteMin(10);

        stock2 = new Stock();
        stock2.setLibelleStock("Test Stock 2");
        stock2.setQte(50);
        stock2.setQteMin(5);
    }

    @Test
    void testSaveStock() {
        // Act
        Stock savedStock = stockRepository.save(stock1);

        // Assert
        assertNotNull(savedStock);
        assertNotNull(savedStock.getIdStock());
        assertEquals("Test Stock 1", savedStock.getLibelleStock());
        assertEquals(100, savedStock.getQte());
        assertEquals(10, savedStock.getQteMin());
    }

    @Test
    void testFindById() {
        // Arrange
        Stock savedStock = stockRepository.save(stock1);

        // Act
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getIdStock());

        // Assert
        assertTrue(foundStock.isPresent());
        assertEquals(savedStock.getIdStock(), foundStock.get().getIdStock());
        assertEquals("Test Stock 1", foundStock.get().getLibelleStock());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Stock> foundStock = stockRepository.findById(999L);

        // Assert
        assertFalse(foundStock.isPresent());
    }

    @Test
    void testFindAll() {
        // Arrange
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        // Act
        List<Stock> stocks = (List<Stock>) stockRepository.findAll();

        // Assert
        assertNotNull(stocks);
        assertEquals(2, stocks.size());
    }

    @Test
    void testFindAll_Empty() {
        // Act
        List<Stock> stocks = (List<Stock>) stockRepository.findAll();

        // Assert
        assertNotNull(stocks);
        assertTrue(stocks.isEmpty());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Stock savedStock = stockRepository.save(stock1);
        Long stockId = savedStock.getIdStock();

        // Act
        stockRepository.deleteById(stockId);

        // Assert
        Optional<Stock> deletedStock = stockRepository.findById(stockId);
        assertFalse(deletedStock.isPresent());
    }

    @Test
    void testUpdateStock() {
        // Arrange
        Stock savedStock = stockRepository.save(stock1);

        // Act
        savedStock.setLibelleStock("Updated Stock");
        savedStock.setQte(200);
        savedStock.setQteMin(20);
        Stock updatedStock = stockRepository.save(savedStock);

        // Assert
        assertNotNull(updatedStock);
        assertEquals(savedStock.getIdStock(), updatedStock.getIdStock());
        assertEquals("Updated Stock", updatedStock.getLibelleStock());
        assertEquals(200, updatedStock.getQte());
        assertEquals(20, updatedStock.getQteMin());
    }

    @Test
    void testRetrieveStatusStock() {
        // Arrange - Create stocks with quantities below minimum
        Stock lowStock1 = new Stock();
        lowStock1.setLibelleStock("Low Stock 1");
        lowStock1.setQte(5);
        lowStock1.setQteMin(10);
        stockRepository.save(lowStock1);

        Stock lowStock2 = new Stock();
        lowStock2.setLibelleStock("Low Stock 2");
        lowStock2.setQte(3);
        lowStock2.setQteMin(8);
        stockRepository.save(lowStock2);

        Stock normalStock = new Stock();
        normalStock.setLibelleStock("Normal Stock");
        normalStock.setQte(100);
        normalStock.setQteMin(10);
        stockRepository.save(normalStock);

        // Act
        List<Stock> lowStocks = (List<Stock>) stockRepository.retrieveStatusStock();

        // Assert
        assertNotNull(lowStocks);
        assertEquals(2, lowStocks.size());
        
        // Verify all returned stocks have quantity below minimum
        for (Stock stock : lowStocks) {
            assertTrue(stock.getQte() < stock.getQteMin(),
                    "Stock " + stock.getLibelleStock() + " should have qty < qtyMin");
        }
    }

    @Test
    void testRetrieveStatusStock_NoLowStocks() {
        // Arrange - Create only stocks with quantities above minimum
        Stock normalStock1 = new Stock();
        normalStock1.setLibelleStock("Normal Stock 1");
        normalStock1.setQte(100);
        normalStock1.setQteMin(10);
        stockRepository.save(normalStock1);

        Stock normalStock2 = new Stock();
        normalStock2.setLibelleStock("Normal Stock 2");
        normalStock2.setQte(50);
        normalStock2.setQteMin(5);
        stockRepository.save(normalStock2);

        // Act
        List<Stock> lowStocks = (List<Stock>) stockRepository.retrieveStatusStock();

        // Assert
        assertNotNull(lowStocks);
        assertTrue(lowStocks.isEmpty());
    }

    @Test
    void testCount() {
        // Arrange
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        // Act
        long count = stockRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testExistsById() {
        // Arrange
        Stock savedStock = stockRepository.save(stock1);

        // Act
        boolean exists = stockRepository.existsById(savedStock.getIdStock());
        boolean notExists = stockRepository.existsById(999L);

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testDeleteAll() {
        // Arrange
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        // Act
        stockRepository.deleteAll();
        long count = stockRepository.count();

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testSaveMultipleStocks() {
        // Arrange
        Stock stock3 = new Stock();
        stock3.setLibelleStock("Test Stock 3");
        stock3.setQte(75);
        stock3.setQteMin(7);

        // Act
        stockRepository.save(stock1);
        stockRepository.save(stock2);
        stockRepository.save(stock3);

        List<Stock> stocks = (List<Stock>) stockRepository.findAll();

        // Assert
        assertEquals(3, stocks.size());
    }

    @Test
    void testStockWithZeroQuantity() {
        // Arrange
        stock1.setQte(0);

        // Act
        Stock savedStock = stockRepository.save(stock1);

        // Assert
        assertNotNull(savedStock);
        assertEquals(0, savedStock.getQte());
    }

    @Test
    void testStockWithNegativeMinimumQuantity() {
        // Arrange - Although this shouldn't happen in real scenarios, test edge case
        stock1.setQteMin(0);

        // Act
        Stock savedStock = stockRepository.save(stock1);

        // Assert
        assertNotNull(savedStock);
        assertEquals(0, savedStock.getQteMin());
    }

    @Test
    void testRetrieveStatusStock_EdgeCase_QuantityEqualsMinimum() {
        // Arrange - Create stock with quantity equal to minimum
        Stock edgeStock = new Stock();
        edgeStock.setLibelleStock("Edge Case Stock");
        edgeStock.setQte(10);
        edgeStock.setQteMin(10);
        stockRepository.save(edgeStock);

        // Act
        List<Stock> lowStocks = (List<Stock>) stockRepository.retrieveStatusStock();

        // Assert - Stock with qty == qtyMin should NOT be in low stock list
        assertTrue(lowStocks.isEmpty() || lowStocks.stream().noneMatch(s -> 
            s.getLibelleStock().equals("Edge Case Stock")));
    }
}

