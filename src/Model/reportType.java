package Model;

/**
 * @author Ashley Jette
 */
public class reportType {
    private String appType;
    private int appTotal;

    /**
     * report type constructor
     * @param appType - appointment type
     * @param appTotal - total number of appointments
     */
    public reportType(String appType, int appTotal) {
        this.appType = appType;
        this.appTotal = appTotal;
    }

    /**
     * appointment type getter
     * @return appointment type
     */
    public String getAppType() {
        return appType;
    }

    /**
     * total appointments getter
     * @return total appointments
     */
    public int getAppTotal() {
        return appTotal;
    }
}
