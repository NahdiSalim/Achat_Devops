package tn.esprit.rh.achat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.rh.achat.dto.FournisseurDTO;
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

    @MockBean
    private tn.esprit.rh.achat.util.DTOMapper dtoMapper;

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
        FournisseurDTO dto = new FournisseurDTO();
        dto.setIdFournisseur(1L);
        dto.setCode("FRN-CTRL-001");
        dto.setLibelle("Controller Test Fournisseur");
        dto.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        
        when(fournisseurService.retrieveAllFournisseurs()).thenReturn(fournisseurs);
        when(dtoMapper.toFournisseurDTOList(fournisseurs)).thenReturn(Arrays.asList(dto));

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-all-fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("FRN-CTRL-001")))
                .andExpect(jsonPath("$[0].libelle", is("Controller Test Fournisseur")))
                .andExpect(jsonPath("$[0].categorieFournisseur", is("ORDINAIRE")));

        verify(fournisseurService, times(1)).retrieveAllFournisseurs();
        verify(dtoMapper, times(1)).toFournisseurDTOList(fournisseurs);
    }

    @Test
    void testGetFournisseurs_EmptyList() throws Exception {
        // Arrange
        List<Fournisseur> emptyList = Arrays.asList();
        when(fournisseurService.retrieveAllFournisseurs()).thenReturn(emptyList);
        when(dtoMapper.toFournisseurDTOList(emptyList)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-all-fournisseurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(fournisseurService, times(1)).retrieveAllFournisseurs();
        verify(dtoMapper, times(1)).toFournisseurDTOList(emptyList);
    }

    @Test
    void testRetrieveFournisseur_Success() throws Exception {
        // Arrange
        FournisseurDTO dto = new FournisseurDTO();
        dto.setIdFournisseur(1L);
        dto.setCode("FRN-CTRL-001");
        dto.setLibelle("Controller Test Fournisseur");
        dto.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);
        
        when(fournisseurService.retrieveFournisseur(anyLong())).thenReturn(fournisseur);
        when(dtoMapper.toDTO(fournisseur)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-fournisseur/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFournisseur", is(1)))
                .andExpect(jsonPath("$.code", is("FRN-CTRL-001")))
                .andExpect(jsonPath("$.libelle", is("Controller Test Fournisseur")))
                .andExpect(jsonPath("$.categorieFournisseur", is("ORDINAIRE")));

        verify(fournisseurService, times(1)).retrieveFournisseur(1L);
        verify(dtoMapper, times(1)).toDTO(fournisseur);
    }

    @Test
    void testRetrieveFournisseur_NotFound() throws Exception {
        // Arrange
        when(fournisseurService.retrieveFournisseur(anyLong())).thenReturn(null);
        when(dtoMapper.toDTO((Fournisseur) null)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/fournisseur/retrieve-fournisseur/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(fournisseurService, times(1)).retrieveFournisseur(999L);
        verify(dtoMapper, times(1)).toDTO((Fournisseur) null);
    }

    @Test
    void testAddFournisseur_Success() throws Exception {
        // Arrange
        FournisseurDTO inputDto = new FournisseurDTO();
        inputDto.setCode("FRN-NEW-001");
        inputDto.setLibelle("New Fournisseur");
        inputDto.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN-NEW-001");
        newFournisseur.setLibelle("New Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        Fournisseur savedFournisseur = new Fournisseur();
        savedFournisseur.setIdFournisseur(2L);
        savedFournisseur.setCode("FRN-NEW-001");
        savedFournisseur.setLibelle("New Fournisseur");
        savedFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        FournisseurDTO savedDto = new FournisseurDTO();
        savedDto.setIdFournisseur(2L);
        savedDto.setCode("FRN-NEW-001");
        savedDto.setLibelle("New Fournisseur");
        savedDto.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        when(dtoMapper.toEntity(any(FournisseurDTO.class))).thenReturn(newFournisseur);
        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(savedFournisseur);
        when(dtoMapper.toDTO(savedFournisseur)).thenReturn(savedDto);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFournisseur", is(2)))
                .andExpect(jsonPath("$.code", is("FRN-NEW-001")))
                .andExpect(jsonPath("$.libelle", is("New Fournisseur")))
                .andExpect(jsonPath("$.categorieFournisseur", is("CONVENTIONNE")));

        verify(dtoMapper, times(1)).toEntity(any(FournisseurDTO.class));
        verify(fournisseurService, times(1)).addFournisseur(any(Fournisseur.class));
        verify(dtoMapper, times(1)).toDTO(savedFournisseur);
    }

    @Test
    void testModifyFournisseur_Success() throws Exception {
        // Arrange
        FournisseurDTO inputDto = new FournisseurDTO();
        inputDto.setIdFournisseur(1L);
        inputDto.setCode("FRN-CTRL-001");
        inputDto.setLibelle("Updated Fournisseur");
        inputDto.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        fournisseur.setLibelle("Updated Fournisseur");
        
        FournisseurDTO updatedDto = new FournisseurDTO();
        updatedDto.setIdFournisseur(1L);
        updatedDto.setCode("FRN-CTRL-001");
        updatedDto.setLibelle("Updated Fournisseur");
        updatedDto.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        when(dtoMapper.toEntity(any(FournisseurDTO.class))).thenReturn(fournisseur);
        when(fournisseurService.updateFournisseur(any(Fournisseur.class))).thenReturn(fournisseur);
        when(dtoMapper.toDTO(fournisseur)).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/fournisseur/modify-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFournisseur", is(1)))
                .andExpect(jsonPath("$.code", is("FRN-CTRL-001")))
                .andExpect(jsonPath("$.libelle", is("Updated Fournisseur")));

        verify(dtoMapper, times(1)).toEntity(any(FournisseurDTO.class));
        verify(fournisseurService, times(1)).updateFournisseur(any(Fournisseur.class));
        verify(dtoMapper, times(1)).toDTO(fournisseur);
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
        FournisseurDTO invalidDto = new FournisseurDTO();
        Fournisseur invalidFournisseur = new Fournisseur();

        when(dtoMapper.toEntity(any(FournisseurDTO.class))).thenReturn(invalidFournisseur);
        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(invalidFournisseur);
        when(dtoMapper.toDTO(invalidFournisseur)).thenReturn(invalidDto);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isOk());

        verify(dtoMapper, times(1)).toEntity(any(FournisseurDTO.class));
        verify(fournisseurService, times(1)).addFournisseur(any(Fournisseur.class));
        verify(dtoMapper, times(1)).toDTO(invalidFournisseur);
    }

    @Test
    void testAddFournisseur_ORDINAIRE_Category() throws Exception {
        // Arrange
        FournisseurDTO inputDto = new FournisseurDTO();
        inputDto.setCode("FRN-ORD-001");
        inputDto.setLibelle("Ordinaire Fournisseur");
        inputDto.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN-ORD-001");
        newFournisseur.setLibelle("Ordinaire Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        FournisseurDTO savedDto = new FournisseurDTO();
        savedDto.setCode("FRN-ORD-001");
        savedDto.setLibelle("Ordinaire Fournisseur");
        savedDto.setCategorieFournisseur(CategorieFournisseur.ORDINAIRE);

        when(dtoMapper.toEntity(any(FournisseurDTO.class))).thenReturn(newFournisseur);
        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(newFournisseur);
        when(dtoMapper.toDTO(newFournisseur)).thenReturn(savedDto);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorieFournisseur", is("ORDINAIRE")));

        verify(dtoMapper, times(1)).toEntity(any(FournisseurDTO.class));
        verify(fournisseurService, times(1)).addFournisseur(any(Fournisseur.class));
        verify(dtoMapper, times(1)).toDTO(newFournisseur);
    }

    @Test
    void testAddFournisseur_CONVENTIONNE_Category() throws Exception {
        // Arrange
        FournisseurDTO inputDto = new FournisseurDTO();
        inputDto.setCode("FRN-CONV-001");
        inputDto.setLibelle("Conventionne Fournisseur");
        inputDto.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        Fournisseur newFournisseur = new Fournisseur();
        newFournisseur.setCode("FRN-CONV-001");
        newFournisseur.setLibelle("Conventionne Fournisseur");
        newFournisseur.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        FournisseurDTO savedDto = new FournisseurDTO();
        savedDto.setCode("FRN-CONV-001");
        savedDto.setLibelle("Conventionne Fournisseur");
        savedDto.setCategorieFournisseur(CategorieFournisseur.CONVENTIONNE);

        when(dtoMapper.toEntity(any(FournisseurDTO.class))).thenReturn(newFournisseur);
        when(fournisseurService.addFournisseur(any(Fournisseur.class))).thenReturn(newFournisseur);
        when(dtoMapper.toDTO(newFournisseur)).thenReturn(savedDto);

        // Act & Assert
        mockMvc.perform(post("/fournisseur/add-fournisseur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorieFournisseur", is("CONVENTIONNE")));

        verify(dtoMapper, times(1)).toEntity(any(FournisseurDTO.class));
        verify(fournisseurService, times(1)).addFournisseur(any(Fournisseur.class));
        verify(dtoMapper, times(1)).toDTO(newFournisseur);
    }
}

