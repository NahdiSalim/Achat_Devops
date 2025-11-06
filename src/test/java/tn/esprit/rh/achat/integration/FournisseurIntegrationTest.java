package tn.esprit.rh.achat.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tn.esprit.rh.achat.entities.*;
import tn.esprit.rh.achat.repositories.FournisseurRepository;
import tn.esprit.rh.achat.repositories.SecteurActiviteRepository;
import tn.esprit.rh.achat.services.IFournisseurService;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Fournisseur functionality
 * Tests the complete flow from service to repository
 */
@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FournisseurIntegrationTest {

    @Autowired
    private IFournisseurService fournisseurService;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private SecteurActiviteRepository secteurActiviteRepository;

    private Fournisseur fournisseur;
    private SecteurActivite secteurActivite;

    @BeforeEach
    void setUp() {
        // Clean database
        fournisseurRepository.deleteAll();
        secteurActiviteRepository.deleteAll();

        // Initialize test data
        fournisseur = new Fournisseur();
        fournisseur.setCode("FRN-INT-001");
        fournisseur.setLibelle("Integration Test Fournisseur");
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        fournisseur.setFactures(new HashSet<>());
        fournisseur.setSecteurActivites(new HashSet<>());

        secteurActivite = new SecteurActivite();
        secteurActivite.setCodeSecteurActivite("SEC-INT-001");
        secteurActivite.setLibelleSecteurActivite("Integration Sector");
    }

    @Test
    void testCompleteAddFournisseurFlow() {
        // Act
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);

        // Assert
        assertNotNull(savedFournisseur);
        assertNotNull(savedFournisseur.getIdFournisseur());
        assertNotNull(savedFournisseur.getDetailFournisseur());
        assertNotNull(savedFournisseur.getDetailFournisseur().getDateDebutCollaboration());
        assertEquals("FRN-INT-001", savedFournisseur.getCode());
    }

    @Test
    void testCompleteRetrieveAllFournisseursFlow() {
        // Arrange
        fournisseurService.addFournisseur(fournisseur);

        Fournisseur fournisseur2 = new Fournisseur();
        fournisseur2.setCode("FRN-INT-002");
        fournisseur2.setLibelle("Second Integration Fournisseur");
        fournisseur2.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);
        fournisseurService.addFournisseur(fournisseur2);

        // Act
        List<Fournisseur> fournisseurs = fournisseurService.retrieveAllFournisseurs();

        // Assert
        assertNotNull(fournisseurs);
        assertEquals(2, fournisseurs.size());
    }

    @Test
    void testCompleteUpdateFournisseurFlow() {
        // Arrange
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);
        Long id = savedFournisseur.getIdFournisseur();

        // Modify
        savedFournisseur.setCode("FRN-INT-UPDATED");
        savedFournisseur.setLibelle("Updated Integration Fournisseur");
        savedFournisseur.getDetailFournisseur().setEmail("updated@integration.com");

        // Act
        Fournisseur updatedFournisseur = fournisseurService.updateFournisseur(savedFournisseur);

        // Assert
        assertNotNull(updatedFournisseur);
        assertEquals(id, updatedFournisseur.getIdFournisseur());
        assertEquals("FRN-INT-UPDATED", updatedFournisseur.getCode());
        assertEquals("Updated Integration Fournisseur", updatedFournisseur.getLibelle());
        assertEquals("updated@integration.com", updatedFournisseur.getDetailFournisseur().getEmail());
    }

    @Test
    void testCompleteDeleteFournisseurFlow() {
        // Arrange
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);
        Long id = savedFournisseur.getIdFournisseur();

        // Act
        fournisseurService.deleteFournisseur(id);

        // Assert
        Fournisseur deletedFournisseur = fournisseurService.retrieveFournisseur(id);
        assertNull(deletedFournisseur);
    }

    @Test
    void testCompleteRetrieveFournisseurFlow() {
        // Arrange
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);
        Long id = savedFournisseur.getIdFournisseur();

        // Act
        Fournisseur retrievedFournisseur = fournisseurService.retrieveFournisseur(id);

        // Assert
        assertNotNull(retrievedFournisseur);
        assertEquals(id, retrievedFournisseur.getIdFournisseur());
        assertEquals("FRN-INT-001", retrievedFournisseur.getCode());
        assertEquals("Integration Test Fournisseur", retrievedFournisseur.getLibelle());
    }

    @Test
    void testCompleteAssignSecteurActiviteFlow() {
        // Arrange
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);
        SecteurActivite savedSecteur = secteurActiviteRepository.save(secteurActivite);

        // Act
        fournisseurService.assignSecteurActiviteToFournisseur(
                savedSecteur.getIdSecteurActivite(),
                savedFournisseur.getIdFournisseur()
        );

        // Assert
        Fournisseur updatedFournisseur = fournisseurService.retrieveFournisseur(savedFournisseur.getIdFournisseur());
        assertNotNull(updatedFournisseur);
        assertNotNull(updatedFournisseur.getSecteurActivites());
        assertEquals(1, updatedFournisseur.getSecteurActivites().size());
    }

    @Test
    void testFournisseurWithDetailFournisseurCascade() {
        // Act
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);

        // Assert - DetailFournisseur should be saved via cascade
        assertNotNull(savedFournisseur.getDetailFournisseur());
        assertNotNull(savedFournisseur.getDetailFournisseur().getIdDetailFournisseur());
        assertNotNull(savedFournisseur.getDetailFournisseur().getDateDebutCollaboration());
    }

    @Test
    void testMultipleSecteurActivitesAssignment() {
        // Arrange
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);
        
        SecteurActivite secteur1 = new SecteurActivite();
        secteur1.setCodeSecteurActivite("SEC-INT-001");
        secteur1.setLibelleSecteurActivite("Sector 1");
        SecteurActivite savedSecteur1 = secteurActiviteRepository.save(secteur1);

        SecteurActivite secteur2 = new SecteurActivite();
        secteur2.setCodeSecteurActivite("SEC-INT-002");
        secteur2.setLibelleSecteurActivite("Sector 2");
        SecteurActivite savedSecteur2 = secteurActiviteRepository.save(secteur2);

        // Act
        fournisseurService.assignSecteurActiviteToFournisseur(
                savedSecteur1.getIdSecteurActivite(),
                savedFournisseur.getIdFournisseur()
        );
        fournisseurService.assignSecteurActiviteToFournisseur(
                savedSecteur2.getIdSecteurActivite(),
                savedFournisseur.getIdFournisseur()
        );

        // Assert
        Fournisseur updatedFournisseur = fournisseurService.retrieveFournisseur(savedFournisseur.getIdFournisseur());
        assertNotNull(updatedFournisseur.getSecteurActivites());
        assertEquals(2, updatedFournisseur.getSecteurActivites().size());
    }

    @Test
    void testFournisseurWithCategorie_ORDINAIRE() {
        // Arrange
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        // Act
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);

        // Assert
        assertNotNull(savedFournisseur);
        assertEquals(CategorieFournisseur.ORDINAIRE, savedFournisseur.getCategorieFournisseur());
    }

    @Test
    void testFournisseurWithCategorie_CONVENTIONNE() {
        // Arrange
        fournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        // Act
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);

        // Assert
        assertNotNull(savedFournisseur);
        assertEquals(CategorieFournisseur.CONVENTIONNE, savedFournisseur.getCategorieFournisseur());
    }

    @Test
    void testUpdateFournisseurDetailFournisseur() {
        // Arrange
        Fournisseur savedFournisseur = fournisseurService.addFournisseur(fournisseur);
        
        // Modify DetailFournisseur
        savedFournisseur.getDetailFournisseur().setEmail("newmail@test.com");
        savedFournisseur.getDetailFournisseur().setAdresse("New Address 123");
        savedFournisseur.getDetailFournisseur().setMatricule("NEW-MAT-001");

        // Act
        Fournisseur updatedFournisseur = fournisseurService.updateFournisseur(savedFournisseur);

        // Assert
        assertEquals("newmail@test.com", updatedFournisseur.getDetailFournisseur().getEmail());
        assertEquals("New Address 123", updatedFournisseur.getDetailFournisseur().getAdresse());
        assertEquals("NEW-MAT-001", updatedFournisseur.getDetailFournisseur().getMatricule());
    }
}

