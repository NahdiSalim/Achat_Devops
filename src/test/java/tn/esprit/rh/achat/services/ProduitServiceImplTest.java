package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.Produit;
import tn.esprit.rh.achat.entities.Stock;
import tn.esprit.rh.achat.repositories.CategorieProduitRepository;
import tn.esprit.rh.achat.repositories.ProduitRepository;
import tn.esprit.rh.achat.repositories.StockRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProduitServiceImpl using Mockito
 * Tests service logic in isolation without Spring context
 */
@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CategorieProduitRepository categorieProduitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private Produit produit1;
    private Produit produit2;
    private Stock stock;

    @BeforeEach
    void setUp() {
        // Initialize test data
        produit1 = new Produit();
        produit1.setIdProduit(1L);
        produit1.setCodeProduit("PROD-001");
        produit1.setLibelleProduit("Test Product 1");
        produit1.setPrix(100.0f);

        produit2 = new Produit();
        produit2.setIdProduit(2L);
        produit2.setCodeProduit("PROD-002");
        produit2.setLibelleProduit("Test Product 2");
        produit2.setPrix(200.0f);

        stock = new Stock();
        stock.setIdStock(1L);
        stock.setLibelleStock("Main Stock");
        stock.setQte(100);
        stock.setQteMin(10);
    }

    @Test
    void testRetrieveAllProduits() {
        // Arrange
        List<Produit> expectedProduits = Arrays.asList(produit1, produit2);
        when(produitRepository.findAll()).thenReturn(expectedProduits);

        // Act
        List<Produit> actualProduits = produitService.retrieveAllProduits();

        // Assert
        assertNotNull(actualProduits);
        assertEquals(2, actualProduits.size());
        assertEquals(expectedProduits, actualProduits);
        verify(produitRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllProduits_EmptyList() {
        // Arrange
        when(produitRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Produit> actualProduits = produitService.retrieveAllProduits();

        // Assert
        assertNotNull(actualProduits);
        assertTrue(actualProduits.isEmpty());
        verify(produitRepository, times(1)).findAll();
    }

    @Test
    void testAddProduit() {
        // Arrange
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act
        Produit savedProduit = produitService.addProduit(produit1);

        // Assert
        assertNotNull(savedProduit);
        assertEquals("PROD-001", savedProduit.getCodeProduit());
        assertEquals("Test Product 1", savedProduit.getLibelleProduit());
        verify(produitRepository, times(1)).save(produit1);
    }

    @Test
    void testDeleteProduit() {
        // Arrange
        Long produitId = 1L;
        doNothing().when(produitRepository).deleteById(produitId);

        // Act
        produitService.deleteProduit(produitId);

        // Assert
        verify(produitRepository, times(1)).deleteById(produitId);
    }

    @Test
    void testUpdateProduit() {
        // Arrange
        produit1.setLibelleProduit("Updated Product");
        produit1.setPrix(150.0f);
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act
        Produit updatedProduit = produitService.updateProduit(produit1);

        // Assert
        assertNotNull(updatedProduit);
        assertEquals("Updated Product", updatedProduit.getLibelleProduit());
        assertEquals(150.0f, updatedProduit.getPrix());
        verify(produitRepository, times(1)).save(produit1);
    }

    @Test
    void testRetrieveProduit_Found() {
        // Arrange
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit1));

        // Act
        Produit retrievedProduit = produitService.retrieveProduit(1L);

        // Assert
        assertNotNull(retrievedProduit);
        assertEquals(1L, retrievedProduit.getIdProduit());
        assertEquals("PROD-001", retrievedProduit.getCodeProduit());
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveProduit_NotFound() {
        // Arrange
        when(produitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Produit retrievedProduit = produitService.retrieveProduit(999L);

        // Assert
        assertNull(retrievedProduit);
        verify(produitRepository, times(1)).findById(999L);
    }

    @Test
    void testAssignProduitToStock() {
        // Arrange
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit1));
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act
        produitService.assignProduitToStock(1L, 1L);

        // Assert
        assertNotNull(produit1.getStock());
        assertEquals(stock, produit1.getStock());
        verify(produitRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).findById(1L);
        verify(produitRepository, times(1)).save(produit1);
    }

    @Test
    void testAssignProduitToStock_ProduitNotFound() {
        // Arrange
        when(produitRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            produitService.assignProduitToStock(999L, 1L);
        });

        verify(produitRepository, times(1)).findById(999L);
        verify(stockRepository, times(1)).findById(1L);
        verify(produitRepository, never()).save(any());
    }

    @Test
    void testAssignProduitToStock_StockNotFound() {
        // Arrange
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit1));
        when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act - Stock is null but service doesn't throw exception
        produitService.assignProduitToStock(1L, 999L);

        // Assert
        verify(produitRepository, times(1)).findById(1L);
        verify(stockRepository, times(1)).findById(999L);
        verify(produitRepository, times(1)).save(produit1);
    }

    @Test
    void testAddProduit_WithStock() {
        // Arrange
        produit1.setStock(stock);
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act
        Produit savedProduit = produitService.addProduit(produit1);

        // Assert
        assertNotNull(savedProduit);
        assertNotNull(savedProduit.getStock());
        assertEquals("Main Stock", savedProduit.getStock().getLibelleStock());
        verify(produitRepository, times(1)).save(produit1);
    }

    @Test
    void testRetrieveAllProduits_MultipleCalls() {
        // Arrange
        List<Produit> expectedProduits = Arrays.asList(produit1, produit2);
        when(produitRepository.findAll()).thenReturn(expectedProduits);

        // Act
        produitService.retrieveAllProduits();
        produitService.retrieveAllProduits();

        // Assert
        verify(produitRepository, times(2)).findAll();
    }

    @Test
    void testDeleteProduit_MultipleDeletes() {
        // Arrange
        doNothing().when(produitRepository).deleteById(anyLong());

        // Act
        produitService.deleteProduit(1L);
        produitService.deleteProduit(2L);

        // Assert
        verify(produitRepository, times(1)).deleteById(1L);
        verify(produitRepository, times(1)).deleteById(2L);
        verify(produitRepository, times(2)).deleteById(anyLong());
    }
}

