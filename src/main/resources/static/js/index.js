layui.use(['layer', 'form', 'table', 'upload'], function(){
    var layer = layui.layer,
        form = layui.form,
        upload = layui.upload,
        table = layui.table;

    table.render({
        elem: '#fileTable',
        height: 'full',
        url: 'fileInfo/queryList',
        page: true,
        toolbar: '#toolbarDemo',
        limit: 20,
        limits: [20, 40, 80],
        cols: [[
            {checkbox: true},
            {field: 'name', title: '文件名称', width:200, sort: true, align: 'center'},
            {field: 'contentType', title: 'content-type', width:200, align: 'center'},
            {field: 'extName', title: '文件扩展名', width:200, align: 'center'},
            {field: 'length', title: '文件大小', width:200, align: 'center'},
            {field: 'uploadDateTime', title: '上传日期', width:200, align: 'center'}
        ]]
    });

    // 头工具栏
    table.on('toolbar(fileTableFilter)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'openUpload':
                openUpload();
                break;
            case 'batchDelete':
                deleteBatch(checkStatus['data']);
                break;
        };
    });

    function openUpload() {
        layer.open({
          type: 1,
          title: '文件上传',
          skin: 'layui-layer-rim', //加上边框
          area: ['600px', '300px'], //宽高
          content: $('#uploadWindow')
        });
    }

    var demoListView = $('#demoList');
    var uploadListIns = upload.render({
        elem: '#testList',
        url: 'fileInfo/upload/',
        accept: 'file',
        multiple: true,
        auto: false,
        bindAction: '#testListAction',
        choose: function(obj){
            //将每次选择的文件追加到文件队列
            var files = this.files = obj.pushFile();
            //读取本地文件
            obj.preview(function(index, file, result){
                var tr = $(['<tr id="upload-'+ index +'">',
                    '<td>'+ file.name +'</td>',
                    '<td>'+ (file.size/1014).toFixed(1) +'kb</td>',
                    '<td>等待上传</td>',
                    '<td>',
                    '<button class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>',
                    '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>',
                    '</td>',
                    '</tr>'].join(''));
                //单个重传
                tr.find('.demo-reload').on('click', function(){
                    obj.upload(index, file);
                });
                //删除
                tr.find('.demo-delete').on('click', function(){
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });
                demoListView.append(tr);
            });
        },
        done: function(res, index, upload){
            if(res.code == 0){ //上传成功
                var tr = demoListView.find('tr#upload-'+ index);
                var tds = tr.children();
                tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                tds.eq(3).html(''); //清空操作
                return delete this.files[index]; //删除文件队列已经上传成功的文件
            }
            this.error(index, upload);
        },
        error: function(index, upload){
            var tr = demoListView.find('tr#upload-'+ index);
            var tds = tr.children();
            tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
            tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
        }
    });

    function deleteBatch(data) {
        var length = data.length;
        if (length == 0) return false;
        var ids = new Array();
        for(var i = 0; i < length; i ++) {
            ids.push(data[i]['id']);
        }
        $.post({
            url: 'fileInfo/deleteBatch',
            dateType: 'json',
            data: {ids: ids},
            success: function(data){
                if (data['code'] === 0) {
                    layer.msg(data['msg'], {icon: 1});
                    reloadTableData();
                } else {
                    layer.msg(data['msg'], {icon: 2});
                }
            }
        });
    }

    function reloadTableData(param) {
        table.reload('fileTable', {
            where: param,
//            page: {curr: 1},
            done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                console.log(res);
                //得到当前页码
                console.log(curr);
                //得到数据总量
                console.log(count);
                if (res['data'].length == 0 && count != 0) {
                    table.reload('fileTable', {where: param, page: {curr: curr - 1}});
                }
            }
        });
    }
});