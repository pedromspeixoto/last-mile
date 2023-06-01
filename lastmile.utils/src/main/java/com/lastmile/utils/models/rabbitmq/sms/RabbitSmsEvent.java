package com.lastmile.utils.models.rabbitmq.sms;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RabbitSmsEvent {

    @JsonProperty("template")
    private String template;

    @JsonProperty("text_message")
    private RabbitSmsModel textMessage;

    @JsonProperty("template")
    public String getTemplate() {
        return template;
    }

    @JsonProperty("template")
    public void setTemplate(String template) {
        this.template = template;
    }


    @JsonProperty("text_message")
    public RabbitSmsModel getTextMessage() {
        return textMessage;
    }

    @JsonProperty("text_message")
    public void setTextMessage(RabbitSmsModel textMessage) {
        this.textMessage = textMessage;
    }

}