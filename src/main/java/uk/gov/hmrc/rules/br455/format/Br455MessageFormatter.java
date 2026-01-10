package uk.gov.hmrc.rules.br455.format;

public final class Br455MessageFormatter {

    public String buildMessage(String domainPath, boolean mustExist, String listName) {
        String tail = stripRoot(domainPath);
        String readable = dotsToSpaces(tail);

        String verb = mustExist ? "must exist in list " : "must not exist in list ";
        return readable + " " + verb + listName;
    }

    private String stripRoot(String path) {
        if (path == null) return "";
        int firstDot = path.indexOf('.');
        return firstDot < 0 ? path : path.substring(firstDot + 1);
    }

    private String dotsToSpaces(String path) {
        if (path == null) return "";
        return path.replace('.', ' ').trim();
    }
}
