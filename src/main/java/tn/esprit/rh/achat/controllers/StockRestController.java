package tn.esprit.rh.achat.controllers;


import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rh.achat.dto.StockDTO;
import tn.esprit.rh.achat.services.IStockService;
import tn.esprit.rh.achat.util.DTOMapper;

import java.util.List;

@RestController
@Api(tags = "Gestion des stocks")
@RequestMapping("/stock")
@CrossOrigin("*")
public class StockRestController {

	@Autowired
	IStockService stockService;

	@Autowired
	DTOMapper dtoMapper;

	@GetMapping("/retrieve-all-stocks")
	@ResponseBody
	public List<StockDTO> getStocks() {
		return dtoMapper.toStockDTOList(stockService.retrieveAllStocks());
	}

	@GetMapping("/retrieve-stock/{stock-id}")
	@ResponseBody
	public StockDTO retrieveStock(@PathVariable("stock-id") Long stockId) {
		return dtoMapper.toDTO(stockService.retrieveStock(stockId));
	}

	@PostMapping("/add-stock")
	@ResponseBody
	public StockDTO addStock(@RequestBody StockDTO dto) {
		return dtoMapper.toDTO(stockService.addStock(dtoMapper.toEntity(dto)));
	}

	@DeleteMapping("/remove-stock/{stock-id}")
	@ResponseBody
	public void removeStock(@PathVariable("stock-id") Long stockId) {
		stockService.deleteStock(stockId);
	}

	@PutMapping("/modify-stock")
	@ResponseBody
	public StockDTO modifyStock(@RequestBody StockDTO dto) {
		return dtoMapper.toDTO(stockService.updateStock(dtoMapper.toEntity(dto)));
	}
	// http://localhost:8089/SpringMVC/stock/retrieveStatusStock
	// @Scheduled(fixedRate = 60000)
	// @Scheduled(fixedDelay = 60000)
	//@Scheduled(cron = "*/60 * * * * *")
	//@GetMapping("/retrieveStatusStock")
//	@ResponseBody
//	public void retrieveStatusStock() {
//		stockService.retrieveStatusStock();
//	}

}