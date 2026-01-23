package uk.gov.hmrc.rules.br455.reflection;

public final class ResolvedScalarPath {

    // Version: 2026-01-23 v1.0.0

    private final String path;
    private final String reason;

    public ResolvedScalarPath(String path, String reason) {
        this.path = path;
        this.reason = reason;
    }

    public String getPath() {
        return path;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ResolvedScalarPath{path='" + path + "', reason='" + reason + "'}";
    }
}
