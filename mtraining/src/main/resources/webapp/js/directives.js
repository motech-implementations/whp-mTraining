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
                    colNames: ['ID', scope.msg('mtraining.courseId'), scope.msg('mtraining.courseName'), scope.msg('mtraining.description'), scope.msg('mtraining.status'),
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
                        name: 'content',
                        index: 'content',
                        align: 'center',
                        width: 200
                    }, {
                        name: 'state',
                        index: 'state',
                        align: 'center',
                        width: 60
                    },{
                        name: 'content',
                        index: 'content',
                        align: 'center',
                        width: 100
                    }, {
                        name: 'creationDate',
                        index: 'creationDate',
                        align: 'center',
                        width: 70
                    }, {
                        name: 'modificationDate',
                        index: 'modificationDate',
                        align: 'center',
                        width: 70
                    }],
                    width: '100%',
                    height: 'auto',
                    sortname: 'modificationDate',
                    sortorder: 'desc',
                    loadComplete : function(array) {
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                    },
                    gridComplete: function () {
                      elem.jqGrid('setGridWidth', '100%');
                    },
                    onCellSelect: function (rowId, iRow, iCol, e) {
                        var rowData = $('#emailLoggingTable').jqGrid('getRowData', rowId);
                        scope.$emit('courseClick', rowData.id);
                    }
                });
            }
        };
    });
}());