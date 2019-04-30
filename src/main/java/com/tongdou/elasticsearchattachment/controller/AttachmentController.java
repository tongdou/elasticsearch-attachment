package com.tongdou.elasticsearchattachment.controller;

import com.alibaba.fastjson.JSON;
import com.tongdou.elasticsearchattachment.constant.AttachmentConstant;
import com.tongdou.elasticsearchattachment.service.AttachmentService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.ingest.GetPipelineResponse;
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


    /**
     * 附件列表
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
            queryBuilder = QueryBuilders.matchQuery("attachment.content", content);
        }
        SortBuilder sortBuilder = null;//SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
        SearchResponse response = attachmentService.searchData(AttachmentConstant.index, AttachmentConstant.type, queryBuilder, null, sortBuilder);

        SearchHits hits = response.getHits();
        SearchHit[] searchHists = hits.getHits();
        //遍历取出查询结果
        for (SearchHit hit : searchHists) {
            Map<String, Object> sourceMap = hit.getSource();

            Map map = new HashMap();
            map.put("createTime", sourceMap.get("createTime"));
            map.put("filename", sourceMap.get("filename"));
            map.putAll((Map) sourceMap.get(AttachmentConstant.attachmentField));

            result.add(map);
        }

        return result;
    }

    /**
     * 管道列表
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("/pipelineList")
    public String pipelineList(ModelMap modelMap) throws Exception {
        modelMap.addAttribute("msg", "Hello dalaoyang , this is freemarker");

        // 获取所有管道
        GetPipelineResponse pipelineResponse = attachmentService.getPipeLines();
        if (pipelineResponse.isFound()) {
            List<PipelineConfiguration> pipelines = pipelineResponse.pipelines();
            modelMap.addAttribute("pipelines", JSON.toJSONString(pipelines));
            JSON.toJSONString("");
        }

        return "pipelineList";
    }


    @RequestMapping(value = "/attachmentUpload", method = {RequestMethod.POST})
    @ResponseBody
    public String attachmentUpload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        Map map = new HashMap();
        map.put(AttachmentConstant.pipelineField, Base64.encodeBase64String(file.getBytes()));
        map.put("createTime", new Date());
        map.put("filename", file.getOriginalFilename());
        IndexResponse response = attachmentService.addDataByMap(AttachmentConstant.index, AttachmentConstant.type, AttachmentConstant.pipeline, map);

        return "上传成功！";
    }
}