<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>参数配置</title>
    <meta name="decorator" content="default_sys"/>
    <script type="text/javascript">
        var data = ${fns:toJson(data)};
        $(document).ready(function () {
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

            $.formLoadData($("#inputForm"),data);

        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctxAdmin}/sys/config/paramForm">参数配置</a></li>
    <li><a href="${ctxAdmin}/sys/config">参数配置（更多）</a></li>
</ul><br/>
<form id="inputForm" action="${ctxAdmin}/sys/config/saveParam" method="post" class="form-horizontal">
    <tags:message content="${message}"/>

    <h5 class="page-header">基础配置</h5>
    <div class="control-group">
        <label class="control-label">应用版本：</label>
        <div class="controls">
            <input name="app.version" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">应用名称：</label>
        <div class="controls">
            <input name="app.name" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">应用名称（全称）：</label>
        <div class="controls">
            <input name="app.fullName" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">应用名称（简称）：</label>
        <div class="controls">
            <input name="app.shortName" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">厂商信息：</label>
        <div class="controls">
            <input name="app.productName" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">厂商URL：</label>
        <div class="controls">
            <input name="app.productURL" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">客服信息：</label>
        <div class="controls">
            <input name="app.productContact" type="text" class="input-xxlarge"/>
            <span class="help-inline"></span>
        </div>
    </div>

    <h5 class="page-header">系统安全</h5>

    <div class="control-group">
        <label class="control-label">系统安全：</label>
        <div class="controls">
            <label><input name="security.on" type="radio" value="true"/>启用</label>
            <label><input name="security.on" type="radio" value="false"/>禁用</label>
            <span class="help-inline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;定期修改密码等</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">最大登录用户数限制：</label>
        <div class="controls">
            <input name="sessionUser.MaxSize" type="number" min="-1" max="999999" class="input-small"/>
            <span class="help-inline">不允许登录：小于0 不限制；0 限制：具体数值</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">用户可创建会话数量：</label>
        <div class="controls">
            <input name="sessionUser.UserSessionSize" type="number" min="0" max="100" class="input-small"/>
            <span class="help-inline">用户可创建会话数量 默认值：0</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">用户密码更新周期：</label>
        <div class="controls">
            <input name="password.updateCycle" type="number" min="0" max="9999" class="input-small"/>
            <span class="help-inline">默认值：30（天）</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">密码重复校验：</label>
        <div class="controls">
            <input name="password.repeatCount" type="number" min="0" max="100" class="input-small"/>
            <span class="help-inline">用户密码至少多少次内不能重复 默认值：0</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">日志保留时间：</label>
        <div class="controls">
            <input name="system.logKeepTime" type="number" min="0" max="9999" class="input-small"/>
            <span class="help-inline">数据库审计日志 单位：天</span>
        </div>
    </div>


    <div class="control-group">
        <label class="control-label">登录账号白名单：</label>
        <div class="controls">
            <textarea name="system.security.limit.user.whitelist" class="input-xxlarge"></textarea>
            <span class="help-inline">不受“最大登录用户数限制”，每行一个或多个之间以";"分割</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">IP访问控制：</label>
        <div class="controls">
            <label><input name="system.security.limit.ip.enable" type="radio" value="true"/>启用</label>
            <label><input name="system.security.limit.ip.enable" type="radio" value="false"/>禁用</label>
            <span class="help-inline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;启用IP访问控制，全局配置</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">仅限IP白名单：</label>
        <div class="controls">
            <label><input name="system.security.limit.ip.whiteEnable" type="radio" value="true"/>启用</label>
            <label><input name="system.security.limit.ip.whiteEnable" type="radio" value="false"/>禁用</label>
            <span class="help-inline">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;仅限IP白名单，仅限IP白名单可以访问，IP黑名单无需设置</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">IP白名单：</label>
        <div class="controls">
            <textarea name="system.security.limit.ip.whitelist" class="input-xxlarge"></textarea>
            <span class="help-inline">系统访问IP白名单，127.0.0.1、localhost无需配置，每行一个或多个之间以";"分割</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">IP黑名单：</label>
        <div class="controls">
            <textarea name="system.security.limit.ip.blacklist" class="input-xxlarge"></textarea>
            <span class="help-inline">系统访问IP黑名单，多个之间以"，"分割</span>
        </div>
    </div>


    <div class="form-actions">
        <e:hasPermission name="sys:config:edit">
            <input id="btnSave" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
        </e:hasPermission>
<%--        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>--%>
    </div>
</form>


</body>
</html>
