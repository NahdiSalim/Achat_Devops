package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.ReglementDTO;
import tn.esprit.rh.achat.services.IReglementService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "Gestion des reglements")
@RequestMapping("/reglement")
@CrossOrigin("*")
public class ReglementRestController {

    @Autowired
    IReglementService reglementService;

    @Autowired
    DTOMapper dtoMapper;

    @PostMapping("/add-reglement")
    @ResponseBody
    public ReglementDTO addReglement(@RequestBody ReglementDTO dto) {
        return dtoMapper.toDTO(reglementService.addReglement(dtoMapper.toEntity(dto)));
    }

    @GetMapping("/retrieve-all-reglements")
    @ResponseBody
    public List<ReglementDTO> getReglement() {
        return dtoMapper.toReglementDTOList(reglementService.retrieveAllReglements());
    }

    @GetMapping("/retrieve-reglement/{reglement-id}")
    @ResponseBody
    public ReglementDTO retrieveReglement(@PathVariable("reglement-id") Long reglementId) {
        return dtoMapper.toDTO(reglementService.retrieveReglement(reglementId));
    }

    @GetMapping("/retrieveReglementByFacture/{facture-id}")
    @ResponseBody
    public List<ReglementDTO> retrieveReglementByFacture(@PathVariable("facture-id") Long factureId) {
        return dtoMapper.toReglementDTOList(reglementService.retrieveReglementByFacture(factureId));
    }

    // http://localhost:8089/SpringMVC/reglement/getChiffreAffaireEntreDeuxDate/{startDate}/{endDate}
    @GetMapping(value = "/getChiffreAffaireEntreDeuxDate/{startDate}/{endDate}")
    public float getChiffreAffaireEntreDeuxDate(
            @PathVariable(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @PathVariable(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        try {
            return reglementService.getChiffreAffaireEntreDeuxDate(startDate, endDate);
        } catch (Exception e) {
            return 0;
        }
    }
}
