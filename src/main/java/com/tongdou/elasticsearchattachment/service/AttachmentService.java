package com.tongdou.elasticsearchattachment.service;

import com.tongdou.elasticsearchattachment.constant.Configurations;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.ingest.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class AttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);

    @Resource
    private Configurations config;

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
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(config.getIp()), config.getPort()));
        return client;
    }

    /**
     * 创建索引
     *
     * @param index 索引名称
     * @param type  类型
     * @return
     */
    public CreateIndexResponse createIndex(String index, String type) throws Exception {
        CreateIndexRequest request = new CreateIndexRequest(index);

        // setting
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );

        // properties
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> createTime = new HashMap<>();
        createTime.put("type", "date");
        properties.put("createTime", createTime);
        Map<String, Object> filename = new HashMap<>();
        filename.put("type", "text");
        properties.put("filename", filename);


        // mapping
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        request.mapping(type, mapping);

        CreateIndexResponse response = this.getClient().execute(CreateIndexAction.INSTANCE, request).actionGet();
        return response;
    }

    /**
     * 查询index
     *
     * @return
     */
    public IndicesExistsResponse getIndex(String index) throws Exception {
        IndicesAdminClient adminClient = this.getClient().admin().indices();
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        return response;
    }


    /**
     * 创建pipeline
     *
     * @param pipelineId    pipeline名称
     * @param pipelineField 字段名称
     * @return
     */
    public WritePipelineResponse createPipeline(String pipelineId, String pipelineField) throws Exception {

        if (StringUtils.isBlank(pipelineField)) {
            pipelineField = "data";
        }

        String source = "{" +
                " \"description\" : \"Extract attachment information\"," +
                " \"processors\":[" +
                " {" +
                "    \"attachment\":{" +
                "        \"field\":\"" + pipelineField + "\"," +
                "        \"indexed_chars\" : -1," +
                "        \"ignore_missing\":true" +
                "     }" +
                " }," +
                " {" +
                "     \"remove\":{\"field\":\"" + pipelineField + "\"}" +
                " }]}";

        PutPipelineRequest request = new PutPipelineRequest(
                pipelineId,
                new BytesArray(source.getBytes(StandardCharsets.UTF_8)),
                XContentType.JSON
        );

        WritePipelineResponse response = this.getClient().execute(PutPipelineAction.INSTANCE, request).actionGet();

        return response;
    }

    /**
     * 查询pipeline
     *
     * @param pipelineId id
     * @return
     */
    public GetPipelineResponse getPipeline(String pipelineId) throws Exception {
        GetPipelineRequest request = new GetPipelineRequest(pipelineId);
        GetPipelineResponse response = this.getClient().execute(GetPipelineAction.INSTANCE, request).actionGet();
        return response;
    }

    public IndexResponse addDataByMap(String index, String type, String pipeline, Map<String, Object> map) throws Exception {
        IndexRequestBuilder indexRequestBuilder = this.getClient().prepareIndex(index, type);
        if (StringUtils.isNotBlank(pipeline)) {
            indexRequestBuilder.setPipeline(pipeline);
        }
        IndexResponse indexResponse = indexRequestBuilder.setSource(map).execute().actionGet();
        return indexResponse;
    }

    public SearchResponse searchData(String index, String type, QueryBuilder queryBuilder, QueryBuilder postFilter, SortBuilder sortBuilder) throws
            Exception {
        SearchRequestBuilder searchRequestBuilder = this.getClient().prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        if (queryBuilder != null) {
            searchRequestBuilder.setQuery(queryBuilder);                 // Query
        }
        if (postFilter != null) {
            searchRequestBuilder.setPostFilter(postFilter);     // Filter
        }
        if (sortBuilder != null) {
            searchRequestBuilder.addSort(sortBuilder);
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
