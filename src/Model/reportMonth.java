package Model;

/**
 * @author Ashley Jette
 */
public class reportMonth {
    private String appMonth;
    private int appTotal;

    /**
     * report month constructor
     * @param appMonth appointment month
     * @param appTotal total number of appointments
     */
    public reportMonth(String appMonth, int appTotal) {
        this.appMonth = appMonth;
        this.appTotal = appTotal;
    }

    /**
     * appointment month getter
     * @return appointment month
     */
    public String getAppMonth() {
        return appMonth;
    }

    /**
     * total appointments getter
     * @return total appointments
     */
    public int getAppTotal() {
        return appTotal;
    }
}
