package fr.neowave.beans;

import java.io.Serializable;
import java.security.cert.X509Certificate;

/**
 * Représentation d'un enregistrement de la table Registrations
 */
public class Registration implements Serializable{

    private String username;
    private String publicKey;
    private X509Certificate certificate;
    private Long counter;
    private String keyHandle;
    private String timestamp;
    private String hostname;
    private Boolean suspended;


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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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
