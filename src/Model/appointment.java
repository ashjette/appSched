package Model;

import java.time.LocalDateTime;

/**
 * @author Ashley Jette
 */
public class appointment {
    private int appId;
    private String appTitle;
    private String appDescription;
    private String appLocation;
    private int contact;
    private String appType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int custId;
    private int userId;

    /**
     * Appointment Constructor
     * @param appId - appointment id number
     * @param appTitle - appointment title
     * @param appDescription - appointment description
     * @param appLocation - appointment location
     * @param contact - appointment contact
     * @param appType - appointment type
     * @param startTime - local start time
     * @param endTime - local end time
     * @param custId - customer Id number
     * @param userID - user id number
     *
     */
    public appointment(int appId, String appTitle, String appDescription, String appLocation, int contact, String appType, LocalDateTime startTime, LocalDateTime endTime, int custId, int userID){
        this.appId = appId;
        this.appTitle = appTitle;
        this.appDescription = appDescription;
        this.appLocation = appLocation;
        this.contact = contact;
        this.appType = appType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.custId = custId;
        this.userId = userID;
    }

    /**
     * appointment id getter
     * @return appId
     */
    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }
    /**
     *appointment title getter
     * @return appTitle
     */
    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }
    /**
     *appointment description getter
     * @return appDescription
     */
    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }
    /**
     *appointment location getter
     * @return appLocation
     */
    public String getAppLocation() {
        return appLocation;
    }

    public void setAppLocation(String appLocation) {
        this.appLocation = appLocation;
    }
    /**
     *contact getter
     * @return contact
     */
    public int getContact() {
        return contact;
    }

    public void setContact(int contact) {
        this.contact = contact;
    }
    /**
     *appointment type getter
     * @return appType
     */
    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
    /**
     * local start time getter
     * @return start
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    /**
     *local end time getter
     * @return end
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    /**
     *customer id getter
     * @return custID
     */
    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }
    /**
     *user id getter
     * @return userId
     */
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

