package cloud.developing.ocr.runtime.lambda;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import cloud.developing.ocr.Ocr;
import cloud.developing.ocr.model.OcrInput;

public class LambdaRunner implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream is, OutputStream os, Context context) throws IOException {
		var log = context.getLogger();
		JSONObject input = new JSONObject(new JSONTokener(is));
		log.log("input: " + input.toString());

		var analysisStrategyKey = "analysisStrategy";
		var bucket = input.getString("bucket");
		var key = input.getString("key");

		var ocrOutput = new Ocr().recognize(
		        input.has(analysisStrategyKey) ? new OcrInput(bucket, key, input.getString(analysisStrategyKey))
		                : new OcrInput(bucket, key));
		JSONObject output = new JSONObject(ocrOutput);

		var outputAsString = output.toString();
		log.log("output:" + outputAsString);
		os.write(outputAsString.getBytes(UTF_8));
	}

}
