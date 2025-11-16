package tn.esprit.rh.achat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Long idStock;
    private String libelleStock;
    private Integer qte;
    private Integer qteMin;
}

