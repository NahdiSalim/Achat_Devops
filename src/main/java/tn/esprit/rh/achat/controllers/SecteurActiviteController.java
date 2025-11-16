package tn.esprit.rh.achat.controllers;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.SecteurActiviteDTO;
import tn.esprit.rh.achat.services.ISecteurActiviteService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.List;

@RestController
@Api(tags = "Gestion des secteurs activites")
@RequestMapping("/secteurActivite")
@CrossOrigin("*")
public class SecteurActiviteController {

	@Autowired
	ISecteurActiviteService secteurActiviteService;

	@Autowired
	DTOMapper dtoMapper;
	
	@GetMapping("/retrieve-all-secteurActivite")
	@ResponseBody
	public List<SecteurActiviteDTO> getSecteurActivite() {
		return dtoMapper.toSecteurActiviteDTOList(secteurActiviteService.retrieveAllSecteurActivite());
	}

	@GetMapping("/retrieve-secteurActivite/{secteurActivite-id}")
	@ResponseBody
	public SecteurActiviteDTO retrieveSecteurActivite(@PathVariable("secteurActivite-id") Long secteurActiviteId) {
		return dtoMapper.toDTO(secteurActiviteService.retrieveSecteurActivite(secteurActiviteId));
	}

	@PostMapping("/add-secteurActivite")
	@ResponseBody
	public SecteurActiviteDTO addSecteurActivite(@RequestBody SecteurActiviteDTO dto) {
		return dtoMapper.toDTO(secteurActiviteService.addSecteurActivite(dtoMapper.toEntity(dto)));
	}

	@DeleteMapping("/remove-secteurActivite/{secteurActivite-id}")
	@ResponseBody
	public void removeSecteurActivite(@PathVariable("secteurActivite-id") Long secteurActiviteId) {
		secteurActiviteService.deleteSecteurActivite(secteurActiviteId);
	}

	@PutMapping("/modify-secteurActivite")
	@ResponseBody
	public SecteurActiviteDTO modifySecteurActivite(@RequestBody SecteurActiviteDTO dto) {
		return dtoMapper.toDTO(secteurActiviteService.updateSecteurActivite(dtoMapper.toEntity(dto)));
	}

	
}
