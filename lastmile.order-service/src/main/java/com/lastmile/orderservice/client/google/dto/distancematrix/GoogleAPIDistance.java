
package com.lastmile.orderservice.client.google.dto.distancematrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleAPIDistance {

    @SerializedName("value")
    @Expose
    private Integer value;

    @SerializedName("text")
    @Expose
    private String text;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
