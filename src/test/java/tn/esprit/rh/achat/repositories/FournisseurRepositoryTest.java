package tn.esprit.rh.achat.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tn.esprit.rh.achat.entities.CategorieFournisseur;
import tn.esprit.rh.achat.entities.DetailFournisseur;
import tn.esprit.rh.achat.entities.Fournisseur;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for FournisseurRepository
 * Tests database operations for Fournisseur entity
 */
@DataJpaTest
class FournisseurRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    private Fournisseur fournisseur;
    private DetailFournisseur detailFournisseur;

    @BeforeEach
    void setUp() {
        // Initialize test data
        detailFournisseur = new DetailFournisseur();
        detailFournisseur.setEmail("test@repository.com");
        detailFournisseur.setAdresse("456 Repository Street");
        detailFournisseur.setMatricule("MAT-REPO-001");
        detailFournisseur.setDateDebutCollaboration(new Date());

        fournisseur = new Fournisseur();
        fournisseur.setCode("FRN-REPO-001");
        fournisseur.setLibelle("Repository Test Fournisseur");
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        fournisseur.setDetailFournisseur(detailFournisseur);
    }

    @Test
    void testSaveFournisseur_Success() {
        // Act
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        // Assert
        assertNotNull(savedFournisseur);
        assertNotNull(savedFournisseur.getIdFournisseur());
        assertEquals("FRN-REPO-001", savedFournisseur.getCode());
        assertEquals("Repository Test Fournisseur", savedFournisseur.getLibelle());
        assertEquals(CategorieFournisseur.ORDINAIRE, savedFournisseur.getCategorieFournisseur());
    }

    @Test
    void testFindById_Success() {
        // Arrange
        Fournisseur savedFournisseur = entityManager.persistAndFlush(fournisseur);

        // Act
        Optional<Fournisseur> foundFournisseur = fournisseurRepository.findById(savedFournisseur.getIdFournisseur());

        // Assert
        assertTrue(foundFournisseur.isPresent());
        assertEquals("FRN-REPO-001", foundFournisseur.get().getCode());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Fournisseur> foundFournisseur = fournisseurRepository.findById(999L);

        // Assert
        assertFalse(foundFournisseur.isPresent());
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        entityManager.persistAndFlush(fournisseur);

        Fournisseur fournisseur2 = new Fournisseur();
        fournisseur2.setCode("FRN-REPO-002");
        fournisseur2.setLibelle("Second Fournisseur");
        fournisseur2.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);
        
        DetailFournisseur detail2 = new DetailFournisseur();
        detail2.setEmail("test2@repository.com");
        detail2.setAdresse("789 Repository Avenue");
        detail2.setMatricule("MAT-REPO-002");
        detail2.setDateDebutCollaboration(new Date());
        fournisseur2.setDetailFournisseur(detail2);
        
        entityManager.persistAndFlush(fournisseur2);

        // Act
        List<Fournisseur> fournisseurs = (List<Fournisseur>) fournisseurRepository.findAll();

        // Assert
        assertNotNull(fournisseurs);
        assertEquals(2, fournisseurs.size());
    }

    @Test
    void testUpdateFournisseur_Success() {
        // Arrange
        Fournisseur savedFournisseur = entityManager.persistAndFlush(fournisseur);
        Long id = savedFournisseur.getIdFournisseur();

        // Act
        savedFournisseur.setCode("FRN-UPDATED");
        savedFournisseur.setLibelle("Updated Fournisseur");
        Fournisseur updatedFournisseur = fournisseurRepository.save(savedFournisseur);

        // Assert
        assertEquals(id, updatedFournisseur.getIdFournisseur());
        assertEquals("FRN-UPDATED", updatedFournisseur.getCode());
        assertEquals("Updated Fournisseur", updatedFournisseur.getLibelle());
    }

    @Test
    void testDeleteFournisseur_Success() {
        // Arrange
        Fournisseur savedFournisseur = entityManager.persistAndFlush(fournisseur);
        Long id = savedFournisseur.getIdFournisseur();

        // Act
        fournisseurRepository.deleteById(id);

        // Assert
        Optional<Fournisseur> deletedFournisseur = fournisseurRepository.findById(id);
        assertFalse(deletedFournisseur.isPresent());
    }

    @Test
    void testSaveFournisseur_WithDetailFournisseur_CascadeAll() {
        // Act
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        // Assert
        assertNotNull(savedFournisseur.getDetailFournisseur());
        assertNotNull(savedFournisseur.getDetailFournisseur().getIdDetailFournisseur());
        assertEquals("test@repository.com", savedFournisseur.getDetailFournisseur().getEmail());
    }

    @Test
    void testFournisseurWithCategorieFournisseur_ORDINAIRE() {
        // Arrange
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        // Act
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        // Assert
        assertEquals(CategorieFournisseur.ORDINAIRE, savedFournisseur.getCategorieFournisseur());
    }

    @Test
    void testFournisseurWithCategorieFournisseur_CONVENTIONNE() {
        // Arrange
        fournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        // Act
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        // Assert
        assertEquals(CategorieFournisseur.CONVENTIONNE, savedFournisseur.getCategorieFournisseur());
    }

    @Test
    void testCount_Success() {
        // Arrange
        entityManager.persistAndFlush(fournisseur);

        // Act
        long count = fournisseurRepository.count();

        // Assert
        assertEquals(1, count);
    }

    @Test
    void testExistsById_Success() {
        // Arrange
        Fournisseur savedFournisseur = entityManager.persistAndFlush(fournisseur);

        // Act
        boolean exists = fournisseurRepository.existsById(savedFournisseur.getIdFournisseur());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsById_NotFound() {
        // Act
        boolean exists = fournisseurRepository.existsById(999L);

        // Assert
        assertFalse(exists);
    }
}

