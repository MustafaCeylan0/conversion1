package com.seng.conversion.helper;

public class ConversionHelper {
    String inputFormat;
    String outputFormat;
    String script;

    public ConversionHelper(String inputFormat, String outputFormat, String script) {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.script = script;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
