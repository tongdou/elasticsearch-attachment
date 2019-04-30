<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>FreeMarker</title>
    <link href="reset.css" rel="stylesheet"/>
    <link href="lib/jsonTree/jsonTree.css" rel="stylesheet"/>
    <script src="lib/jsonTree/jsonTree.js"></script>
    <script src="lib/jquery/jquery-3.4.0.min.js"></script>
</head>
<style type="text/css">
    table.dataintable {
        margin-top: 15px;
        border-collapse: collapse;
        border: 1px solid #aaa;
        width: 90%;
        margin: auto;
    }

    table.dataintable td {
        vertical-align: text-top;
        padding: 6px 15px 6px 6px;
        border: 1px solid #aaa;
    }

    table.dataintable th {
        vertical-align: baseline;
        padding: 5px 15px 5px 6px;
        background-color: #3F3F3F;
        border: 1px solid #3F3F3F;
        text-align: left;
        color: #fff;
    }

    table.dataintable tr:nth-child(odd) {
        background-color: #F5F5F5;
    }
</style>
<body>



<table class="dataintable">
    <thead>
    <tr>
        <th style="width: 10%;">说明</th>
        <th style="width: 80%;">内容</th>
        <th style="width: 10%;">操作</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>pipeline(管道)</td>
        <td id="pipelinesWrapper"></td>
        <td><a href="javascript:void(0)" id="pipelinesBtn"/>展开</td>
    </tr>
    </tbody>
</table>
<textarea style="display: none" id="pipelinesText">${pipelines}</textarea>

<script type="text/javascript">
    // Create json-tree
    var pipelinesTree = jsonTree.create(JSON.parse($("#pipelinesText").val()), document.getElementById("pipelinesWrapper"));
    $('#pipelinesBtn').click(function () {
        if ($('#pipelinesBtn').html() == '折叠') {
            $('#pipelinesBtn').html('展开');
            pipelinesTree.collapse();
        } else {
            $('#pipelinesBtn').html('折叠');
            pipelinesTree.expand();
        }
    });
</script>
</body>
</html>