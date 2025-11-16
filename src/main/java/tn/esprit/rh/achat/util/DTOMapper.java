package tn.esprit.rh.achat.util;

import org.springframework.stereotype.Component;
import tn.esprit.rh.achat.dto.*;
import tn.esprit.rh.achat.entities.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DTOMapper {

    // Fournisseur mappings
    public FournisseurDTO toDTO(Fournisseur entity) {
        if (entity == null) return null;
        FournisseurDTO dto = new FournisseurDTO();
        dto.setIdFournisseur(entity.getIdFournisseur());
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setCategorieFournisseur(entity.getCategorieFournisseur());
        return dto;
    }

    public Fournisseur toEntity(FournisseurDTO dto) {
        if (dto == null) return null;
        Fournisseur entity = new Fournisseur();
        entity.setIdFournisseur(dto.getIdFournisseur());
        entity.setCode(dto.getCode());
        entity.setLibelle(dto.getLibelle());
        entity.setCategorieFournisseur(dto.getCategorieFournisseur());
        return entity;
    }

    public List<FournisseurDTO> toFournisseurDTOList(List<Fournisseur> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Produit mappings
    public ProduitDTO toDTO(Produit entity) {
        if (entity == null) return null;
        ProduitDTO dto = new ProduitDTO();
        dto.setIdProduit(entity.getIdProduit());
        dto.setCodeProduit(entity.getCodeProduit());
        dto.setLibelleProduit(entity.getLibelleProduit());
        dto.setPrix(entity.getPrix());
        dto.setDateCreation(entity.getDateCreation());
        dto.setDateDerniereModification(entity.getDateDerniereModification());
        return dto;
    }

    public Produit toEntity(ProduitDTO dto) {
        if (dto == null) return null;
        Produit entity = new Produit();
        entity.setIdProduit(dto.getIdProduit());
        entity.setCodeProduit(dto.getCodeProduit());
        entity.setLibelleProduit(dto.getLibelleProduit());
        entity.setPrix(dto.getPrix());
        entity.setDateCreation(dto.getDateCreation());
        entity.setDateDerniereModification(dto.getDateDerniereModification());
        return entity;
    }

    public List<ProduitDTO> toProduitDTOList(List<Produit> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Stock mappings
    public StockDTO toDTO(Stock entity) {
        if (entity == null) return null;
        StockDTO dto = new StockDTO();
        dto.setIdStock(entity.getIdStock());
        dto.setLibelleStock(entity.getLibelleStock());
        dto.setQte(entity.getQte());
        dto.setQteMin(entity.getQteMin());
        return dto;
    }

    public Stock toEntity(StockDTO dto) {
        if (dto == null) return null;
        Stock entity = new Stock();
        entity.setIdStock(dto.getIdStock());
        entity.setLibelleStock(dto.getLibelleStock());
        entity.setQte(dto.getQte());
        entity.setQteMin(dto.getQteMin());
        return entity;
    }

    public List<StockDTO> toStockDTOList(List<Stock> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Operateur mappings
    public OperateurDTO toDTO(Operateur entity) {
        if (entity == null) return null;
        OperateurDTO dto = new OperateurDTO();
        dto.setIdOperateur(entity.getIdOperateur());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    public Operateur toEntity(OperateurDTO dto) {
        if (dto == null) return null;
        Operateur entity = new Operateur();
        entity.setIdOperateur(dto.getIdOperateur());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setPassword(dto.getPassword());
        return entity;
    }

    public List<OperateurDTO> toOperateurDTOList(List<Operateur> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // CategorieProduit mappings
    public CategorieProduitDTO toDTO(CategorieProduit entity) {
        if (entity == null) return null;
        CategorieProduitDTO dto = new CategorieProduitDTO();
        dto.setIdCategorieProduit(entity.getIdCategorieProduit());
        dto.setCodeCategorie(entity.getCodeCategorie());
        dto.setLibelleCategorie(entity.getLibelleCategorie());
        return dto;
    }

    public CategorieProduit toEntity(CategorieProduitDTO dto) {
        if (dto == null) return null;
        CategorieProduit entity = new CategorieProduit();
        entity.setIdCategorieProduit(dto.getIdCategorieProduit());
        entity.setCodeCategorie(dto.getCodeCategorie());
        entity.setLibelleCategorie(dto.getLibelleCategorie());
        return entity;
    }

    public List<CategorieProduitDTO> toCategorieProduitDTOList(List<CategorieProduit> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // SecteurActivite mappings
    public SecteurActiviteDTO toDTO(SecteurActivite entity) {
        if (entity == null) return null;
        SecteurActiviteDTO dto = new SecteurActiviteDTO();
        dto.setIdSecteurActivite(entity.getIdSecteurActivite());
        dto.setCodeSecteurActivite(entity.getCodeSecteurActivite());
        dto.setLibelleSecteurActivite(entity.getLibelleSecteurActivite());
        return dto;
    }

    public SecteurActivite toEntity(SecteurActiviteDTO dto) {
        if (dto == null) return null;
        SecteurActivite entity = new SecteurActivite();
        entity.setIdSecteurActivite(dto.getIdSecteurActivite());
        entity.setCodeSecteurActivite(dto.getCodeSecteurActivite());
        entity.setLibelleSecteurActivite(dto.getLibelleSecteurActivite());
        return entity;
    }

    public List<SecteurActiviteDTO> toSecteurActiviteDTOList(List<SecteurActivite> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Facture mappings
    public FactureDTO toDTO(Facture entity) {
        if (entity == null) return null;
        FactureDTO dto = new FactureDTO();
        dto.setIdFacture(entity.getIdFacture());
        dto.setMontantRemise(entity.getMontantRemise());
        dto.setMontantFacture(entity.getMontantFacture());
        dto.setDateCreationFacture(entity.getDateCreationFacture());
        dto.setDateDerniereModificationFacture(entity.getDateDerniereModificationFacture());
        dto.setArchivee(entity.getArchivee());
        return dto;
    }

    public Facture toEntity(FactureDTO dto) {
        if (dto == null) return null;
        Facture entity = new Facture();
        entity.setIdFacture(dto.getIdFacture());
        entity.setMontantRemise(dto.getMontantRemise());
        entity.setMontantFacture(dto.getMontantFacture());
        entity.setDateCreationFacture(dto.getDateCreationFacture());
        entity.setDateDerniereModificationFacture(dto.getDateDerniereModificationFacture());
        entity.setArchivee(dto.getArchivee());
        return entity;
    }

    public List<FactureDTO> toFactureDTOList(List<Facture> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}

