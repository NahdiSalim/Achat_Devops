package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.entities.CategorieFournisseur;
import tn.esprit.rh.achat.entities.DetailFournisseur;
import tn.esprit.rh.achat.entities.Fournisseur;
import tn.esprit.rh.achat.services.IFournisseurService;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for FournisseurRestController
 * Tests REST API endpoints for Fournisseur operations
 */
@WebMvcTest(FournisseurRestController.class)
class FournisseurRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFournisseurService fournisseurService;

    @Autowired
    private ObjectMapper objectMapper;

    private Fournisseur fournisseur;
    private DetailFournisseur detailFournisseur;

    @BeforeEach
    void setUp() {
        detailFournisseur = new DetailFournisseur();
        detailFournisseur.setIdDetailFournisseur(1L);
        detailFournisseur.setEmail("test@controller.com");
        detailFournisseur.setAdresse("123 Controller Street");
        detailFournisseur.setMatricule("MAT-CTRL-001");
        detailFournisseur.setDateDebutCollaboration(new Date());

        fournisseur = new Fournisseur();
        fournisseur.setIdFournisseur(1L);
        fournisseur.setCode("FRN-CTRL-001");
        fournisseur.setLibelle("Controller Test Fournisseur");
        fournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        fournisseur.setDetailFournisseur(detailFournisseur);
        fournisseur.setFactures(new HashSet<>());
        fournisseur.setSecteurActivites(new HashSet<>());
    }

    @Test
    void testGetFournisseurs_Success() throws Exception {
        // Arrange
        List<Fournisseur> fournisseurs = Arrays.asList(fournisseur);
        when(fournisseurService.retrieveAllFournisseurs()).thenReturn(fournisseurs);

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-all-fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("FRN-CTRL-001")))
                .andExpect(jsonPath("$[0].libelle", is("Controller Test Fournisseur")))
                .andExpect(jsonPath("$[0].categorieFournisseur", is("ORDINAIRE")));

        verify(fournisseurService, times(1)).retrieveAllFournisseurs();
    }

    @Test
    void testGetFournisseurs_EmptyList() throws Exception {
        // Arrange
        when(fournisseurService.retrieveAllFournisseurs()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-all-fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(fournisseurService, times(1)).retrieveAllFournisseurs();
    }

    @Test
    void testRetrieveFournisseur_Success() throws Exception {
        // Arrange
        when(fournisseurService.retrieveFournisseur(anyLong())).thenReturn(fournisseur);

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-fournisseur/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFournisseur", is(1)))
                .andExpect(jsonPath("$.code", is("FRN-CTRL-001")))
                .andExpect(jsonPath("$.libelle", is("Controller Test Fournisseur")))
                .andExpect(jsonPath("$.categorieFournisseur", is("ORDINAIRE")));

        verify(fournisseurService, times(1)).retrieveFournisseur(1L);
    }

    @Test
    void testRetrieveFournisseur_NotFound() throws Exception {
        // Arrange
        when(fournisseurService.retrieveFournisseur(anyLong())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-fournisseur/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(fournisseurService, times(1)).retrieveFournisseur(999L);
    }

    @Test
    void testAddFournisseur_Success() throws Exception {
        // Arrange
        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN-NEW-001");
        newFournisseur.setLibelle("New Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        Fournisseur savedFournisseur = new Fournisseur();
        savedFournisseur.setIdFournisseur(2L);
        savedFournisseur.setCode("FRN-NEW-001");
        savedFournisseur.setLibelle("New Fournisseur");
        savedFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(savedFournisseur);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFournisseur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFournisseur", is(2)))
                .andExpect(jsonPath("$.code", is("FRN-NEW-001")))
                .andExpect(jsonPath("$.libelle", is("New Fournisseur")))
                .andExpect(jsonPath("$.categorieFournisseur", is("CONVENTIONNE")));

        verify(fournisseurService, times(1)).addFournisseur(any(Fournisseur.class));
    }

    @Test
    void testModifyFournisseur_Success() throws Exception {
        // Arrange
        fournisseur.setLibelle("Updated Fournisseur");
        when(fournisseurService.updateFournisseur(any(Fournisseur.class))).thenReturn(fournisseur);

        // Act & Assert
        mockMvc.perform(put("/fournisseur/modify-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fournisseur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFournisseur", is(1)))
                .andExpect(jsonPath("$.code", is("FRN-CTRL-001")))
                .andExpect(jsonPath("$.libelle", is("Updated Fournisseur")));

        verify(fournisseurService, times(1)).updateFournisseur(any(Fournisseur.class));
    }

    @Test
    void testRemoveFournisseur_Success() throws Exception {
        // Arrange
        doNothing().when(fournisseurService).deleteFournisseur(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/fournisseur/remove-fournisseur/1"))
                .andExpect(status().isOk());

        verify(fournisseurService, times(1)).deleteFournisseur(1L);
    }

    @Test
    void testAssignSecteurActiviteToFournisseur_Success() throws Exception {
        // Arrange
        doNothing().when(fournisseurService).assignSecteurActiviteToFournisseur(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(put("/fournisseur/assignSecteurActiviteToFournisseur/1/1"))
                .andExpect(status().isOk());

        verify(fournisseurService, times(1)).assignSecteurActiviteToFournisseur(1L, 1L);
    }

    @Test
    void testAddFournisseur_WithInvalidData() throws Exception {
        // Arrange - Fournisseur with empty required fields
        Fournisseur invalidFournisseur = new Fournisseur();

        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(invalidFournisseur);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFournisseur)))
                .andExpect(status().isOk());

        verify(fournisseurService, times(1)).addFournisseur(any(Fournisseur.class));
    }

    @Test
    void testAddFournisseur_ORDINAIRE_Category() throws Exception {
        // Arrange
        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN-ORD-001");
        newFournisseur.setLibelle("Ordinaire Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(newFournisseur);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFournisseur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorieFournisseur", is("ORDINAIRE")));
    }

    @Test
    void testAddFournisseur_CONVENTIONNE_Category() throws Exception {
        // Arrange
        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN-CONV-001");
        newFournisseur.setLibelle("Conventionne Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(newFournisseur);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFournisseur)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorieFournisseur", is("CONVENTIONNE")));
    }
}

