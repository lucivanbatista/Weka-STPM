package weka.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import weka.gui.stpm.Config;
import weka.gui.stpm.TrajectoryFrame;
import weka.model.PointStop;
import weka.model.Tabela;
import weka.repository.SemanticRepository;

@Controller
public class SemanticController {
	
	private TrajectoryFrame tf = null;
	
	@Autowired
	private SemanticRepository semanticRepository;
	
	// Executa o enriquecimento semântico / Run semantic enrichment
	@PostMapping("/semanticstart")
	public String semanticStart(Model model, RedirectAttributes redirectAttributes, String schema, String trajectoryTable, String trajectoryId, String detectionTime, String pointsInterest, 
			Double userBuff, Integer rfMinTime, String methodName, Double maxAvgSpeed, Integer minTime, Double maxSpeed,
			@RequestParam(value = "method") Integer method, String tableName){
		Config config = new Config(schema, trajectoryTable, trajectoryId, detectionTime, pointsInterest, userBuff, rfMinTime, 
				(method == 1) ? "SMoT" : "CB-SMoT", maxAvgSpeed, minTime, maxSpeed);
		model.addAttribute("config", config);
        tf = new TrajectoryFrame(null, null, null, tableName, config);
        model.addAttribute("msg", "Sucesso!");
        model.addAttribute("tabelas", tf.getTables());
		return "/data";
	}	
	
	// Main Page
	@RequestMapping("/")
	public String executar(){
		return "/index";
	}
	
	// NÃO ESTÁ SENDO UTILIZADO / NOT BEING USED
	@RequestMapping("/data")
	public ModelAndView dados(ModelAndView model) {
		List<Tabela> t = new ArrayList<>();
		t.add(new Tabela("testeABC"));
		
		List<PointStop> stops = new ArrayList<>();
		stops.add(new PointStop(39.9220529553753,116.43993444234));
		
//		model.addAttribute("point", stops);
		model.addObject("temp", t);
		model.addObject("point", stops);
		return model;
	}
	
	// Mostrar resultados / Show Results
	@PostMapping("/getstops")
	public String getPointStops(Model model, @RequestParam("tableName") String tableName, @RequestParam("limit") int limit) {
		model.addAttribute("tabelas", tf.getTables());
		model.addAttribute("tableName", tableName);
		model.addAttribute("stops", tf.getPointStops(tableName, limit));
		return "/data";
	}
	
	// Page to Semantic Enrichment
	@RequestMapping("/semantic")
	public String semantic() {
		return "/semantic";
	}
	
	// NÃO ESTÁ SENDO UTILIZADO / NOT BEING USED
	@RequestMapping("/map")
	public ModelAndView mapa(ModelAndView model) {
		return model;
	}
	
	// About the page
	@RequestMapping("/sobre")
	public String sobre() {
		return "/sobre";
	}
}
