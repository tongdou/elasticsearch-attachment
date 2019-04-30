package com.tongdou.elasticsearchattachment.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configurations {

    /**
     * ES的IP地址
     */
    @Value("${config.elastic.ip}")
    public String ip = "127.0.0.1";

    /**
     * ES的端口
     */
    @Value("${config.elastic.port}")
    public int port = 9300;

    /**
     * 索引名称
     */
    @Value("${config.index}")
    public String index = "index2";

    /**
     * 类型名称
     */
    @Value("${config.type}")
    public String type = "type2";

    /**
     * 创建pipeline名称
     */
    @Value("${config.pipelineId}")
    public String pipelineId = "transport2";

    /**
     * pipeline中的字段名称
     */
    @Value("${config.pipelineField}")
    public String pipelineField = "data2";

    /*这个值不能改，是Ingest Attachment插件设置的*/
    public String attachmentField = "attachment";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineField() {
        return pipelineField;
    }

    public void setPipelineField(String pipelineField) {
        this.pipelineField = pipelineField;
    }

    public String getAttachmentField() {
        return attachmentField;
    }

    public void setAttachmentField(String attachmentField) {
        this.attachmentField = attachmentField;
    }
}
