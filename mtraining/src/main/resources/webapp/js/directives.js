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
                        name: 'filename',
                        index: 'filename',
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
                        name: 'filename',
                        index: 'filename',
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
                        name: 'filename',
                        index: 'filename',
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
                        name: 'filename',
                        index: 'filename',
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
                            name: 'filename',
                            index: 'filename',
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
                        width: 100
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
                        width: 120
                    }, {
                        name: 'location.name',
                        index: 'location.name',
                        align: 'center',
                        width: 90
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
                        scope.$emit('providerClick', rowData.id);
                    }
                });
            }
        };
    });

}());