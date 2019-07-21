layui.use(['layer', 'form', 'table', 'upload'], function(){
    var layer = layui.layer,
        form = layui.form,
        upload = layui.upload,
        table = layui.table;

    // 搜索关键字
    var keyword = $.trim($('[name="keyword"]').val());

    // 页面加载完成后，加载搜索数据
    function requestSearchResult() {
        if(keyword == '') return;
        $.post({
            url: '/search/doSearch',
            data: {
                keyword: keyword,
                page: 0,
                limit: 20
            },
            success: function(data) {
                initSearchData(data);
            }
        });
    }

    // 数据
    function initSearchData(data) {
        console.info(data);
        var contentPanel = $('.content-panel');
        var html = '';
        if (data['code'] === 0) {
            var list = data['data'];
            if (list.length === 0) html = '<div class="search-empty">can not find what you search...</div>';
            for(var i = 0; i < list.length; i ++) {
                var fileInfo = list[i];
                html += '<div class="result-item">' +
                '<div class="name">' + fileInfo['name'] + '</div>' +
                '<div class="content">' + fileInfo['content'] + '</div>' +
                '</div><hr>';
            }
        } else {
            html = '<span class="search-empty">search error</span>';
        }
        contentPanel.html(html);
    }

    // 执行搜索
    requestSearchResult();

    // 搜索事件
    $('#search').click(function() {
        var keyword = $.trim($('[name="keyword"]').val());
        if(keyword == '') return;
        window.location.href = '/search/search/' + keyword;
    });
});