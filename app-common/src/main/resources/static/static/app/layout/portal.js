var $layout_portal_portal;
var $jplayer;
var protal_titles = ['&nbsp;我的通知', '&nbsp;我的邮件'];
$(function () {
    panels = [{
        id: 'p1',
        href: ctxAdmin + '/portal/notice',
        title: protal_titles[0],
        iconCls: 'eu-icon-notice_user_comment',
        height: 300,
        collapsible: true,
        tools: [{
            iconCls: 'eu-icon-more',
            handler: function () {
                var url = ctxAdmin + '/notice';
                try {
                    parent.addTabs({id: 'notice', title: '我的通知', close: true, url: url, urlType: ''});
                } catch (e) {
                    eu.addTab(window.parent.layout_center_tabs, "我的通知", url, true, "eu-icon-notice_user_comment");
                }


            }
        }]
    }];

    $('#layout_portal_portal').portal({
        border: false,
        fit: true,
        onStateChange: function () {
            $.cookie('portal-state', getPortalState(), {
                expires: 7
            });
        }
    });
    var state = $.cookie('portal-state');
    if (!state) {
        state = 'p1';/*冒号代表列，逗号代表行*/
    }
    addPortalPanels(state);
    $('#layout_portal_portal').portal('resize');

    initMedia();

    mymessages(false, true);
    window.setInterval('mymessages(true,true)', 5 * 60 * 1000);
});

function getPanelOptions(id) {
    for (var i = 0; i < panels.length; i++) {
        if (panels[i].id === id) {
            return panels[i];
        }
    }
    return undefined;
}

function getPortalState() {
    var aa = [];
    for (var columnIndex = 0; columnIndex < 2; columnIndex++) {
        var cc = [];
        var panels = $('#layout_portal_portal').portal('getPanels', columnIndex);
        for (var i = 0; i < panels.length; i++) {
            cc.push(panels[i].attr('id'));
        }
        aa.push(cc.join(','));
    }
    return aa.join(':');
}

function addPortalPanels(portalState) {
    var columns = portalState.split(':');
    for (var columnIndex = 0; columnIndex < columns.length; columnIndex++) {
        var cc = columns[columnIndex].split(',');
        for (var j = 0; j < cc.length; j++) {
            var options = getPanelOptions(cc[j]);
            if (options) {
                var p = $('<div/>').attr('id', options.id).appendTo('body');
                p.panel(options);
                $('#layout_portal_portal').portal('add', {
                    panel: p,
                    columnIndex: columnIndex
                });
            }
        }
    }
}

/**
 * 初始化声音提醒
 */
function initMedia() {
    $jplayer = $("#jplayer").jPlayer({
        swfPath: ctxStatic + "/js/jquery/jplayer/Jplayer.swf",
        ready: function () {
            $(this).jPlayer("setMedia", {
                mp3: ctxStatic + "/media/tip_new_msg.mp3"
            });
        },
        supplied: "mp3"
    });
}

/**
 * 声音提示
 */
function tipMsg() {
    var tipMessage = $.cookie('portal-tipMessage') !== "false";
    if (tipMessage) {
        $jplayer.jPlayer('play');
    }
    var tipMessageHtml = "<span>您有新的消息，请注意查收！</span>";
    tipMessageHtml += "<div style='margin-top: 10px;'><label><input id='tip_checkbox' type='checkbox' onclick='setTipMessage(this.checked);' ";
    if (tipMessage) {
        tipMessageHtml += " checked ";
    }
    tipMessageHtml += "/> 提示声音</label></div>";
    $.messager.show({
        title: '<span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"></span><span style="color: red;"> 提示信息！</span>',
        msg: tipMessageHtml,
        height: 110,
        timeout: 5000,
        showType: 'slide' //null,slide,fade,show.
    });
}

function setTipMessage(tipMessage) {
    $.cookie('portal-tipMessage', tipMessage, {expires: 7});
}

/**
 * 更新portal消息
 * @param refreshPanel 是否需要刷新Panel
 * @param tipMessage 是否提示声音
 */
function mymessages(refreshPanel, tipMessage) {
    $.ajax({
        url: ctxAdmin + '/portal/mymessages',
        type: 'get',
        dataType: 'json',
        success: function (data) {
            if (data.code === 1) {
                if (refreshPanel) {
                    $("#p1").panel("refresh");
                }

                var hashNewMessage = false;//是否提示声音
                var obj = data.obj;
                var messagesHtml = undefined;
                if (obj["noticeReceiveInfos"] > 0) {
                    hashNewMessage = true;
                    messagesHtml = "&nbsp;" + "<span  style='color: #FE6600;font-size: 16px;'>" + obj["noticeReceiveInfos"] + "</span>&nbsp条";
                } else {
                    messagesHtml = "&nbsp;" + "<span>" + obj["noticeReceiveInfos"] + "</span>&nbsp;条";
                }
                $("#p1").panel("setTitle", protal_titles[0] + messagesHtml);

                if (obj["tipPasswordType"] !== undefined && obj["tipPasswordType"] !== null) {
                    var tipMsg = '';
                    if (obj["tipPasswordType"] === 0) {
                        tipMsg = "您从未修改过用户密码，请<a onclick='try { parent.editLoginUserPassword();} catch(e) {editLoginUserPassword(); }'>修改用户密码</a>！";
                    } else if (obj["tipPasswordType"] === 1) {
                        tipMsg = "距离上次修改密码已经很长时间了，请<a onclick='try { parent.editLoginUserPassword();} catch(e) {editLoginUserPassword(); }'>修改用户密码</a>！";
                    }
                    if(tipMsg !== ''){
                        $.messager.show({
                            title: '<span class="tree-icon tree-file easyui-icon-tip easyui-tooltip"></span><span style="color: red;"> 提示信息！</span>',
                            msg: tipMsg,
                            height: 110,
                            timeout: 30 * 1000,
                            showType: 'slide' //null,slide,fade,show.
                        });
                    }

                }

            } else {
            }
        }
    });
}

var login_password_dialog;
var login_password_form;

function initLoginPasswordForm() {
    login_password_form = $('#login_password_form').form({
        url: ctxAdmin + '/sys/user/updateUserPassword',
        onSubmit: function (param) {
            param.upateOperate = '1';
            return $(this).form('validate');
        },
        success: function (data) {
            var json = eval('(' + data + ')');
            if (json.code === 1) {
                login_about_dialog.dialog('close');
                eu.showMsg(json.msg);//操作结果提示
            } else if (json.code === 2) {
                $.messager.alert('提示信息！', json.msg, 'warning', function () {
                    var userId = $('#login_password_form_id').val();
                    $(this).form('clear');
                    $('#login_password_form_id').val(userId);
                    if (json.obj) {
                        $('#login_password_form input[name="' + json.obj + '"]').focus();
                    }
                });
            } else {
                eu.showAlertMsg(json.msg, 'error');
            }
        }
    });
}

function editLoginUserPassword() {
    //弹出对话窗口
    login_password_dialog = $('<div/>').dialog({
        title: '&nbsp;修改用户密码',
        top: 100,
        width: 460,
        height: 240,
        modal: true,
        iconCls: 'easyui-icon-edit',
        href: ctxAdmin + '/common/layout/north-password',
        buttons: [{
            text: '保存',
            iconCls: 'easyui-icon-save',
            handler: function () {
                login_password_form.submit();
            }
        }, {
            text: '关闭',
            iconCls: 'easyui-icon-cancel',
            handler: function () {
                login_password_dialog.dialog('destroy');
            }
        }],
        onClose: function () {
            login_password_dialog.dialog('destroy');
        },
        onLoad: function () {
            initLoginPasswordForm();
        }
    });
}