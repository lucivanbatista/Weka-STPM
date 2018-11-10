package weka;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import weka.gui.stpm.Config;
import weka.gui.stpm.TrajectoryFrame;

@Controller
public class SemanticController {

	@PostMapping("/semanticstart")
	public String semanticStart(Model model, String schema, String trajectoryTable, String trajectoryId, String detectionTime, String pointsInterest, 
			Double userBuff, Integer rfMinTime, String methodName, Double maxAvgSpeed, Integer minTime, Double maxSpeed,
			@RequestParam(value = "method") Integer method, String tableName){
		Config config = new Config(schema, trajectoryTable, trajectoryId, detectionTime, pointsInterest, userBuff, rfMinTime, 
				(method == 1) ? "SMoT" : "CB-SMoT", maxAvgSpeed, minTime, maxSpeed);
		model.addAttribute("config", config);
        TrajectoryFrame tf = new TrajectoryFrame(null, null, null, tableName, config);
        model.addAttribute("tablestops", tf.getCurrentNameTableStop());
        model.addAttribute("stops", tf.getStops());
		return "/data";
	}
	
	@RequestMapping("/")
	public String executar(){
		return "/semantic";
	}
}
