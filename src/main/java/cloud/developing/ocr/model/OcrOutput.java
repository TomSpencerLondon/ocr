package cloud.developing.ocr.model;

public class OcrOutput {

	private static final String BLANK = "";

	private String text, message;

	private RecognitionCode recognitionCode;

	public OcrOutput() {

	}

	public OcrOutput(RecognitionCode recognitionCode) {
		this(recognitionCode, BLANK, BLANK);
	}

	public OcrOutput(RecognitionCode recognitionCode, String text) {
		this(recognitionCode, text, BLANK);
	}

	public OcrOutput(RecognitionCode recognitionCode, String text, String message) {
		this.recognitionCode = recognitionCode;
		this.text = text;
		this.message = message;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public RecognitionCode getRecognitionCode() {
		return recognitionCode;
	}

	public void setRecognitionCode(RecognitionCode recognitionCode) {
		this.recognitionCode = recognitionCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "OcrOutput [text=" + text + ", recognitionCode=" + recognitionCode + ", message=" + message + "]";
	}

}
