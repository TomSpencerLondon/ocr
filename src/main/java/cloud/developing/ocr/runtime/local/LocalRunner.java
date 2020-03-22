package cloud.developing.ocr.runtime.local;

import static cloud.developing.ocr.model.AnalysisStrategy.BIG_BOX;

import cloud.developing.ocr.Ocr;
import cloud.developing.ocr.model.OcrInput;

public class LocalRunner {

	public static void main(String[] args) throws Exception {
		var bucket = args[0];
		var key = args[1];
		var ocrOutput = new Ocr().recognize(new OcrInput(bucket, key, BIG_BOX));
		System.out.println(ocrOutput);
	}

}
