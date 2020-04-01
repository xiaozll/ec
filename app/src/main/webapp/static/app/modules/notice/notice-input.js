var modelIsTop = modelIsTop;
var modelTipMessage = modelTipMessage;
var modelType = modelType;
var dictionaryTypeCode = dictionaryTypeCode;
var modelFileIds = modelFileIds;
var noticeReceiveUserIds = noticeReceiveUserIds;
var noticeReceiveOrganIds = noticeReceiveOrganIds;
var jsessionid = jsessionid;
var fileSizeLimit = fileSizeLimit;

var $form_noticeUser_MultiSelect = undefined;
var $form_noticeOrgan_combotree = undefined;
$(function () {
    loadReceiveScope();

    var $isTop =  $("input[name=isTop]:eq("+modelIsTop+")");
    $isTop.prop("checked",'checked');
    toggoleIsTop($isTop.val());
    $("input[name=isTop]").bind("click",function(){
        toggoleIsTop($(this).val());
    });
    if(modelTipMessage){
        var val = modelTipMessage.split(",");
        var boxes = document.getElementsByName("tipMessage");
        for(i=0;i<boxes.length;i++){
            for(j=0;j<val.length;j++){
                if(boxes[i].value == val[j]){
                    boxes[i].checked = true;
                    break
                }
            }
        }
    }

    var result= $.hasUsableFlash();
    if(!result) {
        eu.showMsg("您未安装Flash插件，或您的浏览未启用Flash插件！")
    }

    uploadify();
    editor();

    uploadifyHeadImage();
    $(".uploadify").css({'display': 'inline-block', 'height': '24px', 'padding-right': '18px', 'outline': 'none'});
});

function editor(){
    $("#editor").kendoEditor({
        encoded: false,
        resizable: {
            content: true,
            toolbar: true
        }
    });
}
/**
 * 数据字典 Javascript调用方式
 */
function loadNoticeType(){
    $('#notice_type').combobox({
        url: ctxAdmin + '/sys/dictionary/combobox?dictionaryTypeCode='+dictionaryTypeCode+'&selectType=select',
        multiple: false,//是否可多选
        editable: false,//是否可编辑
        width: 120,
        height: 28,
        value: modelType,
        validType: ['comboboxRequired[\'#notice_type\']']
    });
}


function loadReceiveScope(){
    $('#receiveScope').combobox({
        url: ctxAdmin + '/notice/receiveScopeCombobox',
        multiple: false,
        editable: false,
        required:true,
        missingMessage:'请选择接收范围.',
        onChange: function (newValue, oldValue) {
            toggoleReceiveScope(newValue);
        },
        onLoadSuccess:function(){
            var value = $(this).combobox("getValue");
            toggoleReceiveScope(value);
        }
    });
}

function toggoleReceiveScope(receiveScope){
    if("0" == receiveScope){
        $("#noticeUserIds_div").show();
        $("#noticeOrganIds_div").show();
        if($form_noticeUser_MultiSelect == undefined){
            loadNoticeUser();
        }
        if($form_noticeOrgan_combotree == undefined){
            loadNoticeOrgan();
        }
    }else{
        $("#noticeUserIds_div").hide();
        if($form_noticeUser_MultiSelect != undefined){
            $form_noticeUser_MultiSelect.value(" ");
        }
        $("#noticeOrganIds_div").hide();
        if($form_noticeOrgan_combotree != undefined){
            $form_noticeOrgan_combotree.combotree("setValue","");
        }
    }
}

function toggoleIsTop(isTop){
    if("0" == isTop){//不置顶
        $("#endTopDay_span").hide();
        //TODO 清空#endTopDay
    }else{
        $("#endTopDay_span").show();
        if(!$("#endTopDay").numberspinner().numberspinner("getValue")){
            $("#endTopDay").numberspinner("setValue",7);
        }
    }
}

function loadNoticeOrgan() {
    $form_noticeOrgan_combotree = $("#_noticeOrganIds").combotree({
        url: ctxAdmin + '/sys/organ/tree?dataScope=2&cascade=true',
        multiple: true,//是否可多选
        editable: false,
        value:noticeReceiveOrganIds
    });
}

var fileIdArray = modelFileIds;
var fileIds = fileIdArray.join(",");
$("#fileIds").val(fileIds);
function uploadify() {
    $('#uploadify').uploadify({
        method: 'post',
        swf: ctxStatic + '/js/uploadify/scripts/uploadify.swf',
        buttonText: '浏  览',
        uploader: ctxAdmin + '/notice/upload;jsessionid='+jsessionid,
        fileObjName: 'uploadFile',
        removeCompleted: false,
        multi: true,
        fileSizeLimit: fileSizeLimit, //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
        fileTypeDesc: '全部文件',
        fileTypeExts: '*.*',
        onUploadSuccess: function (file, data, response) {
            data = eval("(" + data + ")");
            if(data.code != undefined && data.code == 1){
                fileIdArray.push(data.obj.id);
            }
            $('#' + file.id).find('.data').html(data.msg);
            var fileIds = fileIdArray.join(",");
            $("#fileIds").val(fileIds);
            var uploadify = this;
            var cancel = $('#uploadify-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
            if (cancel) {
                cancel.attr("rel", data.obj.id);
                cancel.click(function() {
                    delUpload( data.obj.id,file.id,uploadify);
                });
            }
        }

    });
}


function uploadifyHeadImage() {
    $('#file').uploadify({
        method: 'post',
        swf: ctxStatic + '/js/uploadify/scripts/uploadify.swf',  //FLash文件路径
        buttonText: '浏  览',                                 //按钮文本
        uploader: ctxAdmin + '/notice/upload;jsessionid='+jsessionid,
        fileObjName: 'uploadFile',
        removeCompleted: false,
        multi: false,
        fileSizeLimit: '1MB', //单个文件大小，0为无限制，可接受KB,MB,GB等单位的字符串值
        fileTypeDesc: '全部文件', //文件描述
        fileTypeExts: '*.gif; *.jpg; *.png; *.bmp',  //上传的文件后缀过滤器
        //上传到服务器，服务器返回相应信息到data里
        onUploadSuccess: function (file, data, response) {
            data = eval("(" + data + ")");
            if(data.code == 1){
                $('#head_image_pre').attr("src",data['obj']['url']);
                $('#head_image_pre').show();
                $("#head_image").val(data['obj']['id']);
            }
            $('#' + file.id).find('.data').html(data.msg);


            var uploadify = this;
            var cancel = $('#file-queue .uploadify-queue-item[id="' + file.id + '"]').find(".cancel a");
            if (cancel) {
                cancel.attr("rel", data['obj']['id']);
                cancel.click(function() {
                    $('#' + file.id).empty();
                    delete uploadify.queueData.files[file.id]; //删除上传组件中的附件队列
                    $('#' + file.id).remove();
                });
            }
        }

    });
}
function loadOrOpen(fileId) {
    $('#annexFrame').attr('src', ctxAdmin + '/disk/fileDownload/' + fileId);
}

/**
 * 删除附件 页面删除
 * @param fileId 后台File ID
 * @param pageFileId uploadify页面ID'
 * @param uploadify
 */
function delUpload(fileId,pageFileId,uploadify) {
    fileIdArray.splice($.inArray(fileId,fileIdArray),1);
    var fileIds = fileIdArray.join(",");
    $("#fileIds").val(fileIds);
    $('#' + fileId).remove();
    if(pageFileId){
        $('#' + pageFileId).empty();
        delete uploadify.queueData.files[pageFileId]; //删除上传组件中的附件队列
        $('#' + pageFileId).remove();
    }

}

/**
 *
 */
function loadNoticeUser() {
    var dataSource = {data: [],group: { field: "defaultOrganName" }};
    $form_noticeUser_MultiSelect = $("#noticeUserIds").kendoMultiSelect({
        dataTextField: "name",
        dataValueField: "id",
        dataSource: dataSource,
        value: noticeReceiveUserIds,
        dataBound: function (e) {

        }

    }).data("kendoMultiSelect");
    $.ajax({
        type: "post",
        dataType: 'json',
        contentType: "application/json",
        url: ctxAdmin + '/sys/user/userList?dataScope=2',
        //async: false,
        success: function (data) {
            var dataSource = {data: data,group: { field: "defaultOrganName" }};
            $form_noticeUser_MultiSelect.setDataSource(dataSource);
            $form_noticeUser_MultiSelect.value(noticeReceiveUserIds);
        }
    });

}
function _selectUser() {
    var userIds = "";
    var dataItems = $form_noticeUser_MultiSelect.dataItems();
    if (dataItems && dataItems.length > 0) {
        var num = dataItems.length;
        $.each(dataItems, function (n, value) {
            if (n == num - 1) {
                userIds += value.id;
            } else {
                userIds += value.id + ",";
            }

        });

    }
    var input_selectUser_dialog = $("<div/>").dialog({
        title: "选择用户",
        top: 10,
        href: ctxAdmin + '/sys/user/select?dataScope=2&cascade=true&userIds=' + userIds,
        width: '700',
        height: '450',
        maximizable: true,
        iconCls: 'easyui-icon-edit',
        modal: true,
        buttons: [
            {
                text: '确定',
                iconCls: 'easyui-icon-save',
                handler: function () {
                    _setSelectUser();
                    input_selectUser_dialog.dialog('destroy');
                }
            },
            {
                text: '关闭',
                iconCls: 'easyui-icon-cancel',
                handler: function () {
                    input_selectUser_dialog.dialog('destroy');

                }
            }
        ],
        onClose: function () {
            input_selectUser_dialog.dialog('destroy');
        }
    });
}

function _setSelectUser() {
    var selectUserIds = new Array();
    $("#selectUser option").each(function () {
        var txt = $(this).val();
        selectUserIds.push($.trim(txt));
    });
    $form_noticeUser_MultiSelect.value(selectUserIds);
}