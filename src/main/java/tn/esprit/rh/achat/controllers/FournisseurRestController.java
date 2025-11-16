package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.FournisseurDTO;
import tn.esprit.rh.achat.services.IFournisseurService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.List;


@RestController
@Api(tags = "Gestion des fournisseurss")
@RequestMapping("/fournisseur")
public class FournisseurRestController {

	@Autowired
	IFournisseurService fournisseurService;

	@Autowired
	DTOMapper dtoMapper;

	@GetMapping("/retrieve-all-fournisseurs")
	@ResponseBody
	public List<FournisseurDTO> getFournisseurs() {
		return dtoMapper.toFournisseurDTOList(fournisseurService.retrieveAllFournisseurs());
	}

	@GetMapping("/retrieve-fournisseur/{fournisseur-id}")
	@ResponseBody
	public FournisseurDTO retrieveFournisseur(@PathVariable("fournisseur-id") Long fournisseurId) {
		return dtoMapper.toDTO(fournisseurService.retrieveFournisseur(fournisseurId));
	}

	@PostMapping("/add-fournisseur")
	@ResponseBody
	public FournisseurDTO addFournisseur(@RequestBody FournisseurDTO dto) {
		return dtoMapper.toDTO(fournisseurService.addFournisseur(dtoMapper.toEntity(dto)));
	}

	@DeleteMapping("/remove-fournisseur/{fournisseur-id}")
	@ResponseBody
	public void removeFournisseur(@PathVariable("fournisseur-id") Long fournisseurId) {
		fournisseurService.deleteFournisseur(fournisseurId);
	}

	@PutMapping("/modify-fournisseur")
	@ResponseBody
	public FournisseurDTO modifyFournisseur(@RequestBody FournisseurDTO dto) {
		return dtoMapper.toDTO(fournisseurService.updateFournisseur(dtoMapper.toEntity(dto)));
	}

	// http://localhost:8089/SpringMVC/fournisseur/assignSecteurActiviteToFournisseur/1/5
		@PutMapping(value = "/assignSecteurActiviteToFournisseur/{idSecteurActivite}/{idFournisseur}")
		public void assignProduitToStock(@PathVariable("idSecteurActivite") Long idSecteurActivite, @PathVariable("idFournisseur") Long idFournisseur) {
			fournisseurService.assignSecteurActiviteToFournisseur(idSecteurActivite, idFournisseur);
		}

}
