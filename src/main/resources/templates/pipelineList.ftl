<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>FreeMarker</title>
    <link href="reset.css" rel="stylesheet"/>
    <link href="table.css" rel="stylesheet"/>
    <link href="lib/jsonTree/jsonTree.css" rel="stylesheet"/>
    <script src="lib/jsonTree/jsonTree.js"></script>
    <script src="lib/jquery/jquery-3.4.0.min.js"></script>
</head>
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