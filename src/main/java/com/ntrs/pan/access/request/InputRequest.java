package com.ntrs.pan.access.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputRequest {

    @JsonProperty("protectModel")
    private ProtectModel protectModel;
    @JsonProperty("inputFileLocation")
    private String inputFileLocation;
    @JsonProperty("outputFileDir")
    private String outputFileDir;

    public ProtectModel getProtectModel() {
        return protectModel;
    }

    public void setProtectModel(ProtectModel protectModel) {
        this.protectModel = protectModel;
    }

    public String getInputFileLocation() {
        return inputFileLocation;
    }

    public void setInputFileLocation(String inputFileLocation) {
        this.inputFileLocation = inputFileLocation;
    }

    public String getOutputFileDir() {
        return outputFileDir;
    }

    public void setOutputFileDir(String outputFileDir) {
        this.outputFileDir = outputFileDir;
    }

    @Override
    public String toString() {
        return "InputRequest{" +
                "protectModel=" + protectModel +
                ", inputFileLocation='" + inputFileLocation + '\'' +
                ", OutputFileDir='" + outputFileDir + '\'' +
                '}';
    }
}
