package Model;

/**
 * @author Ashley Jette
 */
public class divisions {
    private String divisionName;
    private String countryName;

    /**
     * division constructor
     * @param divisionName - division name
     * @param countryName - country name
     */
    public divisions(String countryName, String divisionName) {
        this.countryName = countryName;
        this.divisionName = divisionName;
    }

    /**
     * country name getter
     * @return country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * division name getter
     * @return division name
     */
    public String getDivisionName() {
        return divisionName;
    }

}
