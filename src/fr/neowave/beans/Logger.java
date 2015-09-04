package fr.neowave.beans;

import java.io.Serializable;

/**
 * Représentation d'un log
 */
public class Logger implements Serializable{
    private String serverSessionId;
    private String message;
    private String context;
    private String username;
    private String browserName;
    private String browserVersion;
    private String osName;
    private String osVersion;
    private String ip;
    private String reverseName;
    private String requestParameters;
    private String requestAttributes;
    private String requestErrors;
    private String sessionAttributes;
    private String dateTimeStart;
    private String dateTimeEnd;
    private String endType;

    public String getServerSessionId() {
        return serverSessionId;
    }

    public void setServerSessionId(String serverSessionId) {
        this.serverSessionId = serverSessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReverseName() {
        return reverseName;
    }

    public void setReverseName(String reverseName) {
        this.reverseName = reverseName;
    }

    public String getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(String requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String getRequestAttributes() {
        return requestAttributes;
    }

    public void setRequestAttributes(String requestAttributes) {
        this.requestAttributes = requestAttributes;
    }

    public String getRequestErrors() {
        return requestErrors;
    }

    public void setRequestErrors(String requestErrors) {
        this.requestErrors = requestErrors;
    }

    public String getSessionAttributes() {
        return sessionAttributes;
    }

    public void setSessionAttributes(String sessionAttributes) {
        this.sessionAttributes = sessionAttributes;
    }

    public String getDateTimeStart() {
        return dateTimeStart;
    }

    public void setDateTimeStart(String dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    public String getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(String dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }
}
