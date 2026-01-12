package uk.gov.hmrc.rules.parsing.logic;

public final class LogicExtractionResult {

    private final String ifText;
    private final String thenText;
    private final String messageText;

    private LogicExtractionResult(String ifText, String thenText, String messageText) {
        this.ifText = safe(ifText);
        this.thenText = safe(thenText);
        this.messageText = safe(messageText);
    }

    public static LogicExtractionResult of(String ifText, String thenText, String messageText) {
        return new LogicExtractionResult(ifText, thenText, messageText);
    }

    public String ifText() {
        return ifText;
    }

    public String thenText() {
        return thenText;
    }

    public String messageText() {
        return messageText;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    @Override
    public String toString() {
        return "LogicExtractionResult{ifText='" + ifText + "', thenText='" + thenText + "', messageText='" + messageText + "'}";
    }
}
