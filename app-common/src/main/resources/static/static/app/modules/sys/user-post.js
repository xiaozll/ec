var modelId = modelId;
var organId = organId;
var postIds = postIds;

var $post_combobox;
$(function () {
    loadPost();
});

//加载用户可选岗位
function loadPost() {
    $post_combobox = $("#postIds").combobox({
        url: ctxAdmin + '/sys/post/userPostCombobox?userId=' + modelId + '&organId=' + organId,
        multiple: true,
        editable: false
    });
    if (postIds) {
        $post_combobox.combobox('setValues', postIds);
    }
}