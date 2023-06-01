package com.lastmile.quartzservice.enums;

public enum BatchExecutionStatus {

    FAILED, SUCCESS;

    public String getBatchExecutionStatus() {
        return name();
    }
}