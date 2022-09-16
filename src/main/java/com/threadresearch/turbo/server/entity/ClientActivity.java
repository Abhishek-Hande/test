package com.threadresearch.turbo.studyconfigurator.server.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * . ClientActivity class
 * 
 * @author Harinath
 *
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientActivity implements Serializable {

    private static final long serialVersionUID = 5226377902975189739L;

    @Id
    private String id;
    private String activityKey;
    private String activityTitle;
    private String status;
    private String clientId;
    private Date createdTime;
    private Date modifiedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public void setActivityKey(String activityKey) {
        this.activityKey = activityKey;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "ClientActivity [id=" + id + ", activityKey=" + activityKey + ", activityTitle=" + activityTitle + ", status=" + status + ", clientId=" + clientId + ", createdTime="
                + createdTime + ", modifiedTime=" + modifiedTime + "]";
    }

}
