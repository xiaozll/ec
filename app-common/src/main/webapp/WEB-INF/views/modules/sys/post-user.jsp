<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var organId = '${organId}';
    var userIds = ${fns:toJson(userIds)};
</script>
<script type="text/javascript" src="${ctxStatic}/app/modules/sys/post-user${yuicompressor}.js?_=${sysInitTime}" charset="utf-8"></script>
<div>
    <form id="post_user_form" class="dialog-form" method="post">
        <input type="hidden" id="id" name="id" value="${model.id}"/>
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" value="${model.version}"/>
        <input type="hidden" name="organId" value="${model.organId}"/>
        <div>
            <label style="vertical-align: top;">岗位用户:</label>
            <input id="userIds" name="userIds"/>
        </div>
    </form>
</div>