package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.ProduitDTO;
import tn.esprit.rh.achat.services.IProduitService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.List;


@RestController
@CrossOrigin("*")
@Api(tags = "Gestion des produits")
@RequestMapping("/produit")
public class ProduitRestController {

	@Autowired
	IProduitService produitService;

	@Autowired
	DTOMapper dtoMapper;

	@GetMapping("/retrieve-all-produits")
	@ResponseBody
	public List<ProduitDTO> getProduits() {
		return dtoMapper.toProduitDTOList(produitService.retrieveAllProduits());
	}

	@GetMapping("/retrieve-produit/{produit-id}")
	@ResponseBody
	public ProduitDTO retrieveRayon(@PathVariable("produit-id") Long produitId) {
		return dtoMapper.toDTO(produitService.retrieveProduit(produitId));
	}

	@PostMapping("/add-produit")
	@ResponseBody
	public ProduitDTO addProduit(@RequestBody ProduitDTO dto) {
		return dtoMapper.toDTO(produitService.addProduit(dtoMapper.toEntity(dto)));
	}

	@DeleteMapping("/remove-produit/{produit-id}")
	@ResponseBody
	public void removeProduit(@PathVariable("produit-id") Long produitId) {
		produitService.deleteProduit(produitId);
	}

	@PutMapping("/modify-produit")
	@ResponseBody
	public ProduitDTO modifyProduit(@RequestBody ProduitDTO dto) {
		return dtoMapper.toDTO(produitService.updateProduit(dtoMapper.toEntity(dto)));
	}
	// http://localhost:8089/SpringMVC/produit/assignProduitToStock/1/5
	@PutMapping(value = "/assignProduitToStock/{idProduit}/{idStock}")
	public void assignProduitToStock(@PathVariable("idProduit") Long idProduit, @PathVariable("idStock") Long idStock) {
		produitService.assignProduitToStock(idProduit, idStock);
	}

	/*
	 * Revenu Brut d'un produit (qte * prix unitaire de toutes les lignes du
	 * detailFacture du produit envoyé en paramètre )
	 */
	// http://localhost:8089/SpringMVC/produit/getRevenuBrutProduit/1/{startDate}/{endDate}
/*	@GetMapping(value = "/getRevenuBrutProduit/{idProduit}/{startDate}/{endDate}")
	public float getRevenuBrutProduit(@PathVariable("idProduit") Long idProduit,
			@PathVariable(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@PathVariable(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {

		return produitService.getRevenuBrutProduit(idProduit, startDate, endDate);
	}*/

}
