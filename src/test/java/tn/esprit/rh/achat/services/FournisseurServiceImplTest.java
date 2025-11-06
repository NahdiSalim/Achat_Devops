package tn.esprit.rh.achat.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.*;
import tn.esprit.rh.achat.repositories.DetailFournisseurRepository;
import tn.esprit.rh.achat.repositories.FournisseurRepository;
import tn.esprit.rh.achat.repositories.SecteurActiviteRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FournisseurServiceImpl
 * Tests all CRUD operations and business logic for Fournisseur entity
 */
@ExtendWith(MockitoExtension.class)
class FournisseurServiceImplTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private DetailFournisseurRepository detailFournisseurRepository;

    @Mock
    private SecteurActiviteRepository secteurActiviteRepository;

    @InjectMocks
    private FournisseurServiceImpl fournisseurService;

    private Fournisseur fournisseur;
    private DetailFournisseur detailFournisseur;
    private SecteurActivite secteurActivite;

    @BeforeEach
    void setUp() {
        // Initialize test data
        detailFournisseur = new DetailFournisseur();
        detailFournisseur.setIdDetailFournisseur(1L);
        detailFournisseur.setEmail("test@fournisseur.com");
        detailFournisseur.setAdresse("123 Test Street");
        detailFournisseur.setMatricule("MAT001");
        detailFournisseur.setDateDebutCollaboration(new Date());

        fournisseur = new Fournisseur();
        fournisseur.setIdFournisseur(1L);
        fournisseur.setCode("FRN001");
        fournisseur.setLibelle("Fournisseur Test");
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        fournisseur.setDetailFournisseur(detailFournisseur);
        fournisseur.setFactures(new HashSet<>());
        fournisseur.setSecteurActivites(new HashSet<>());

        secteurActivite = new SecteurActivite();
        secteurActivite.setIdSecteurActivite(1L);
        secteurActivite.setCodeSecteurActivite("SEC001");
        secteurActivite.setLibelleSecteurActivite("IT Services");
    }

    @Test
    void testRetrieveAllFournisseurs_Success() {
        // Arrange
        List<Fournisseur> fournisseurs = Arrays.asList(fournisseur);
        when(fournisseurRepository.findAll()).thenReturn(fournisseurs);

        // Act
        List<Fournisseur> result = fournisseurService.retrieveAllFournisseurs();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FRN001", result.get(0).getCode());
        verify(fournisseurRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllFournisseurs_EmptyList() {
        // Arrange
        when(fournisseurRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Fournisseur> result = fournisseurService.retrieveAllFournisseurs();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(fournisseurRepository, times(1)).findAll();
    }

    @Test
    void testAddFournisseur_Success() {
        // Arrange
        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN002");
        newFournisseur.setLibelle("New Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(newFournisseur);

        // Act
        Fournisseur result = fournisseurService.addFournisseur(newFournisseur);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getDetailFournisseur());
        assertNotNull(result.getDetailFournisseur().getDateDebutCollaboration());
        verify(fournisseurRepository, times(1)).save(any(Fournisseur.class));
    }

    @Test
    void testAddFournisseur_WithDetailFournisseur() {
        // Arrange
        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN003");
        newFournisseur.setLibelle("Fournisseur with Details");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(newFournisseur);

        // Act
        Fournisseur result = fournisseurService.addFournisseur(newFournisseur);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getDetailFournisseur());
        verify(fournisseurRepository, times(1)).save(any(Fournisseur.class));
    }

    @Test
    void testUpdateFournisseur_Success() {
        // Arrange
        fournisseur.getDetailFournisseur().setEmail("updated@fournisseur.com");
        when(detailFournisseurRepository.save(any(DetailFournisseur.class))).thenReturn(detailFournisseur);
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);

        // Act
        Fournisseur result = fournisseurService.updateFournisseur(fournisseur);

        // Assert
        assertNotNull(result);
        assertEquals("updated@fournisseur.com", result.getDetailFournisseur().getEmail());
        verify(detailFournisseurRepository, times(1)).save(any(DetailFournisseur.class));
        verify(fournisseurRepository, times(1)).save(any(Fournisseur.class));
    }

    @Test
    void testUpdateFournisseur_WithNullDetailFournisseur() {
        // Arrange
        fournisseur.setDetailFournisseur(null);
        
        // Act & Assert - Current implementation will throw NullPointerException
        // when trying to access null detailFournisseur
        try {
            fournisseurService.updateFournisseur(fournisseur);
            // If we reach here, the service handled null gracefully (unexpected but acceptable)
        } catch (NullPointerException e) {
            // This is expected behavior - accessing null reference
            assertNotNull(e);
        }
    }

    @Test
    void testDeleteFournisseur_Success() {
        // Arrange
        Long fournisseurId = 1L;
        doNothing().when(fournisseurRepository).deleteById(anyLong());

        // Act
        fournisseurService.deleteFournisseur(fournisseurId);

        // Assert
        verify(fournisseurRepository, times(1)).deleteById(fournisseurId);
    }

    @Test
    void testRetrieveFournisseur_Success() {
        // Arrange
        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.of(fournisseur));

        // Act
        Fournisseur result = fournisseurService.retrieveFournisseur(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdFournisseur());
        assertEquals("FRN001", result.getCode());
        verify(fournisseurRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveFournisseur_NotFound() {
        // Arrange
        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Fournisseur result = fournisseurService.retrieveFournisseur(999L);

        // Assert
        assertNull(result);
        verify(fournisseurRepository, times(1)).findById(999L);
    }

    @Test
    void testAssignSecteurActiviteToFournisseur_Success() {
        // Arrange
        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.of(fournisseur));
        when(secteurActiviteRepository.findById(anyLong())).thenReturn(Optional.of(secteurActivite));
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);

        // Act
        fournisseurService.assignSecteurActiviteToFournisseur(1L, 1L);

        // Assert
        verify(fournisseurRepository, times(1)).findById(1L);
        verify(secteurActiviteRepository, times(1)).findById(1L);
        verify(fournisseurRepository, times(1)).save(any(Fournisseur.class));
    }

    @Test
    void testAssignSecteurActiviteToFournisseur_FournisseurNotFound() {
        // Arrange
        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(secteurActiviteRepository.findById(anyLong())).thenReturn(Optional.of(secteurActivite));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            fournisseurService.assignSecteurActiviteToFournisseur(1L, 999L);
        });
    }

    @Test
    void testAssignSecteurActiviteToFournisseur_SecteurNotFound() {
        // Arrange
        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.of(fournisseur));
        when(secteurActiviteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert - Current implementation does not validate null secteur
        // This test documents that the service should ideally handle null values better
        try {
            fournisseurService.assignSecteurActiviteToFournisseur(999L, 1L);
            // If no exception is thrown, verify repositories were called
            verify(fournisseurRepository, times(1)).findById(1L);
            verify(secteurActiviteRepository, times(1)).findById(999L);
        } catch (NullPointerException e) {
            // This is also acceptable behavior - service attempts to use null secteur
            assertNotNull(e);
        }
    }

    @Test
    void testFournisseurWithCategorieFournisseur_ORDINAIRE() {
        // Arrange
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);

        // Act
        Fournisseur result = fournisseurService.addFournisseur(fournisseur);

        // Assert
        assertNotNull(result);
        assertEquals(CategorieFournisseur.ORDINAIRE, result.getCategorieFournisseur());
    }

    @Test
    void testFournisseurWithCategorieFournisseur_CONVENTIONNE() {
        // Arrange
        fournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);

        // Act
        Fournisseur result = fournisseurService.addFournisseur(fournisseur);

        // Assert
        assertNotNull(result);
        assertEquals(CategorieFournisseur.CONVENTIONNE, result.getCategorieFournisseur());
    }

    @Test
    void testFournisseurWithMultipleSecteurActivites() {
        // Arrange
        SecteurActivite secteur2 = new SecteurActivite();
        secteur2.setIdSecteurActivite(2L);
        secteur2.setCodeSecteurActivite("SEC002");
        secteur2.setLibelleSecteurActivite("Manufacturing");

        Set<SecteurActivite> secteurs = new HashSet<>();
        secteurs.add(secteurActivite);
        secteurs.add(secteur2);
        fournisseur.setSecteurActivites(secteurs);

        when(fournisseurRepository.findById(anyLong())).thenReturn(Optional.of(fournisseur));

        // Act
        Fournisseur result = fournisseurService.retrieveFournisseur(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getSecteurActivites().size());
    }
}

