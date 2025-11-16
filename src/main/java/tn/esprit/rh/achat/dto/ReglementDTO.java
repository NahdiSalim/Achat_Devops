package tn.esprit.rh.achat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReglementDTO {
    private Long idReglement;
    private float montantPaye;
    private float montantRestant;
    private Date dateReglement;
    private Boolean payee;
}

