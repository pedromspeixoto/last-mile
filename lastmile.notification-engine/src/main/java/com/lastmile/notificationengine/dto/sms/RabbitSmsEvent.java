package com.lastmile.notificationengine.dto.sms;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lastmile.notificationengine.domain.sms.Sms;

public class RabbitSmsEvent implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "template", required = true)
    private String templateCode;

    @JsonProperty(value = "text_message", required = true)
    private Sms textMessage;

    public RabbitSmsEvent(String templateCode, Sms textMessage) {
	    super();
	    this.templateCode = templateCode;
	    this.textMessage = textMessage;
    }

    public String getTemplateCode() {
	    return templateCode;
    }

    public void setTemplateCode(String templateCode) {
	    this.templateCode = templateCode;
    }

    public Sms getTextMessage() {
	    return textMessage;
    }

    public void setTextMessage(Sms textMessage) {
	    this.textMessage = textMessage;
    }

}