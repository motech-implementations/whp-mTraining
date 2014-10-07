(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('mtraining.directives', []);

    // get json attribute safely
    function getAttr(obj, attrName) {
        if (obj != null) {
            return obj[attrName];
        } else {
            return null;
        }
    }

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
                    colNames: ['rowId', 'id', scope.msg('mtraining.courseName'), scope.msg('mtraining.courseLocation'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
                        scope.msg('mtraining.filename'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated'), scope.msg('mtraining.duration')],
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
                        name: 'location',
                        index: 'location',
                        align: 'center',
                        width: 130,
                        formatter: locationFormatter
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
                    }, {
                        name: 'duration',
                        index: 'duration',
                        align: 'center',
                        width: 60
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
                    function locationFormatter(cellValue, options, rowObject) {
                        if (!cellValue){
                            return '';
                        }
                        return cellValue.state;
                    }                    
                
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
                            scope.msg('mtraining.noOfQuestionsToBePlayed'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
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
                            name: 'noOfQuestionsToBePlayed',
                            index: 'noOfQuestionsToBePlayed',
                            align: 'center',
                            sortable: false,
                            width: 50
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
                    return cellValue.state;
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
                    colNames: ['rowId', 'id', scope.msg('mtraining.state'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
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
                    colNames: ['rowId', 'id', scope.msg('mtraining.published'), scope.msg('mtraining.responseCode'), scope.msg('mtraining.courseName'),
                        scope.msg('mtraining.responseMessage'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
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
                        width: 50,
                        formatter: function (cellValue, options, rowObject) {
                            return (cellValue == true) ? 'Success' : 'Failure';
                        }
                    }, {
                        name: 'responseCode',
                        index: 'responseCode',
                        align: 'center',
                        width: 80
                    }, {
                        name: 'courseName',
                        index: 'courseName',
                        align: 'center',
                        width: 80
                    }, {
                        name: 'responseMessage',
                        index: 'responseMessage',
                        align: 'center',
                        width: 260,
                        cellattr: function (rowId, tv, rawObject, cm, rdata) {
                            return 'style="max-width: 300px; white-space: nowrap"';
                        }
                    },{
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 110
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'},
                        hidden: true,
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


    directives.directive('bookmarkRequestsGrid', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters;

                elem.jqGrid({
                    url: '../mtraining/web-api/bookmarkRequests',
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
                    colNames: ['rowId', 'id', scope.msg('mtraining.callerId'), scope.msg('mtraining.remediId'), scope.msg('mtraining.requestType'),
                     scope.msg('mtraining.responseCode'), scope.msg('mtraining.responseMessage'), scope.msg('mtraining.courseStartTime'), scope.msg('mtraining.timeLeftToCompleteCourse'),
                     scope.msg('mtraining.course'), scope.msg('mtraining.moduleWhp'), scope.msg('mtraining.chapter'), scope.msg('mtraining.message'), scope.msg('mtraining.quiz'),
                     scope.msg('mtraining.courseStatus'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
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
                        name: 'callerId',
                        index: 'callerId',
                        align: 'center',
                        width: 60
                    }, {
                        name: 'remediId',
                        index: 'remediId',
                        align: 'center',
                        width: 60
                    }, {
                        name: 'requestType',
                        index: 'requestType',
                        align: 'center',
                        width: 40
                    }, {
                        name: 'responseCode',
                        index: 'responseCode',
                        align: 'center',
                        width: 40
                    }, {
                        name: 'responseMessage',
                        index: 'responseMessage',
                        align: 'center',
                        width: 70
                    },{
                        name: 'courseStartTime',
                        index: 'courseStartTime',
                        align: 'center',
                        width: 60,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'}
                    },{
                        name: 'timeLeftToCompleteCourseInHrs',
                        index: 'timeLeftToCompleteCourseInHrs',
                        align: 'center',
                        width: 50
                    },{
                        name: 'bookmarkReport.courseIdentifier.contentId',
                        index: 'course',
                        align: 'center',
                        width: 70,
                        hidden: true
                    },{
                        name: 'bookmarkReport.moduleIdentifier.contentId',
                        index: 'module',
                        align: 'center',
                        width: 70,
                        hidden: true
                    },{
                        name: 'bookmarkReport.chapterIdentifier.contentId',
                        index: 'chapter',
                        align: 'center',
                        width: 70,
                        hidden: true
                    },{
                        name: 'bookmarkReport.messageIdentifier.contentId',
                        index: 'message',
                        align: 'center',
                        width: 70,
                        hidden: true
                    },{
                        name: 'bookmarkReport.quizIdentifier.contentId',
                        index: 'quiz',
                        align: 'center',
                        width: 70,
                        hidden: true
                    },{
                        name: 'courseStatus',
                        index: 'courseStatus',
                        align: 'center',
                        width: 40
                    },{
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 60,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'},
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70,
                        formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'},
                        hidden: true,
                    }],
                    pager: '#' + attrs.bookmarkRequestsGrid,
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
                        var rowData = $('#bookmarkRequestListTable').jqGrid('getRowData', row_id);
                        $.ajaxSetup({
                            async: false
                        });
                        var data = [ {
                            "responseMessage": rowData.responseMessage,
                            "course": (rowData['bookmarkReport.courseIdentifier.contentId'].length == 0) ? "" :
                                 getAttr($.getJSON("../mtraining/web-api/courseByContentId/" + rowData['bookmarkReport.courseIdentifier.contentId']).responseJSON, 'name'),
                            "module": (rowData['bookmarkReport.moduleIdentifier.contentId'].length == 0) ? "" :
                                 getAttr($.getJSON("../mtraining/web-api/moduleByContentId/" + rowData['bookmarkReport.moduleIdentifier.contentId']).responseJSON, 'name'),
                            "chapter": (rowData['bookmarkReport.chapterIdentifier.contentId'].length == 0) ? "" :
                                 getAttr($.getJSON("../mtraining/web-api/chapterByContentId/" + rowData['bookmarkReport.chapterIdentifier.contentId']).responseJSON, 'name'),
                            "message": (rowData['bookmarkReport.messageIdentifier.contentId'].length == 0) ? "" :
                                 getAttr($.getJSON("../mtraining/web-api/lessonByContentId/" + rowData['bookmarkReport.messageIdentifier.contentId']).responseJSON, 'name'),
                            "quiz": (rowData['bookmarkReport.quizIdentifier.contentId'].length == 0) ? "" :
                                 getAttr($.getJSON("../mtraining/web-api/quizByContentId/" + rowData['bookmarkReport.quizIdentifier.contentId']).responseJSON.name, 'name')
                        } ];

                        jQuery("#"+subgrid_table_id).jqGrid({
                            datatype: "local",
                            data: data,
                            colNames: [scope.msg('mtraining.responseMessage'), scope.msg('mtraining.course'), scope.msg('mtraining.moduleWhp'),
                            scope.msg('mtraining.chapter'), scope.msg('mtraining.message'), scope.msg('mtraining.quiz')],
                            colModel: [
                                {
                                    name: 'responseMessage',
                                    index: 'responseMessage',
                                    align: 'center',
                                    width: 250
                                },{
                                    name: 'course',
                                    index: 'course',
                                    align: 'center',
                                    width: 120
                                },{
                                    name: 'module',
                                    index: 'module',
                                    align: 'center',
                                    width: 120
                                },{
                                    name: 'chapter',
                                    index: 'chapter',
                                    align: 'center',
                                    width: 120
                                },{
                                    name: 'message',
                                    index: 'message',
                                    align: 'center',
                                    width: 120
                                },{
                                    name: 'quiz',
                                    index: 'quiz',
                                    align: 'center',
                                    width: 120
                                }
                            ],
                            rowNum:1,
                            pager: pager_id,
                            sortname: 'message',
                            sortorder: "asc",
                            height: '100%'
                        });
                        jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false});
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

        directives.directive('callLogGrid', function($http) {
            var idsToNames = [];
            $.ajaxSetup({
                async: false
            });
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    var elem = angular.element(element), filters;

                    elem.jqGrid({
                        url: '../mtraining/web-api/callLogs',
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
                        colNames: ['rowId', 'id', scope.msg('mtraining.callerId'), scope.msg('mtraining.remediId'), scope.msg('mtraining.course'),
                        scope.msg('mtraining.callLogRecordType'), scope.msg('mtraining.startTime'), scope.msg('mtraining.endTime'),
                        scope.msg('mtraining.status'), scope.msg('mtraining.dateCreated')],
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
                            name: 'callerId',
                            index: 'callerId',
                            align: 'center',
                            width: 70
                        }, {
                            name: 'remedyId',
                            index: 'remedyId',
                            align: 'center',
                            width: 50
                        }, {
                            name: 'courseId',
                            index: 'courseId',
                            align: 'center',
                            width: 80,
                            formatter: function (cellValue, options, rowObject) {
                                if (cellValue.length > 0) {
                                    for(var i = 0; i < idsToNames.length; i++) {
                                        if (idsToNames[i].id == cellValue) {
                                            return idsToNames[i].name;
                                        }
                                    }
                                    var name = getAttr($.getJSON("../mtraining/web-api/courseByContentId/" + cellValue).responseJSON, 'name');
                                    idsToNames.push({"id": cellValue, "name": name});
                                    return name;
                                }
                                return "";
                            }
                        }, {
                            name: 'callLogRecordType',
                            index: 'callLogRecordType',
                            align: 'center',
                            width: 40
                        },{
                            name: 'startTime',
                            index: 'startTime',
                            align: 'center',
                            width: 80,
                        },{
                            name: 'endTime',
                            index: 'endTime',
                            align: 'center',
                            width: 80,
                        },{
                            name: 'status',
                            index: 'status',
                            align: 'center',
                            width: 50,
                        },{
                            name: 'creationDate',
                            index: 'creationDate',
                            align: 'center',
                            formatter:'date', formatoptions: {srcformat: 'Y-m-d H:i:s', newformat:'Y/m/d'},
                            width: 60
                        }],
                        pager: '#' + attrs.callLogGrid,
                        width: '100%',
                        height: 'auto',
                        sortname: 'creationDate',
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


        directives.directive('quizAttemptsGrid', function($http) {
            return {
                restrict: 'A',
                link: function(scope, element, attrs) {
                    var elem = angular.element(element), filters;

                    elem.jqGrid({
                        url: '../mtraining/web-api/quizAttempts',
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
                        colNames: ['rowId', 'id', scope.msg('mtraining.callerId'), scope.msg('mtraining.remediId'), scope.msg('mtraining.score'),
                         scope.msg('mtraining.isPassed'), scope.msg('mtraining.incompleteAttempt'), scope.msg('mtraining.course'), scope.msg('mtraining.moduleWhp'),
                         scope.msg('mtraining.chapter'), scope.msg('mtraining.quiz'), scope.msg('mtraining.dateCreated'), scope.msg('mtraining.lastUpdated')],
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
                            name: 'callerId',
                            index: 'callerId',
                            align: 'center',
                            width: 90
                        }, {
                            name: 'remedyId',
                            index: 'remedyId',
                            align: 'center',
                            width: 70
                        }, {
                            name: 'score',
                            index: 'score',
                            align: 'center',
                            width: 40
                        }, {
                            name: 'isPassed',
                            index: 'isPassed',
                            align: 'center',
                            width: 40,
                            formatter: function (cellValue, options, rowObject) {
                                return (cellValue == true) ? 'Success' : 'Failure';
                            }
                        }, {
                            name: 'incompleteAttempt',
                            index: 'incompleteAttempt',
                            align: 'center',
                            width: 40,
                            formatter: function (cellValue, options, rowObject) {
                                return (cellValue == true) ? 'Yes' : 'No';
                            }
                        },{
                            name: 'courseIdentifier.contentId',
                            index: 'course',
                            align: 'center',
                            width: 70,
                            hidden: true
                        },{
                            name: 'moduleIdentifier.contentId',
                            index: 'module',
                            align: 'center',
                            width: 70,
                            hidden: true
                        },{
                            name: 'chapterIdentifier.contentId',
                            index: 'chapter',
                            align: 'center',
                            width: 70,
                            hidden: true
                        },{
                            name: 'quizIdentifier.contentId',
                            index: 'quiz',
                            align: 'center',
                            width: 70,
                            hidden: true
                        },{
                            name: 'creationDate',
                            index: 'creationDate',
                            align: 'center',
                            width: 70
                        },{
                            name: 'modificationDate',
                            index: 'modificationDate',
                            align: 'center',
                            width: 70,
                            hidden: true
                        }],
                        pager: '#' + attrs.quizAttemptsGrid,
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
                            var rowData = $('#quizAttemptListTable').jqGrid('getRowData', row_id);
                            $.ajaxSetup({
                                async: false
                            });
                            var data = [ {
                                "responseMessage": rowData.responseMessage,
                                "course": (rowData['courseIdentifier.contentId'].length == 0) ? "" :
                                     getAttr($.getJSON("../mtraining/web-api/courseByContentId/" + rowData['courseIdentifier.contentId']).responseJSON, 'name'),
                                "module": (rowData['moduleIdentifier.contentId'].length == 0) ? "" :
                                     getAttr($.getJSON("../mtraining/web-api/moduleByContentId/" + rowData['moduleIdentifier.contentId']).responseJSON, 'name'),
                                "chapter": (rowData['chapterIdentifier.contentId'].length == 0) ? "" :
                                     getAttr($.getJSON("../mtraining/web-api/chapterByContentId/" + rowData['chapterIdentifier.contentId']).responseJSON, 'name'),
                                "quiz": (rowData['quizIdentifier.contentId'].length == 0) ? "" :
                                     getAttr($.getJSON("../mtraining/web-api/quizByContentId/" + rowData['quizIdentifier.contentId']).responseJSON, 'name')
                            } ];

                            jQuery("#"+subgrid_table_id).jqGrid({
                                datatype: "local",
                                data: data,
                                colNames: [scope.msg('mtraining.course'), scope.msg('mtraining.moduleWhp'),
                                scope.msg('mtraining.chapter'), scope.msg('mtraining.quiz')],
                                colModel: [
                                    {
                                        name: 'course',
                                        index: 'course',
                                        align: 'center',
                                        width: 180
                                    },{
                                        name: 'module',
                                        index: 'module',
                                        align: 'center',
                                        width: 180
                                    },{
                                        name: 'chapter',
                                        index: 'chapter',
                                        align: 'center',
                                        width: 180
                                    },{
                                        name: 'quiz',
                                        index: 'quiz',
                                        align: 'center',
                                        width: 180
                                    }
                                ],
                                rowNum:1,
                                pager: pager_id,
                                sortname: 'quiz',
                                sortorder: "asc",
                                height: '100%'
                            });
                            jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false});
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
