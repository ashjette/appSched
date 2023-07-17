package Model;

/**
 * @author Ashley Jette
 */

public class customer {
    private int custId;
    private String divisionName;
    private String custName;
    private String custAddress;
    private String custPostal;
    private String custPhone;
    private String divisionId;
    private String custCountry;

    /**
     * customer constructor
     * @param custId - customer id number
     * @param divisionName - division name
     * @param custName - customer name
     * @param custAddress - customer address
     * @param custPostal - customer postal code
     * @param custPhone - customer phone number
     * @param custCountry - customer country
     */

    public customer(int custId, String custName, String custAddress, String custPhone, String custPostal, String divisionName, String custCountry) {
        this.custId = custId;
        this.divisionName = divisionName;
        this.custName = custName;
        this.custAddress = custAddress;
        this.custPostal = custPostal;
        this.custPhone = custPhone;
        this.custCountry = custCountry;
    }

    /**
     * customer country getter
     * @return customer country
     */
    public String getCustCountry() {
        return custCountry;
    }

    /**
     * customer country setter
     * @param custCountry
     */
    public void setCustCountry(String custCountry) {
        this.custCountry = custCountry;
    }

    /**
     * customer id getter
     * @return customer id
     */
    public int getCustId() {
        return custId;
    }

    /**
     * customer id setter
     * @param custId customer id number
     */
    public void setCustId(int custId) {
        this.custId = custId;
    }

    /**
     * division name getter
     * @return division name
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * division name setter
     * @param divisionName division name
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * customer name getter
     * @return customer name
     */
    public String getCustName() {
        return custName;
    }

    /**
     * customer name setter
     * @param custName customer name
     */
    public void setCustName(String custName) {
        this.custName = custName;
    }

    /**
     * customer address getter
     * @return customer address
     */
    public String getCustAddress() {
        return custAddress;
    }

    /**
     * customer address setter
     * @param custAddress customer address
     */
    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    /**
     * customer postal address getter
     * @return customer postal address
     */
    public String getCustPostal() {
        return custPostal;
    }

    /**
     * customer postal address setter
     * @param custPostal customer postal address
     */
    public void setCustPostal(String custPostal) {
        this.custPostal = custPostal;
    }

    /**
     * customer phone number getter
     * @return customer phone number
     */
    public String getCustPhone() {
        return custPhone;
    }

    /**
     * customer phone number setter
     * @param custPhone customer phone number
     */
    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    /**
     * division id getter
     * @return division id
     */
    public String getDivisionId() {
        return divisionId;
    }

    /**
     * division id setter
     * @param divisionId division id
     */
    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }
}
