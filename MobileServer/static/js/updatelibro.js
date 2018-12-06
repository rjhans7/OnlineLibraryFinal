$(function(){
    var url = "libros";

    $("#grid").dxDataGrid({
        dataSource: DevExpress.data.AspNet.createStore({
            key: "ID",
            loadUrl: url ,
            insertUrl: url ,
            updateUrl: url ,
            deleteUrl: url ,
            onBeforeSend: function(method, ajaxOptions) {
                ajaxOptions.xhrFields = { withCredentials: true };
            }
        }),
        editing: {
            allowUpdating: true,
            allowDeleting: true,
            allowAdding: false
        },
        remoteOperations: {
            sorting: true,
            paging: true
        },
        paging: {
            pageSize: 12
        },
        pager: {
            showPageSizeSelector: true,
            allowedPageSizes: [8, 12, 20]
        },
        columns: [{
            dataField: "ID",
            dataType: "number",
            allowEditing: false
        }, {
            dataField: "titulo"
        }, {
            dataField: "autor"
        }, {
            dataField: "nacionalidad"
        }, {
            dataField: "genero"
        }, {
            dataField: "descripcion"
        }, ],
    }).dxDataGrid("instance");
});