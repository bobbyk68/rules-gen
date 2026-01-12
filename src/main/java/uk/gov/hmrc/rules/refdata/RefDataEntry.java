package uk.gov.hmrc.rules.refdata;

public class RefDataEntry {

    private String listName;
    private String value;

    public RefDataEntry(String listName, String value) {
        this.listName = listName;
        this.value = value;
    }

    public String getListName() {
        return listName;
    }

    public String getValue() {
        return value;
    }
}
