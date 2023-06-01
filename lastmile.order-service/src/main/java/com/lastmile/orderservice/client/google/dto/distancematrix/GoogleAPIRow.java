
package com.lastmile.orderservice.client.google.dto.distancematrix;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleAPIRow {

    @SerializedName("elements")
    @Expose
    private List<GoogleAPIElement> elements = null;

    public List<GoogleAPIElement> getElements() {
        return elements;
    }

    public void setElements(List<GoogleAPIElement> elements) {
        this.elements = elements;
    }

}
