(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('mtraining.directives', []);

    directives.directive('fileModel', function ($parse) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var model = $parse(attrs.fileModel);
                var modelSetter = model.assign;

                element.bind('change', function () {
                    scope.$apply(function () {
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    });

    directives.directive('coursesGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/courses',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.courseName'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
                        scope.msg('mtraining.filename'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'name',
                        index: 'name',
                        align: 'center',
                        width: 155
                    }, {
                        name: 'description',
                        index: 'description',
                        align: 'center',
                        sortable: false,
                        width: 200
                    }, {
                        name: 'state',
                        index: 'state',
                        align: 'center',
                        width: 60
                    },{
                        name: 'externalId',
                        index: 'externalId',
                        align: 'center',
                        sortable: false,
                        width: 100
                    }, {
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.coursesGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    },
                    onCellSelect: function (rowId, iRow, iCol, e) {
                        var rowData = $('#coursesListTable').jqGrid('getRowData', rowId);
                        scope.$emit('courseClick', rowData.id);
                    }
                });
            }
        };
    });

    directives.directive('modulesGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/modules',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.moduleName'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
                        scope.msg('mtraining.filename'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'name',
                        index: 'name',
                        align: 'center',
                        width: 155
                    }, {
                        name: 'description',
                        index: 'description',
                        align: 'center',
                        sortable: false,
                        width: 200
                    }, {
                        name: 'state',
                        index: 'state',
                        align: 'center',
                        width: 60
                    },{
                        name: 'externalId',
                        index: 'externalId',
                        align: 'center',
                        sortable: false,
                        width: 100
                    }, {
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.modulesGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    },
                    onCellSelect: function (rowId, iRow, iCol, e) {
                        var rowData = $('#modulesListTable').jqGrid('getRowData', rowId);
                        scope.$emit('moduleClick', rowData.id);
                    }
                });
            }
        };
    });

    directives.directive('chaptersGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/chapters',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.chapterName'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
                        scope.msg('mtraining.filename'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'name',
                        index: 'name',
                        align: 'center',
                        width: 155
                    }, {
                        name: 'description',
                        index: 'description',
                        align: 'center',
                        sortable: false,
                        width: 200
                    }, {
                        name: 'state',
                        index: 'state',
                        align: 'center',
                        width: 60
                    },{
                        name: 'externalId',
                        index: 'externalId',
                        align: 'center',
                        sortable: false,
                        width: 100
                    }, {
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.chaptersGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    },
                    onCellSelect: function (rowId, iRow, iCol, e) {
                        var rowData = $('#chaptersListTable').jqGrid('getRowData', rowId);
                        scope.$emit('chapterClick', rowData.id);
                    }
                });
            }
        };
    });

    directives.directive('messagesGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/lessons',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.messageName'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
                        scope.msg('mtraining.filename'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'name',
                        index: 'name',
                        align: 'center',
                        width: 155
                    }, {
                        name: 'description',
                        index: 'description',
                        align: 'center',
                        sortable: false,
                        width: 200
                    }, {
                        name: 'state',
                        index: 'state',
                        align: 'center',
                        width: 60
                    },{
                        name: 'externalId',
                        index: 'externalId',
                        align: 'center',
                        sortable: false,
                        width: 100
                    }, {
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.messagesGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    },
                    onCellSelect: function (rowId, iRow, iCol, e) {
                        var rowData = $('#messagesListTable').jqGrid('getRowData', rowId);
                        scope.$emit('messageClick', rowData.id);
                    }
                });
            }
        };
        });

        directives.directive('quizzesGrid', function($http) {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    var elem = angular.element(element), filters;

                    elem.jqGrid({
                        url: '../mtraining/web-api/quizzes',
                        datatype: 'json',
                        jsonReader:{
                            repeatitems:false,
                            root: function (obj) {
                                return obj;
                            }
                        },
                        prmNames: {
                            sort: 'sortColumn',
                            order: 'sortDirection'
                        },
                        shrinkToFit: true,
                        forceFit: true,
                        autowidth: true,
                        rownumbers: true,
                        rowNum: 10,
                        rowList: [10, 20, 50],
                        colNames: ['rowId', 'id', scope.msg('mtraining.quizName'), scope.msg('mtraining.passPercentage'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
                            scope.msg('mtraining.filename'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                        colModel: [{
                           name: 'rowId',
                           index: 'rowId',
                           hidden: true,
                           key: true
                        }, {
                           name: 'id',
                           index: 'id',
                           align: 'center',
                           hidden: true,
                        }, {
                            name: 'name',
                            index: 'name',
                            align: 'center',
                            width: 155
                        }, {
                            name: 'passPercentage',
                            index: 'passPercentage',
                            align: 'center',
                            width: 50
                        }, {
                            name: 'description',
                            index: 'description',
                            align: 'center',
                            sortable: false,
                            width: 200
                        }, {
                            name: 'state',
                            index: 'state',
                            align: 'center',
                            width: 60
                        },{
                            name: 'externalId',
                            index: 'externalId',
                            align: 'center',
                            sortable: false,
                            width: 100
                        }, {
                            name: 'creationDate',
                            index: 'creationDate',
                            align: 'center',
                            width: 70,
                            formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                        }, {
                            name: 'modificationDate',
                            index: 'modificationDate',
                            align: 'center',
                            width: 70,
                            formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                        }],
                        pager: '#' + attrs.quizzesGrid,
                        width: '100%',
                        height: 'auto',
                        sortname: 'modificationDate',
                        sortorder: 'desc',
                        viewrecords: true,
                        loadonce: true,
                        gridview: true,
                        loadComplete : function(array) {
                            $('.ui-jqgrid-htable').addClass('table-lightblue');
                            $('.ui-jqgrid-btable').addClass("table-lightblue");
                            if (elem.getGridParam('datatype') === "json") {
                                setTimeout(function () {
                                   elem.trigger("reloadGrid");
                                }, 10);
                            }
                        },
                        gridComplete: function () {
                          elem.jqGrid('setGridWidth', '100%');
                        },
                        onCellSelect: function (rowId, iRow, iCol, e) {
                            var rowData = $('#quizzesListTable').jqGrid('getRowData', rowId);
                            scope.$emit('quizClick', rowData.id);
                        }
                    });
                }
            };
        });

    directives.directive('providersGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/providers',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.remediId'), scope.msg('mtraining.callerId'), scope.msg('mtraining.providerStatus'),
                        scope.msg('mtraining.location'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'remediId',
                        index: 'remediId',
                        align: 'center',
                        width: 70
                    }, {
                        name: 'callerId',
                        index: 'callerId',
                        align: 'center',
                        sortable: false,
                        width: 70
                    }, {
                        name: 'providerStatus',
                        index: 'providerStatus',
                        align: 'center',
                        width: 110
                    }, {
                        name: 'location',
                        index: 'location',
                        align: 'center',
                        width: 130,
                        formatter: locationFormatter
                    }, {
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 50,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 50,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.providersGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    },
                    onCellSelect: function (rowId, iRow, iCol, e) {
                        var rowData = $('#providersListTable').jqGrid('getRowData', rowId);
                        scope.$emit('providerClick', rowData.id, rowData.remediId, rowData.callerId);
                    }
                });

                function locationFormatter(cellValue, options, rowObject) {
                    if (!cellValue){
                        return '';
                    }
                    return cellValue.state + " - " + cellValue.district + " - " + cellValue.block;
                }
            }
        };
    });


    directives.directive('locationsGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/locations',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.state'), scope.msg('mtraining.district'), scope.msg('mtraining.block'),
                        scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'state',
                        index: 'state',
                        align: 'center',
                        width: 160
                    }, {
                        name: 'district',
                        index: 'district',
                        align: 'center',
                        width: 160
                    }, {
                        name: 'block',
                        index: 'block',
                        align: 'center',
                        width: 160
                    },{
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.locationsGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    }
                });
            }
        };
    });


    directives.directive('coursePublicationAttemptsGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/coursePublicationAttempts',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false,
                        root: function (obj) {
                            return obj;
                        }
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    forceFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colNames: ['rowId', 'id', scope.msg('mtraining.published'), scope.msg('mtraining.responseCode'), scope.msg('mtraining.responseMessage'),
                        scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
                    colModel: [{
                       name: 'rowId',
                       index: 'rowId',
                       hidden: true,
                       key: true
                    }, {
                       name: 'id',
                       index: 'id',
                       align: 'center',
                       hidden: true,
                    }, {
                        name: 'publishedToIvr',
                        index: 'publishedToIvr',
                        align: 'center',
                        width: 50
                    }, {
                        name: 'responseCode',
                        index: 'responseCode',
                        align: 'center',
                        width: 80
                    }, {
                        name: 'responseMessage',
                        index: 'responseMessage',
                        align: 'center',
                        width: 300
                    },{
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    }],
                    pager: '#' + attrs.coursePublicationAttemptsGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    viewrecords: true,
                    subGrid: true,
                    subGridOptions: {
                        "plusicon" : "ui-icon-triangle-1-e",
                        "minusicon" : "ui-icon-triangle-1-s",
                        "openicon" : "ui-icon-arrowreturn-1-e",
                        "reloadOnExpand" : false,
                        "selectOnExpand" : true
                    },
                    subGridRowExpanded: function(subgrid_id, row_id) {
                        var subgrid_table_id, pager_id;
                        subgrid_table_id = subgrid_id+"_t";
                        pager_id = "p_"+subgrid_table_id;
                        $("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class=''></table>");
                        var message = $('#coursePublicationAttemptListTable').jqGrid('getRowData', row_id).responseMessage;
                        var messageData = [ {"message": message} ];

                        jQuery("#"+subgrid_table_id).jqGrid({
                            datatype: "local",
                            data: messageData,
                            colNames: [scope.msg('mtraining.responseMessage')],
                            colModel: [
                                {name:"message",index:"message", align:"left", sortable: false, width: 880}
                            ],
                            rowNum:1,
                            pager: pager_id,
                            sortname: 'message',
                            sortorder: "asc",
                            height: '100%'
                        });
                        jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false});
                        $('.ui-subgrid .ui-jqgrid-labels').css({'display':'none'});
                        $('.ui-subgrid td').css({'word-wrap':'break-word', 'white-space':'normal'});

                    },
                    loadonce: true,
                    gridview: true,
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        if (elem.getGridParam('datatype') === "json") {
                            setTimeout(function () {
                               elem.trigger("reloadGrid");
                            }, 10);
                        }
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    }
                });
            }
        };
    });

}());