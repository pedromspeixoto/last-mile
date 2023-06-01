
package com.lastmile.orderservice.client.google.dto.distancematrix;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleAPIResponseDto {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("origin_addresses")
    @Expose
    private List<String> originAddresses = null;

    @SerializedName("destination_addresses")
    @Expose
    private List<String> destinationAddresses = null;

    @SerializedName("rows")
    @Expose
    private List<GoogleAPIRow> rows = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(List<String> originAddresses) {
        this.originAddresses = originAddresses;
    }

    public List<String> getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(List<String> destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public List<GoogleAPIRow> getRows() {
        return rows;
    }

    public void setRows(List<GoogleAPIRow> rows) {
        this.rows = rows;
    }

}
