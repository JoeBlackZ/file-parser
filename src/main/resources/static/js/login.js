$(document).ready(function() {

    layui.use(['layer', 'form', 'table', 'upload'], function(){
        var layer = layui.layer,
            form = layui.form;

        form.verify({
            account: function(value, item){
                if(!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)){
                  return '用户名不能合法';
                }
                if(/(^\_)|(\__)|(\_+$)/.test(value)){
                  return '用户名首尾不能出现下划线\'_\'';
                }
                if(/^\d+\d+\d$/.test(value)){
                  return '用户名不能全为数字';
                }
            },
            password: [
                /^[\S]{6,12}$/,
                '密码必须6到12位，且不能出现空格'
            ]
        });

        form.on('submit(demo1)', function(data){
            $.post({
                url: '/login',
                data: data['field'],
                success: function(data) {
                    if(data['code'] === 0) {
                        window.location.href = '/index';
                    } else {
                        layer.msg(data['msg'], {icon: 5});
                    }
                }
            });
            return false;
        });


    });

});
