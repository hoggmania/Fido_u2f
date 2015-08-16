package fr.neowave.beans;

import u2f.data.DeviceRegistration;
import u2f.exceptions.U2fBadInputException;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.sql.Date;

public class Registration extends DeviceRegistration implements Serializable{

    private String username;
    private String publicKey;
    private X509Certificate certificate;
    private Long counter;
    private String keyHandle;
    private Date timestamp;
    private String hostname;
    private Boolean suspended;

    public Registration(String keyHandle, String publicKey, X509Certificate attestationCert, long counter) throws U2fBadInputException {
        super(keyHandle, publicKey, attestationCert, counter);
        this.keyHandle = keyHandle;
        this.publicKey = publicKey;
        this.certificate = attestationCert;
        this.counter = counter;
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

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
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
