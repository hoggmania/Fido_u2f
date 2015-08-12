package fr.neowave.beans;

import java.sql.Blob;
import java.sql.Date;

/**
 * Created by Elekhyr on 04/08/2015.
 */
public class Registration {

    private Integer id;
    private String username;
    private String publicKey;
    private Blob certificate;
    private Integer counter;
    private String keyHandle;
    private Date timestamp;
    private String hostname;
    private Boolean suspended;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Blob getCertificate() {
        return certificate;
    }

    public void setCertificate(Blob certificate) {
        this.certificate = certificate;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public String getKeyHandle() {
        return keyHandle;
    }

    public void setKeyHandle(String keyHandle) {
        this.keyHandle = keyHandle;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }
}
