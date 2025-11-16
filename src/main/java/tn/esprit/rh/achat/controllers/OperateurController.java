package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.OperateurDTO;
import tn.esprit.rh.achat.services.IOperateurService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.List;

@RestController
@Api(tags = "Gestion des opérateurs")
@RequestMapping("/operateur")
@CrossOrigin("*")
public class OperateurController {

	@Autowired
	IOperateurService operateurService;

	@Autowired
	DTOMapper dtoMapper;
	
	@GetMapping("/retrieve-all-operateurs")
	@ResponseBody
	public List<OperateurDTO> getOperateurs() {
		return dtoMapper.toOperateurDTOList(operateurService.retrieveAllOperateurs());
	}

	@GetMapping("/retrieve-operateur/{operateur-id}")
	@ResponseBody
	public OperateurDTO retrieveOperateur(@PathVariable("operateur-id") Long operateurId) {
		return dtoMapper.toDTO(operateurService.retrieveOperateur(operateurId));
	}

	@PostMapping("/add-operateur")
	@ResponseBody
	public OperateurDTO addOperateur(@RequestBody OperateurDTO dto) {
		return dtoMapper.toDTO(operateurService.addOperateur(dtoMapper.toEntity(dto)));
	}

	@DeleteMapping("/remove-operateur/{operateur-id}")
	@ResponseBody
	public void removeOperateur(@PathVariable("operateur-id") Long operateurId) {
		operateurService.deleteOperateur(operateurId);
	}

	@PutMapping("/modify-operateur")
	@ResponseBody
	public OperateurDTO modifyOperateur(@RequestBody OperateurDTO dto) {
		return dtoMapper.toDTO(operateurService.updateOperateur(dtoMapper.toEntity(dto)));
	}

	
}
