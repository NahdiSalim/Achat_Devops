package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.CategorieProduitDTO;
import tn.esprit.rh.achat.services.ICategorieProduitService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.List;

@RestController
@Api(tags = "Gestion des categories Produit")
@RequestMapping("/categorieProduit")
public class CategorieProduitController {

	@Autowired
	ICategorieProduitService categorieProduitService;

	@Autowired
	DTOMapper dtoMapper;
	
	@GetMapping("/retrieve-all-categorieProduit")
	@ResponseBody
	public List<CategorieProduitDTO> getCategorieProduit() {
		return dtoMapper.toCategorieProduitDTOList(categorieProduitService.retrieveAllCategorieProduits());
	}

	@GetMapping("/retrieve-categorieProduit/{categorieProduit-id}")
	@ResponseBody
	public CategorieProduitDTO retrieveCategorieProduit(@PathVariable("categorieProduit-id") Long categorieProduitId) {
		return dtoMapper.toDTO(categorieProduitService.retrieveCategorieProduit(categorieProduitId));
	}

	@PostMapping("/add-categorieProduit")
	@ResponseBody
	public CategorieProduitDTO addCategorieProduit(@RequestBody CategorieProduitDTO dto) {
		return dtoMapper.toDTO(categorieProduitService.addCategorieProduit(dtoMapper.toEntity(dto)));
	}

	@DeleteMapping("/remove-categorieProduit/{categorieProduit-id}")
	@ResponseBody
	public void removeCategorieProduit(@PathVariable("categorieProduit-id") Long categorieProduitId) {
		categorieProduitService.deleteCategorieProduit(categorieProduitId);
	}

	@PutMapping("/modify-categorieProduit")
	@ResponseBody
	public CategorieProduitDTO modifyCategorieProduit(@RequestBody CategorieProduitDTO dto) {
		return dtoMapper.toDTO(categorieProduitService.updateCategorieProduit(dtoMapper.toEntity(dto)));
	}

	
}
