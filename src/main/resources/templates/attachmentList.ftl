<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>FreeMarker</title>
    <link href="reset.css" rel="stylesheet"/>
    <link href="table.css" rel="stylesheet"/>

    <script src="lib/jquery/jquery-3.4.0.min.js"></script>
</head>
<style type="text/css">

</style>
<body>
<table class="dataintable">
    <tbody>
    <tr>
        <td>文档内容</td>
        <td><input style="border: 1px solid" id="content" type="text" name="content"></td>
    </tr>
    <tr>
        <td colspan="2" style="text-align: center">
            <button id="searchBtn">查询</button>
        </td>
    </tr>
    </tbody>
</table>
<br/>

<table id="dataTable" class="dataintable">
    <thead>
    <tr>
        <th width="5%">date</th>
        <th width="5%">content_type</th>
        <th width="5%">author</th>
        <th width="5%">language</th>
        <th width="5%">title</th>
        <th width="70%">content</th>
        <th width="5%">content_length</th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<script type="text/javascript">

    var attachmentList = {
        // 处理内容
        contentProcess: function (content) {
            if (content == null) {
                return '';
            }
            if (content.length > 200) {
                return content.substr(0, 100).replace(/\n/g, '<br/>') + "<button style='border: 0px;color: #009a61;background: transparent;' onclick='attachmentList.showAll(this)'>显示全部</button>";
            }
            return content.replace(/\n/g, '<br/>');
        },
        // 显示全部
        showAll: function (btn) {
            $(btn).parent().html($(btn).parent().attr('content').replace(/\n/g, '<br/>'))
        }
    };
    $(function () {
        $('#searchBtn').click(function () {
            $('#dataTable tbody').html('正在查找。。。。');
            var jqxhr = $.ajax({
                method: "POST",
                url: "/attachmentSearch",
                data: {content: $('#content').val()}
            }).done(function (data) {
                if (data == null || data.length == 0) {
                    $('#dataTable tbody').html('查询数据为空');
                    return;
                }

                $('#dataTable tbody').html('');
                $(data).each(function (index, item) {
                    var line = "<tr>" +
                            "<td width='5%'>" + item.date + "</td>" +
                            "<td style='word-break: break-all;' width='5%'>" + item.content_type + "</td>" +
                            "<td width='5%'>" + item.author + "</td>" +
                            "<td width='5%'>" + item.language + "</td>" +
                            "<td width='5%'>" + item.title + "</td>" +
                            "<td width='70%' content='" + item.content + "'>" + attachmentList.contentProcess(item.content) + "</td>" +
                            "<td width='5%'>" + item.content_length + "</td>" +
                            "</tr>";
                    $('#dataTable tbody').append(line);
                });

            }).fail(function () {
                alert("error");
            }).always(function () {

            });
        });
    })

</script>
</body>
</html>