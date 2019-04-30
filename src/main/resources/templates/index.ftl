<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>FreeMarker</title>
    <link href="reset.css" rel="stylesheet"/>
    <link href="table.css" rel="stylesheet"/>
    <script src="lib/jquery/jquery-3.4.0.min.js"></script>
</head>
<body>

<table class="dataintable" style="margin-top: 20px">
    <thead>
    <tr>
        <th>步骤</th>
        <th>说明</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>第1步</td>
        <td>
            <#if indexExists>
                <span class="success">索引[${index}]已存在</span>
            <#else>
                <span class="fail">索引[${index}]不存在，请手工创建</span>
            </#if>
        </td>
        <td>
            <button id="createIndexBtn">创建索引</button>
        </td>
    </tr>
    <tr>
        <td>第2步</td>
        <td>
            <#if pipelineExists>
                <span class="success">Pipeline[${pipelineId}]已存在</span>
            <#else>
                <span class="fail">Pipeline[${pipelineId}]不存在，请手工创建</span>
            </#if></td>
        <td>
            <button id="createPipelineBtn">创建Pipeline</button>
        </td>
    </tr>
    <tr>
        <td>第3步</td>
        <td colspan="2"><a href="toAttachmentList">文档上传/检索</a></td>
    </tr>
    </tbody>
</table>

<script type="text/javascript">
    $(function () {
        $('#createIndexBtn').click(function () {
            var jqxhr = $.ajax({
                method: "POST",
                url: "/createIndex",
                data: {}
            }).done(function (data) {
                alert(data);
            }).fail(function () {
                alert("error");
            }).always(function () {

            });
        });

        $('#createPipelineBtn').click(function () {
            var jqxhr = $.ajax({
                method: "POST",
                url: "/createPipeline",
                data: {}
            }).done(function (data) {
                alert(data);
            }).fail(function () {
                alert("error");
            }).always(function () {

            });
        });

    })
</script>
</body>
</html>