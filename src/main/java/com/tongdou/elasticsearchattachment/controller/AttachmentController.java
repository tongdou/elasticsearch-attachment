package com.tongdou.elasticsearchattachment.controller;

import com.alibaba.fastjson.JSON;
import com.tongdou.elasticsearchattachment.constant.AttachmentConstant;
import com.tongdou.elasticsearchattachment.service.AttachmentService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ingest.GetPipelineResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.ingest.PipelineConfiguration;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class AttachmentController {

    @Resource
    private AttachmentService attachmentService;


    /**
     * 管道列表
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("/attachmentList")
    public String attachmentInit(ModelMap modelMap) throws Exception {
        return "attachmentList";
    }


    /**
     * 管道列表
     *
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/attachmentSearch", method = {RequestMethod.POST})
    @ResponseBody
    public List<Map> attachmentSearch(@RequestParam(name = "content") String content, ModelMap modelMap) throws Exception {
        List<Map> result = new ArrayList<>();

        QueryBuilder queryBuilder = null;
        if (StringUtils.isNotBlank(content)) {
            queryBuilder = QueryBuilders.matchQuery("attachment.content", content);
        }
        SearchResponse response = attachmentService.searchData(AttachmentConstant.index, AttachmentConstant.type, queryBuilder, null);

        SearchHits hits = response.getHits();
        SearchHit[] searchHists = hits.getHits();
        //遍历取出查询结果
        for (SearchHit hit : searchHists) {
            Map<String, Object> map = hit.getSource();
            result.add((Map) map.get(AttachmentConstant.attachmentField));
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

}