package sjsu.cloud.cohort10.dto;

import java.io.Serializable;

public class AnalyzeRekognitionOutput implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String detectedText;

	public String getDetectedText() {
		return detectedText;
	}

	public void setDetectedText(String detectedText) {
		this.detectedText = detectedText;
	}
	
	

}
