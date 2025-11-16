package tn.esprit.rh.achat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecteurActiviteDTO {
    private Long idSecteurActivite;
    private String codeSecteurActivite;
    private String libelleSecteurActivite;
}

