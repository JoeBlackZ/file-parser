layui.use(['layer', 'form', 'table'], function(){
    var layer = layui.layer,
        form = layui.form,
        table = layui.table;

    table.render({
        elem: '#fileTable',
        height: 312,
        url: 'https://www.layui.com/demo/table/user/?page=1&limit=30',
        page: true,
        cols: [[
            {checkbox: true},
            {field: 'id', title: 'ID', width:80, sort: true, fixed: 'left'},
            {field: 'username', title: '用户名', width:80},
            {field: 'sex', title: '性别', width:80, sort: true},
            {field: 'city', title: '城市', width:80},
            {field: 'sign', title: '签名', width: 177},
            {field: 'experience', title: '积分', width: 80, sort: true},
            {field: 'score', title: '评分', width: 80, sort: true},
            {field: 'classify', title: '职业', width: 80},
            {field: 'wealth', title: '财富', width: 135, sort: true}
        ]]
    });
});