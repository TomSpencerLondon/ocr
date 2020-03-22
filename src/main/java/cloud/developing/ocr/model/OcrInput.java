package cloud.developing.ocr.model;

import static cloud.developing.ocr.model.AnalysisStrategy.*;

public class OcrInput {

	private String bucket, key;

	private AnalysisStrategy analysisStrategy;

	public OcrInput(String bucket, String key) {
		this(bucket, key, DEFAULT);
	}

	public OcrInput(String bucket, String key, String analysisStrategy) {
		this(bucket, key, valueOf(analysisStrategy));
	}

	public OcrInput(String bucket, String key, AnalysisStrategy analysisStrategy) {
		this.bucket = bucket;
		this.key = key;
		this.analysisStrategy = analysisStrategy;
	}

	public String bucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String key() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public AnalysisStrategy analysisStrategy() {
		return analysisStrategy;
	}

	public void setAnalysisStrategy(AnalysisStrategy analysisStrategy) {
		this.analysisStrategy = analysisStrategy;
	}

	@Override
	public String toString() {
		return "OcrInput [bucket=" + bucket + ", key=" + key + ", analysisStrategy=" + analysisStrategy + "]";
	}

}
