package Model;

/**
 * @author Ashley Jette
 */
public class countries {
    private int countryId;
    private String countryName;

    /**
     * country constructor
     * @param countryId - country id number
     * @param countryName - country name
     */
    public countries(int countryId, String countryName) {
        this.countryName = countryName;
        this.countryId = countryId;
    }

    /**
     *
     * @return country id
     */
    public int getCountryId() {
        return countryId;
    }
    /**
     *
     * @return country name
     */
    public String getCountryName() {
        return countryName;
    }
}
