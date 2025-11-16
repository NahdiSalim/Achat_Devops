package tn.esprit.rh.achat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategorieProduitDTO {
    private Long idCategorieProduit;
    private String codeCategorie;
    private String libelleCategorie;
}

