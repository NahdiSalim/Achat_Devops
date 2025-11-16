package tn.esprit.rh.achat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.rh.achat.entities.CategorieFournisseur;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FournisseurDTO {
    private Long idFournisseur;
    private String code;
    private String libelle;
    private CategorieFournisseur categorieFournisseur;
}

