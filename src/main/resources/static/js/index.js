layui.use(['layer', 'form', 'table', 'upload'], function(){
    var layer = layui.layer,
        form = layui.form,
        upload = layui.upload,
        table = layui.table;

    table.render({
        elem: '#fileTable',
        height: 312,
        url: 'https://www.layui.com/demo/table/user/?page=1&limit=30',
        page: true,
        toolbar: '#toolbarDemo',
        cols: [[
            {checkbox: true},
            {field: 'filename', title: '文件名称', width:100, fixed: 'left', sort: true, align: 'center'},
            {field: 'sex', title: '性别', width:100, fixed: 'right', align: 'center'},
            {field: 'city', title: '城市', width:80},
            {field: 'sign', title: '签名', width: 177},
            {field: 'experience', title: '积分', width: 80, sort: true},
            {field: 'score', title: '评分', width: 80, sort: true},
            {field: 'classify', title: '职业', width: 80},
            {field: 'wealth', title: '财富', width: 135, sort: true}
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
        url: '/upload/',
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
});