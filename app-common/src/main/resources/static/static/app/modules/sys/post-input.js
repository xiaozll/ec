var organIds = organIds;

var $organ_combotree;
var $organIds_combotree;
$(function(){
    loadData();
    //级联点击事件
    $('#changeMode').click(function(){
        var tempData =  $organIds_combotree.combotree('getValues');
        $organIds_combotree.combotree({
            cascadeCheck:$(this).is(':checked'),
            onShowPanel:function(){
                var tree =  $(this).combotree("tree")
                var checkeNodes = tree.tree("getChecked");
                var tempValues = new Array();
                $.each(checkeNodes,function(index,nodeData){
                    tempValues.push(nodeData.id);
                });
                $organIds_combotree.combotree("setValues",tempValues);
            }
        });
        $organIds_combotree.combotree('setValues',tempData);
        $organIds_combotree.combotree("showPanel");
    });

});

function loadData(){
    $.ajax({
        url: ctxAdmin + '/sys/organ/tree?dataScope=2&cascade=true',
        type: 'post',
        data: {},
        traditional: true,
        dataType: 'json',
        success: function (data) {
            loadOrgan(data);
            loadOrganIds(data);
        }
    });
}

function loadOrgan(data){
    $organ_combotree = $("#organId").combotree({
        data:data,
        required:true,
        missingMessage:'请选择所属机构.',
        multiple:false,
        editable:false
    });
}
function loadOrganIds(data){
    $organIds_combotree = $("#organIds").combotree({
        data:data,
        cascadeCheck:false,
        multiple:true,
        editable:false,
        value:organIds
    });
}