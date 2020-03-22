package cloud.developing.ocr;

import static cloud.developing.ocr.model.AnalysisStrategy.BIG_BOX;
import static cloud.developing.ocr.model.AnalysisStrategy.DEFAULT;
import static cloud.developing.ocr.model.RecognitionCode.TEXT_FOUND;
import static cloud.developing.ocr.model.RecognitionCode.TEXT_NOT_FOUND;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import cloud.developing.ocr.model.AnalysisStrategy;
import cloud.developing.ocr.model.OcrInput;
import cloud.developing.ocr.model.OcrOutput;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.Geometry;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

public class Ocr {

	private static final Comparator<TextDetection> areaComparator = (td1,
	        td2) -> getArea(td1.geometry()) > getArea(td2.geometry()) ? -1 : 1;

	private static final Comparator<TextDetection> leftComparator = (td1,
	        td2) -> getLeft(td1) > td2.geometry().boundingBox().left() ? 1 : -1;

	private static final String BLANK = "";

	private static final Map<AnalysisStrategy, Function<DetectTextResponse, String>> ANALYSIS_STRATEGIES = new ConcurrentHashMap<>();

	private static final Function<DetectTextResponse, String> DEFAULT_STRATEGY = resp -> resp.hasTextDetections()
	        ? resp.textDetections().stream().filter(td -> td.parentId() == null).map(td -> td.detectedText()).collect(
	                joining(" "))
	        : BLANK;

	static {
		ANALYSIS_STRATEGIES.put(DEFAULT, DEFAULT_STRATEGY);
		ANALYSIS_STRATEGIES.put(BIG_BOX, resp -> {
			Optional<TextDetection> bigestBoxOptional = resp.hasTextDetections()
			        ? resp.textDetections().stream().sorted(areaComparator).findFirst()
			        : empty();

			var text = BLANK;
			if (bigestBoxOptional.isPresent()) {
				var bigestBox = bigestBoxOptional.get();
				var y = round(getTop(bigestBox));
				text = resp.textDetections().stream()
				        .filter(tdTop -> round(getTop(tdTop)) == y && tdTop.parentId() == null).sorted(leftComparator)
				        .map(tdy -> tdy.detectedText()).collect(joining()).replaceAll("[\\W]", BLANK);
			}
			return text;
		});

	}

	private final RekognitionClient rekognition = RekognitionClient.create();

	public OcrOutput recognize(OcrInput ocrInput) {
		var resp = rekognition.detectText(DetectTextRequest.builder()
		        .image(i -> i.s3Object(o -> o.bucket(ocrInput.bucket()).name(ocrInput.key()))).build());
		var text = ANALYSIS_STRATEGIES.getOrDefault(ocrInput.analysisStrategy(), DEFAULT_STRATEGY).apply(resp);
		return new OcrOutput(text.isBlank() ? TEXT_NOT_FOUND : TEXT_FOUND, text);
	}

	private static final double getArea(Geometry geometry) {
		var bb = geometry.boundingBox();
		return bb.height().doubleValue() * bb.width().doubleValue();
	}

	private static final double round(double d) {
		return new BigDecimal(d).setScale(2, HALF_UP).doubleValue();
	}

	private static final double getLeft(TextDetection td) {
		return td.geometry().boundingBox().left().doubleValue();
	}

	private static final double getTop(TextDetection td) {
		return td.geometry().boundingBox().top().doubleValue();
	}

}
