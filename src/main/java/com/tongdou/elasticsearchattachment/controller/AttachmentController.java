package com.tongdou.elasticsearchattachment.controller;

import com.alibaba.fastjson.JSON;
import com.tongdou.elasticsearchattachment.constant.Configurations;
import com.tongdou.elasticsearchattachment.service.AttachmentService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.ingest.GetPipelineResponse;
import org.elasticsearch.action.ingest.WritePipelineResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.ingest.PipelineConfiguration;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class AttachmentController {

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private Configurations config;


    /**
     * 跳转到附件列表
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String index(ModelMap modelMap) throws Exception {

        // 判断index是否存在
        IndicesExistsResponse existsResponse = attachmentService.getIndex(config.getIndex());
        modelMap.addAttribute("index", config.getIndex());
        modelMap.addAttribute("indexExists", existsResponse.isExists());

        // 判断pipeline是否存在
        GetPipelineResponse pipelineResponse = attachmentService.getPipeline(config.getPipelineId());
        modelMap.addAttribute("pipelineId", config.getPipelineId());
        modelMap.addAttribute("pipelineExists", pipelineResponse.isFound());

        return "index";
    }

    /**
     * 创建索引
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createIndex", method = {RequestMethod.POST})
    @ResponseBody
    public String createIndex(ModelMap modelMap) throws Exception {
        CreateIndexResponse response = attachmentService.createIndex(config.getIndex(), config.getType());
        return "创建成功！";
    }

    /**
     * 创建Pipeline
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createPipeline", method = {RequestMethod.POST})
    @ResponseBody
    public String createPipeline(ModelMap modelMap) throws Exception {
        WritePipelineResponse response = attachmentService.createPipeline(config.getPipelineId(), config.getPipelineField());
        return "创建成功！";
    }

    /**
     * 跳转到附件列表
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toAttachmentList", method = {RequestMethod.GET})
    public String attachmentInit(ModelMap modelMap) throws Exception {
        return "attachmentList";
    }

    /**
     * 获取附件列表
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getAttachmentList", method = {RequestMethod.POST})
    @ResponseBody
    public List<Map> attachmentSearch(@RequestParam(name = "content") String content, ModelMap modelMap) throws Exception {
        List<Map> result = new ArrayList<>();

        QueryBuilder queryBuilder = null;
        if (StringUtils.isNotBlank(content)) {
            queryBuilder = QueryBuilders.matchPhraseQuery("attachment.content", content);
        }
        SortBuilder sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
        SearchResponse response = attachmentService.searchData(config.getIndex(), config.getType(), queryBuilder, null, sortBuilder);

        SearchHits hits = response.getHits();
        SearchHit[] searchHists = hits.getHits();
        //遍历取出查询结果
        for (SearchHit hit : searchHists) {
            Map<String, Object> sourceMap = hit.getSource();

            Map map = new HashMap();
            map.put("createTime", sourceMap.get("createTime"));
            map.put("filename", sourceMap.get("filename"));
            map.putAll((Map) sourceMap.get(config.getAttachmentField()));

            result.add(map);
        }

        return result;
    }


    /**
     * 上传需要索引的文档（word、excel、pdf等）
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/attachmentUpload", method = {RequestMethod.POST})
    @ResponseBody
    public String attachmentUpload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        Map map = new HashMap();
        map.put(config.getPipelineField(), Base64.encodeBase64String(file.getBytes()));
        map.put("createTime", new Date());
        map.put("filename", file.getOriginalFilename());
        IndexResponse response = attachmentService.addDataByMap(config.getIndex(), config.getType(), config.getPipelineId(), map);

        return "上传成功！";
    }


}