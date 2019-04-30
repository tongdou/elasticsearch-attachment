package com.tongdou.elasticsearchattachment.service;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.ingest.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);

    private TransportClient client = null;

    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }


    public TransportClient getClient() throws Exception {
        if (this.client == null) {
            synchronized (AttachmentService.class) {
                if (this.client == null) {
                    this.client = this.init();
                }
            }
        }

        return this.client;
    }

    private TransportClient init() throws Exception {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        return client;
    }

    /**
     * 创建管道
     *
     * @param pipeLineName 管道名称
     * @param filedName    字段名称
     * @return
     */
    public WritePipelineResponse createPipeLine(String pipeLineName, String filedName) throws Exception {


        if (StringUtils.isBlank(filedName)) {
            filedName = "data";
        }

        String source = "{" +
                " \"description\" : \"Extract attachment information\"," +
                " \"processors\":[" +
                " {" +
                "    \"attachment\":{" +
                "        \"field\":\"" + filedName + "\"," +
                "        \"indexed_chars\" : -1," +
                "        \"ignore_missing\":true" +
                "     }" +
                " }," +
                " {" +
                "     \"remove\":{\"field\":\"" + filedName + "\"}" +
                " }]}";

        PutPipelineRequest request = new PutPipelineRequest(
                pipeLineName,
                new BytesArray(source.getBytes(StandardCharsets.UTF_8)),
                XContentType.JSON
        );

        WritePipelineResponse response = this.getClient().execute(PutPipelineAction.INSTANCE, request).actionGet();

        return response;
    }

    /**
     * 查询管道
     *
     * @return
     */
    public GetPipelineResponse getPipeLines() throws Exception {
        GetPipelineRequest request = new GetPipelineRequest();
        GetPipelineResponse response = this.getClient().execute(GetPipelineAction.INSTANCE, request).actionGet();
        return response;

    }

    public IndexResponse addDataByMap(String index, String type, String id, String pipeline, Map<String, Object> map) throws Exception {
        IndexRequestBuilder indexRequestBuilder = this.getClient().prepareIndex(index, type, id);
        if (StringUtils.isNotBlank(pipeline)) {
            indexRequestBuilder.setPipeline(pipeline);
        }
        IndexResponse indexResponse = indexRequestBuilder.setSource(map).execute().actionGet();
        return indexResponse;
    }

    public SearchResponse searchData(String index, String type, QueryBuilder queryBuilder, QueryBuilder postFilter) throws
            Exception {
        SearchRequestBuilder searchRequestBuilder = this.getClient().prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);                 // Query
        }
        if (postFilter != null) {
            searchRequestBuilder.setPostFilter(postFilter);     // Filter
        }
        //.setFrom(0).setSize(60)
        searchRequestBuilder.setExplain(true);
        SearchResponse response = searchRequestBuilder.get();

        return response;
    }

    public void destroy() {
        synchronized (this.client) {
            if (this.client != null) {
                this.client.close();
            }

        }
    }

    public void close() {
        synchronized (this.client) {
            if (this.client != null) {
                this.client.close();
                this.client = null;
            }

        }
    }

}
