package uk.gov.hmrc.rules.ir;

public class EmitErrorActionNode extends ActionNode {

    private String brCode;
    private String dmsErrorCode;

    public String getBrCode() {
        return brCode;
    }

    public void setBrCode(String brCode) {
        this.brCode = brCode;
    }

    public String getDmsErrorCode() {
        return dmsErrorCode;
    }

    public void setDmsErrorCode(String dmsErrorCode) {
        this.dmsErrorCode = dmsErrorCode;
    }

    @Override
    public String toString() {
        return "EmitErrorActionNode{" +
               "brCode='" + brCode + '\'' +
               ", dmsErrorCode='" + dmsErrorCode + '\'' +
               '}';
    }
}
