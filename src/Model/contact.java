package Model;

/**
 * @author Ashley Jette
 */

public class contact {
    public int contactId;
    public String contactName;
    public String contactEmail;

    /**
     * contact Constructor
     * @param contactId - contact id number
     * @param contactName - contact name
     * @param contactEmail - contact email address
     */

    public contact(int contactId, String contactName, String contactEmail){
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * contact id getter
     * @return contactId
     */
    public int getContactId() {
        return contactId;
    }
    /**
     * contact name getter
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }
    /**
     * contact email getter
     * @return contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }
}
