(function () {
    'use strict';

    /* Controllers */

    var controllers = angular.module("mtraining.controllers", []);

    $.postJSON = function(url, data, callback) {
        return jQuery.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        'type': 'POST',
        'url': url,
        'data': JSON.stringify(data),
        'dataType': 'json',
        'success': callback
        });
    };

    controllers.controller('treeViewController', function ($scope, Chapter, Quiz) {
        var jArray = new Array();
        var iterator = 0;
        var node_properties = new Array();
        $scope.alertMessage = undefined;

        $.getJSON('../mtraining/web-api/modules', function(data) {
            $scope.modules = data;
        });
        $.getJSON('../mtraining/web-api/chapters', function(data) {
            $scope.chapters = data;
        });
        $.getJSON('../mtraining/web-api/lessons', function(data) {
            $scope.lessons = data;
        });
        $.getJSON('../mtraining/web-api/quizzes', function(data) {
            $scope.quizzes = data;
        });

        initTree();
         $('.draggable').sortable({
            connectWith: '.droppable',
            receive: receiveEventHandler
         });

         function receiveEventHandler(event, ui) {
            $scope.alertMessage = undefined;
            $("#publishCourse").prop('disabled', true);
            var type = $scope.childType;
            var cancelled = false;
            var parent = $scope.jstree.get_node($scope.jstree.get_selected()).original;
            if (ui.item.attr('class').indexOf("quiz") >= 0) {
                type = 'quiz';
                var item = $scope.quizNodes[ui.item.attr('idx')];
                var qid = ui.item.attr('qid');
                $.each($scope.children, function(idx, el) {
                    if (el.type === 'quiz') {
                        var childId = node_properties[el.id].id;
                        cancelled = true;
                        if (childId == qid) {
                            ui.sender.sortable('cancel');
                            return false;
                        } else {
                            // swap nodes
                            for(var i = 0; i < node_properties.length; i++) {
                                if (node_properties[i].id == node_properties[el.id].id) {
                                    var node = $scope.jstree.get_node(i);
                                    if (node_properties[node.parent].id == node_properties[parent.id].id) {
                                        $scope.jstree.delete_node(i);
                                        createNode(item.id, item.name, node.parent, parent.level + 1, type, item.state);
                                        $scope.jstree.create_node(node.parent, jArray[iterator], 'last', false, false);
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                var item = $scope.nodes[ui.item.attr('idx')];
            }
            if (!cancelled) {
                // create nodes
                for(var i = 1; i < node_properties.length; i++) {
                     if (node_properties[i].id == node_properties[parent.id].id) {
                        createNode(item.id, item.name, i, parent.level + 1, type, item.state);
                        $scope.jstree.create_node(i, jArray[iterator], 'last', false, false);
                    }
                }
            }
            if (type === 'quiz') {
                onChanged();
            }
            $scope.jstree.open_node(parent);
            $scope.$apply();
         }

        var chaptersWithQuizzes = [];
        function createRelations(o, type, relations) {
            var children = o.children;
            if (children) {
                $.each(children, function(idx, el) {
                    var item = $scope.jstree.get_node(el);
                    var relation = {
                       "parentId": node_properties[o.id].id,
                       "childId": node_properties[el].id,
                       "parentType": type
                    };
                    if ($.grep(relations, function (el, index) {
                                return el.parentId == relation.parentId && el.childId == relation.childId;
                              }).length == 0 && (item.type != 'quiz' || $.inArray(o.id, chaptersWithQuizzes) == -1)) {
                        relations.push(relation);
                        if (item.type == 'quiz') {
                            chaptersWithQuizzes.push(o.id);
                        }
                    }
                    if (item.original.type == 'module') {
                        createRelations(item, 'Course', relations);
                    } else if (item.original.type == 'chapter') {
                        createRelations(item, 'Chapter', relations);
                    }
                });
            }
        }

        $scope.saveRelations = function() {
            var courses = $scope.jstree.get_node(0).children;
            var relations = [];
            chaptersWithQuizzes = [];
            $.each(courses, function(idx, el) {
                createRelations($scope.jstree.get_node(el), 'CoursePlan', relations);
            });
            var stateMap = {};
            for(var i = 1; i < node_properties.length; i++) {
                if (stateMap[node_properties[i].id] == undefined) {
                    stateMap[node_properties[i].id] = node_properties[i].state;
                }
            }
            $scope.savingRelations = true;
            $.postJSON('../mtraining/web-api/updateRelations', relations, function() {
                $.postJSON('../mtraining/web-api/updateStates', stateMap, function() {
                    $scope.savingRelations = false;
                    $scope.alertMessage = $scope.msg('mtraining.savedRelations');
                    $("#publishCourse").prop('disabled', false);
                    $scope.$apply();
                });
            });
        }

        $scope.switchUnitState = function() {
            $("#publishCourse").prop('disabled', true);
            var idx = $('#jstree').jstree('get_selected');
            var node = $scope.jstree.get_node(idx);
            var state = "Active";
            if ($scope.isActive(node)) {
                state = "Inactive";
            }
            for(var i = 1; i < node_properties.length; i++) {
                if (node_properties[i].id == node_properties[node.id].id) {
                    node_properties[i].state = state;
                    var el = $scope.jstree.get_node(i, true);
                    el.attr("state", state);
                }
            }
        }

        $scope.isActive = function(node) {
            return node && node_properties[node.id].state == "Active"
        }

        $scope.publishCourse = function() {
            var idx = $('#jstree').jstree('get_selected');
            var node = $scope.jstree.get_node(idx);
            if (node && node.original && node.original.type === 'course') {
                var id = node_properties[idx].id;
                $scope.publishingCourse = true;
                $.get("../mtraining/web-api/publish/" + id, function(response) {
                    $scope.publishingCourse = false;
                    // internal server error - 500; ok - 800; missing files - 1001; network failure - 1002
                    var code = response.responseCode;
                    if (code == 800) {
                        $scope.alertMessage = $scope.msg('mtraining.publishedCourse');
                    } else if (code == 1001) {
                        $("#errorMessage").text($scope.msg('mtraining.error.missingFiles') + ": " + response.responseMessage);
                        $("#errorDialog").modal('show');
                    } else if (code == 1002 || code == 500) {
                        $("#errorMessage").text(response.responseMessage);
                        $("#errorDialog").modal('show');
                    }
                    $scope.$apply();
                });
            } else {
                $("#errorMessage").text($scope.msg('mtraining.error.noCourseSelected'));
                $("#errorDialog").modal('show');
            }
         }

        $scope.removeMember = function() {
            jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.node'), $scope.selected.text), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                if (val) {
                    $("#publishCourse").prop('disabled', true);
                    var idx = $('#jstree').jstree('get_selected');
                    var node = $scope.jstree.get_node(idx);
                    if (node.original.type == 'course' || node.original.type == 'root') {
                        return false;
                    }
                    $scope.alertMessage = undefined;
                    var id = node_properties[node.id].id;
                    for(var i = 1; i < node_properties.length; i++) {
                        if (node_properties[i].id == node_properties[node.id].id) {
                            var n = $scope.jstree.get_node(i);
                            if (node_properties[n.parent].id == node_properties[node.parent].id) {
                                $scope.jstree.delete_node(i);
                                $scope.jstree.delete_node(n.children);
                            }
                        }
                    }
                }
            });
        }

        $scope.cancel = function() {
            initTree();
            $scope.alertMessage = undefined;
            $("#publishCourse").prop('disabled', false);
        }

        $scope.isChildren = function(name) {
            var isChildren = false;
            $.each($scope.children, function(idx, el) {
                if (name === el.text) {
                    isChildren = true;
                }
            });
            return isChildren;
        }

        function createNode(id, text, parent, level, type, state) {
            iterator++;
            jArray[iterator] = {
                "id" : iterator,
                "text" : text,
                "parent" : parent,
                "state" : {
                        opened : false,
                        disabled : false,
                        selected : false
                    },
                li_attr : {"state": state},
                a_attr : {},
                "level" : level,
                "type" : type,
            }
            node_properties[iterator] = {
                id: id,
                state: state
            }
         }

        //Get JSON from server and rewrite it to tree's JSON format
        function initTree() {
            $('#jstree').jstree("destroy");
            var jsonURI = "../mtraining/web-api/all";
            $.getJSON(jsonURI,function (data) {
                fillJson(data, true, 0, "#");
                $scope.alert = true;
                renderTree();
            });
        };

        function renderTree () {
            $('#jstree').jstree({
                "plugins" : ["state", "dnd", "search", "types"],
                "core" : {
                    'data' : jArray,
                    'check_callback' : function (operation, node, node_parent, node_position) {
                        if (operation === "move_node") {
                            if (node_parent.original && node.original.level === node_parent.original.level + 1) {
                                $("#publishCourse").prop('disabled', true);
                                var children = node_parent.children;
                                for(var i = 0; i < node_parent.children.length; i++) {
                                    if (node_properties[node_parent.children[i]].id == node_properties[node.id].id) {
                                        return false;
                                    }
                                }
                                return true;
                            };
                            return false;
                        }
                    }
                },
                "types" : {
                    "root": {
                        "icon" : "glyphicon glyphicon-cloud"
                    },
                    "course": {
                        "icon" : "glyphicon glyphicon-folder-open"
                    },
                    "module": {
                        "icon" : "glyphicon glyphicon-list-alt"
                    },
                    "chapter": {
                        "icon" : "glyphicon glyphicon-book"
                    },
                    "lesson": {
                        "icon" : "glyphicon glyphicon-music"
                    },
                    "quiz": {
                        "icon" : "glyphicon glyphicon-question-sign"
                    }
                }
            });
            // selection changed
            $('#jstree').on("changed.jstree", function (e, data) {
                onChanged();
            });
            $('#jstree').jstree("refresh");
            $scope.jstree = $.jstree.reference('#jstree');
        }

        function onChanged() {
            $scope.children = []
            $scope.nodes = [];
            $scope.quizNodes = [];
            var selected = $scope.jstree.get_node($scope.jstree.get_selected());
            $scope.selected = selected;
            if (selected.children) {
                $.each(selected.children, function(idx, el) {
                    $scope.children.push($scope.jstree.get_node(el));
                });
                $scope.$digest();
            }
            if (selected.original && selected.original.type) {
                var type = selected.original.type;
                $("#removeMember").prop('disabled', false);
                $("#publishCourse").prop('disabled', true);
                if (type == "root") {
                    $("#removeMember").prop('disabled', true);
                } else if (type == "course") {
                    $scope.nodes = $scope.modules;
                    $scope.childIcon = $scope.jstree.settings.types.module.icon;
                    $scope.childType = "module";
                    $("#removeMember").prop('disabled', true);
                    if ($scope.isActive(selected)) {
                        $("#publishCourse").prop('disabled', false);
                    }
                } else if (type == "module") {
                    $scope.nodes = $scope.chapters;
                    $scope.childIcon = $scope.jstree.settings.types.chapter.icon;
                    $scope.childType = "chapter";
                } else if (type == "chapter") {
                    $scope.nodes = $scope.lessons;
                    $scope.childIcon = $scope.jstree.settings.types.lesson.icon;
                    $scope.childType = "lesson";
                    $scope.quizNodes = $scope.quizzes;
                }
                $scope.$digest();
            }
        }

        function fillJson(data, init, level, par) {
            //if initialization
            if (init) {
                jArray = new Array();
                iterator = 0;
                node_properties = new Array();
                jArray[iterator] = {
                    "id" : iterator,
                    "text" : "mTraining",
                    "parent" : par,
                    "state" : {
                            opened : true,
                            disabled : false,
                            selected : false
                        },
                    li_attr : {},
                    "level" : level,
                    "type" : "root"
                }
                par = 0;
                level++;
            }

            data.forEach(function(item) {
                var type = null;
                if (item.modules) {
                    type = "course";
                } else if (item.chapters) {
                    type = "module";
                } else if (item.lessons) {
                    type = "chapter";
                } else if (item.questions) {
                    type = "quiz";
                } else {
                    type = "lesson";
                }
                createNode(item.id, item.name, par, level, type, item.state);

                var child_table = item.modules || item.chapters || item.lessons || [];
                var quiz = item.quiz;
                if (quiz) {
                    child_table.push(quiz);
                }
                if (!(child_table === 0)) {
                    fillJson(child_table, false, level+1, iterator);
                };

            });
        }
    });

    controllers.controller('coursesController', ['$scope', 'Course', function ($scope, Course) {

        $scope.clearCourse = function() {
            $scope.creatingCourse = false;
            $scope.updatingCourse = false;
            $scope.savingCourse = false;
            $scope.createCourse();
        }

        $scope.$on('courseClick', function(event, courseId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.course = Course.get({ id: courseId });
            $scope.updatingCourse = true;
            $scope.creatingCourse = false;
        });

        $scope.createCourse = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingCourse = true;
            $scope.course = new Course();
            $scope.course.modules = [];
        }

        $scope.saveCourse = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingCourse = true;
            $scope.course.state = 'Inactive';
            $scope.course.$save(function(c) {
                // c => saved course object
                $scope.alertMessage = $scope.msg('mtraining.createdCourse');
                $("#coursesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearCourse();
        }

        $scope.updateCourse = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingCourse = true;
            $scope.course.$update({ id:$scope.course.id }, function (c) {
                // c => updated course object
                $scope.alertMessage = $scope.msg('mtraining.updatedCourse');
                $("#coursesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearCourse();
        }

        $scope.deleteCourse = function() {
            jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.course'), $scope.course.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                if (val) {
                    $scope.savingCourse = true;
                    $scope.course.$delete({ id:$scope.course.id }, function () {
                        $scope.alertMessage = $scope.msg('mtraining.deletedCourse');
                        $("#coursesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
                    });
                    $scope.clearCourse();
                }
            });
        }

        $scope.validate = function() {
            if (!$scope.course.name){
                $scope.alertMessage = undefined;
                $scope.errorName = $scope.msg('mtraining.field.required', $scope.msg('mtraining.courseName'));
                return false;
            }
            else {
                $scope.errorName = undefined;
                var data = $("#coursesListTable").jqGrid('getGridParam','data');
                data.every(function(row) {
                    if (row.name == $scope.course.name) {
                        if ($scope.creatingCourse || $scope.course.id != row.id) {
                            $scope.errorName = $scope.msg('mtraining.field.unique', $scope.msg('mtraining.courseName'));
                            return false;
                        }
                    }
                    return true;
                });
            }

            if (!$scope.errorName) {
                return true;
            }
            return false;
        }

        $scope.clearCourse();
    }]);

    controllers.controller('modulesController', ['$scope', 'Module', 'Course', function ($scope, Module, Course) {

        $scope.getCourses = function() {
            $scope.fetchingCourses = true;
            $.getJSON('../mtraining/web-api/courses', function(data) {
                $scope.courses = data;
                $scope.fetchingCourses = false;
                $scope.$apply();
                $("#courses").select2();
            });
        }

        $scope.clearModule = function() {
            $scope.creatingModule = false;
            $scope.updatingModule = false;
            $scope.savingModule = false;
            $scope.selectedCourses = [];
            $scope.createModule();
            $scope.getCourses();
        }

        $scope.$on('moduleClick', function(event, moduleId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.selectedCourses = [];
            $scope.updatingModule = true;
            $scope.creatingModule = false;
            $scope.module = Module.get({ id: moduleId }, function () {
                $.each($scope.courses, function(i, course) {
                    if ($.inArray(course.id, $scope.module.parentIds) != -1) {
                        $scope.selectedCourses.push("" + course.id);
                    }
                });
                $("#courses").select2('val', $scope.selectedCourses);
            });
        });

        $scope.createModule = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingModule = true;
            $scope.module = new Module();
            if ($scope.module.parentIds == undefined) {
                $scope.module.parentIds = [];
            }
        }

        $scope.saveModule = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingModule = true;
            $scope.module.state = 'Inactive';
            $scope.module.parentIds = $scope.selectedCourses;
            $scope.module.$save(function(m) {
                // m => saved module object
                $scope.alertMessage = $scope.msg('mtraining.createdModule');
                $("#modulesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearModule();
        }

        $scope.updateModule = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingModule = true;
            $scope.module.parentIds = $scope.selectedCourses;
            $scope.module.$update({ id:$scope.module.id }, function (m) {
                // m => updated module object
                $scope.alertMessage = $scope.msg('mtraining.updatedModule');
                $("#modulesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearModule();
        }

        $scope.deleteModule = function() {
            if ($scope.selectedCourses != undefined && $scope.selectedCourses.length != 0) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteModule'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.moduleWhp'), $scope.module.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingModule = true;
                        $scope.module.$delete({ id:$scope.module.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedModule');
                            $("#modulesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
                        });
                        $scope.clearModule();
                    }
                });
            }
        }

        $scope.validate = function() {
            if (!$scope.module.name){
                $scope.alertMessage = undefined;
                $scope.errorName = $scope.msg('mtraining.field.required', $scope.msg('mtraining.moduleName'));
                return false;
            }
            else {
                $scope.errorName = undefined;
                var data = $("#modulesListTable").jqGrid('getGridParam','data');
                data.every(function(row) {
                    if (row.name == $scope.module.name) {
                        if ($scope.creatingModule || $scope.module.id != row.id) {
                            $scope.errorName = $scope.msg('mtraining.field.unique', $scope.msg('mtraining.moduleName'));
                            return false;
                        }
                    }
                    return true;
                });
            }

            if (!$scope.errorName) {
                return true;
            }
            return false;
        }

        $scope.clearModule();
    }]);

    controllers.controller('chaptersController', ['$scope', 'Chapter', 'Module', 'Quiz', function ($scope, Chapter, Module, Quiz) {

        $scope.getModules = function() {
            $scope.fetchingModules = true;
            $.getJSON('../mtraining/web-api/modules', function(data) {
                $scope.modules = data;
                $scope.fetchingModules = false;
                $scope.$apply();
                $("#modules").select2();
            });
        }

        $scope.getQuizzes = function() {
            $scope.fetchingQuizzes = true;
            $.getJSON('../mtraining/web-api/quizzes', function(data) {
                $scope.quizzes = data;
                $scope.fetchingQuizzes = false;
                $scope.$apply();
                $("#quiz").select2({
                    allowClear: true,
                    placeholder: "Select a quiz"
                    });
            });
        }

        $scope.clearChapter = function() {
            $scope.creatingChapter = false;
            $scope.updatingChapter = false;
            $scope.savingChapter = false;
            $scope.selectedModules = [];
            $scope.selectedQuiz = undefined;
            $scope.createChapter();
            $scope.getModules();
            $scope.getQuizzes();
        }

        $scope.getQuizFromQuizzes = function () {
            var idx = $scope.selectedQuiz;
            if (!idx) {
                return;
            }
            var chapterId = $scope.chapter.id;
            var quiz = Quiz.get({ id: $scope.quizzes[idx].id }, function() {
                quiz.parentIds = [chapterId];
                quiz.$update({ id: quiz.id }, function() {
                    $scope.quizzes[idx] = quiz;
                });
            });
        }

        $scope.$on('chapterClick', function(event, chapterId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.selectedModules = [];
            $scope.chapter = Chapter.get({ id: chapterId }, function () {
                $.each($scope.modules, function(i, module) {
                    if ($.inArray(module.id, $scope.chapter.parentIds) != -1) {
                        $scope.selectedModules.push("" + module.id);
                    }
                })

                $scope.selectedQuiz = undefined;
                $.each($scope.quizzes, function(i, quiz) {
                    if ($.inArray($scope.chapter.id, quiz.parentIds) != -1) {
                        $scope.selectedQuiz = i;
                    }
                });

                $("#modules").select2('val', $scope.selectedModules);
                $("#quiz").select2('val', $scope.selectedQuiz);
            });
            $scope.updatingChapter = true;
            $scope.creatingChapter = false;
        });

        $scope.createChapter = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingChapter = true;
            $scope.chapter = new Chapter();
            $scope.selectedQuiz = undefined;
            if ($scope.chapter.parentIds == undefined) {
                $scope.chapter.parentIds = [];
            }
        }

        $scope.saveChapter = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingChapter = true;
            $scope.chapter.state = 'Inactive';
            $scope.chapter.parentIds = $scope.selectedModules;
            $scope.getQuizFromQuizzes();
            $scope.chapter.$save(function(c) {
                // c => saved chapter object
                $scope.alertMessage = $scope.msg('mtraining.createdChapter');
                $("#chaptersListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearChapter();
        }

        $scope.updateChapter = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingChapter = true;
            $scope.chapter.parentIds = $scope.selectedModules;
            $scope.getQuizFromQuizzes();
            $scope.chapter.$update({ id:$scope.chapter.id }, function (c) {
                // c => updated chapter object
                $scope.alertMessage = $scope.msg('mtraining.updatedChapter');
                $("#chaptersListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearChapter();
        }

        $scope.deleteChapter = function() {
            if ($scope.selectedModules != undefined && $scope.selectedModules.length != 0) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteChapter'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.chapter'), $scope.chapter.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingChapter = true;
                        $scope.chapter.$delete({ id:$scope.chapter.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedChapter');
                            $("#chaptersListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
                        });
                        $scope.clearChapter();
                    }
                });
            }
        }

        $scope.validate = function() {
            if (!$scope.chapter.name){
                $scope.alertMessage = undefined;
                $scope.errorName = $scope.msg('mtraining.field.required', $scope.msg('mtraining.chapterName'));
                return false;
            }
            else {
                $scope.errorName = undefined;
                var data = $("#chaptersListTable").jqGrid('getGridParam','data');
                data.every(function(row) {
                    if (row.name == $scope.chapter.name) {
                        if ($scope.creatingChapter || $scope.chapter.id != row.id) {
                            $scope.errorName = $scope.msg('mtraining.field.unique', $scope.msg('mtraining.chapterName'));
                            return false;
                        }
                    }
                    return true;
                });
            }

            if (!$scope.errorName) {
                return true;
            }
            return false;
        }

        $scope.clearChapter();
    }]);

    controllers.controller('messagesController', ['$scope', 'Lesson', 'Chapter', function ($scope, Lesson, Chapter) {

        $scope.getChapters = function() {
            $scope.fetchingChapters = true;
            $.getJSON('../mtraining/web-api/chapters', function(data) {
                $scope.chapters = data;
                $scope.fetchingChapters = false;
                $scope.$apply();
                $("#chapters").select2();
            });
        }

        $scope.clearMessage = function() {
            $scope.creatingMessage = false;
            $scope.updatingMessage = false;
            $scope.savingMessage = false;
            $scope.selectedChapters = [];
            $scope.createMessage();
            $scope.getChapters();
        }

        $scope.$on('messageClick', function(event, messageId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.selectedChapters = [];
            $scope.message = Lesson.get({ id: messageId }, function () {
                $.each($scope.chapters, function(i, chapter) {
                    if ($.inArray(chapter.id, $scope.message.parentIds) != -1) {
                        $scope.selectedChapters.push("" + chapter.id);
                    }
                })
                $("#chapters").select2('val', $scope.selectedChapters);
            });
            $scope.updatingMessage = true;
            $scope.creatingMessage = false;
        });

        $scope.createMessage = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingMessage = true;
            $scope.message = new Lesson();
            if ($scope.message.parentIds == undefined) {
                $scope.message.parentIds = [];
            }
        }

        $scope.saveMessage = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingMessage = true;
            $scope.message.state = 'Inactive';
            $scope.message.parentIds = $scope.selectedChapters;
            $scope.message.$save(function(m) {
                // m => saved message object
                $scope.alertMessage = $scope.msg('mtraining.createdMessage');
                $("#messagesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearMessage();
        }

        $scope.updateMessage = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingMessage = true;
            $scope.message.parentIds = $scope.selectedChapters;
            $scope.message.$update({ id:$scope.message.id }, function (m) {
                // m => updated message object
                $scope.alertMessage = $scope.msg('mtraining.updatedMessage');
                $("#messagesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearMessage();
        }

        $scope.deleteMessage = function() {
            if ($scope.selectedChapters != undefined && $scope.selectedChapters.length != 0) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteMessage'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.message'), $scope.message.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingMessage = true;
                        $scope.message.$delete({ id:$scope.message.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedMessage');
                            $("#messagesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
                        });
                        $scope.clearMessage();
                    }
                });
            }
        }

        $scope.validate = function() {
            if (!$scope.message.name){
                $scope.alertMessage = undefined;
                $scope.errorName = $scope.msg('mtraining.field.required', $scope.msg('mtraining.messageName'));
                return false;
            }
            else {
                $scope.errorName = undefined;
                var data = $("#messagesListTable").jqGrid('getGridParam','data');
                data.every(function(row) {
                    if (row.name == $scope.message.name) {
                        if ($scope.creatingMessage || $scope.message.id != row.id) {
                            $scope.errorName = $scope.msg('mtraining.field.unique', $scope.msg('mtraining.messageName'));
                            return false;
                        }
                    }
                    return true;
                });
            }

            if (!$scope.errorName) {
                return true;
            }
            return false;
        }

        $scope.clearMessage();
    }]);

    controllers.controller('quizzesController', ['$scope', 'Quiz', function ($scope, Quiz) {

        $scope.$on('quizClick', function(event, quizId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.errorPercentage = undefined;
            $scope.quiz = Quiz.get({ id: quizId });
            $scope.updatingQuiz = true;
            $scope.creatingQuiz = false;
            $scope.clearQuestion();
        });

        $scope.clearQuiz = function() {
            $scope.creatingQuiz = false;
            $scope.updatingQuiz = false;
            $scope.savingQuiz = false;
            $scope.questionIndex = -1;
            $scope.question = {};
            $("#passPercentage").val('');
            $scope.createQuiz();
        }

        $scope.clearQuestion = function() {
            $scope.question = {};
            $scope.questionIndex = -1;
            $(".valid-option").remove();
        }

        $scope.clearErrorSpans = function() {
            $scope.errorQuestion = undefined;
            $scope.errorOptions = undefined;
            $scope.errorAnswer = undefined;
            $scope.errorFilename = undefined;
            $scope.errorExplainingAnswerFilename = undefined;
        }

        $scope.questionClick = function(index) {
            $scope.questionIndex = index;
        }

        $scope.addQuestion = function() {
            if (!$scope.validateQuestion()) {
                return;
            }
            var question = {};
            $scope.rewriteQuestion(question);
            $scope.quiz.questions.push(question);
            $scope.clearQuestion();
            $('#questionModal').modal('hide');
        }

        $scope.updateQuestion = function() {
            if (!$scope.validateQuestion()) {
                return;
            }
            var question = {};
            $scope.rewriteQuestion(question);
            $scope.quiz.questions[$scope.questionIndex] = question;
            $scope.clearQuestion();
            $('#questionModal').modal('hide');
        }

        $scope.rewriteQuestion = function(question) {
            question.name = $scope.question.name;
            question.description = $scope.question.description;
            question.correctOption = $scope.question.correctOption;
            question.options = $scope.question.options;
            question.externalId = $scope.question.externalId;
            question.explainingAnswerFilename = $scope.question.explainingAnswerFilename;
        }

        $scope.deleteQuestion = function() {
            $scope.quiz.questions.splice($scope.questionIndex, 1);
            $scope.clearQuestion();
            $('#questionModal').modal('hide');
        }

        $scope.createQuiz = function() {
            $scope.errorName = undefined;
            $scope.errorPercentage = undefined;
            $scope.creatingQuiz = true;
            $scope.quiz = new Quiz();
            if ($scope.quiz.questions == undefined) {
                $scope.quiz.questions = [];
            }
            if ($scope.quiz.parentIds == undefined) {
                $scope.quiz.parentIds = [];
            }
        }

        $scope.saveQuiz = function() {
            $scope.alertMessage = undefined;
            if (!$scope.validate()){
                return;
            }
            $scope.savingQuiz = true;
            $scope.quiz.state = 'Inactive';
            $scope.quiz.$save(function(q) {
                // q => saved quiz object
                $scope.alertMessage = $scope.msg('mtraining.createdQuiz');
                $("#quizzesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearQuiz();
        }

        $scope.updateQuiz = function() {
            $scope.alertMessage = undefined;
            if (!$scope.validate()){
                return;
            }
            $scope.savingQuiz = true;
            $scope.quiz.$update({ id:$scope.quiz.id }, function (q) {
                // q => updated quiz object
                $scope.alertMessage = $scope.msg('mtraining.updatedQuiz');
                $("#quizzesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearQuiz();
        }

        $scope.deleteQuiz = function() {
            $scope.alertMessage = undefined;
            if ($scope.quiz.inRelation) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteQuiz'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.quiz'), $scope.quiz.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingQuiz = true;
                        $scope.quiz.$delete({ id:$scope.quiz.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedQuiz');
                            $("#quizzesListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
                        });
                        $scope.clearQuiz();
                    }
                });
            }
        }

        $scope.validate = function() {

            if (!$scope.quiz.name) {
                $scope.errorName = $scope.msg('mtraining.field.required', $scope.msg('mtraining.quizName'));
            }
            else {
                $scope.errorName = undefined;
            }

            if (!$scope.quiz.passPercentage && $scope.quiz.passPercentage != 0) {
                $scope.errorPercentage = $scope.msg('mtraining.set.percentage');
            }
            else {
                $scope.errorPercentage = undefined;
            }

            if (!$scope.errorName && !$scope.errorPercentage) {
                return true
            }
            $scope.alertMessage = undefined;
            return false;
        }

        $scope.validateQuestion = function() {
            if (!$scope.question.name) {
                $scope.errorQuestion = $scope.msg('mtraining.field.required', $scope.msg('mtraining.question'));
            }
            else {
                $scope.errorQuestion = undefined;
            }

            if ($scope.question.options.length == 0) {
                $scope.errorOptions = $scope.msg('mtraining.field.required', $scope.msg('mtraining.options'));
            }
            else {
                $scope.errorOptions = undefined;
            }

            if (!$scope.question.correctOption && $scope.question.correctOption !== 0) {
                $scope.errorAnswer = $scope.msg('mtraining.field.required', $scope.msg('mtraining.correctOption'));
            }
            else {
                $scope.errorAnswer = $scope.msg('mtraining.invalid.answer');
                if ($scope.question.options.length > 0) {
                    var correctOption = $scope.question.correctOption.toString();
                    for (var i=0; i < $scope.question.options.length; i++) {
                        if ($scope.question.options[i] == correctOption){
                            $scope.errorAnswer = undefined;
                            break;
                        }
                    }
                }
            }

            if (!$scope.question.externalId) {
                $scope.errorFilename = $scope.msg('mtraining.field.required', $scope.msg('mtraining.question.filename'));
            }
            else {
                $scope.errorFilename = undefined;
            }

            if (!$scope.question.explainingAnswerFilename) {
                $scope.errorExplainingAnswerFilename = $scope.msg('mtraining.field.required', $scope.msg('mtraining.explainingAnswerFilename'));
            }
            else {
                $scope.errorExplainingAnswerFilename = undefined;
            }

            if (!$scope.errorQuestion && !$scope.errorOptions && !$scope.errorAnswer && !$scope.errorFilename && !$scope.errorExplainingAnswerFilename) {
                return true
            }
            return false;
        }

        $scope.addOption = function (e) {
            if ($.inArray(e.charCode, [9, 27, 13]) !== -1) {
                 return;
            }
            if (e.charCode < 48 || e.charCode > 57 || $.inArray(String.fromCharCode(e.charCode), $scope.question.options) !== -1) {
                e.preventDefault();
            }
            else {
                var option = String.fromCharCode(e.charCode);
                $scope.question.options.push(option);
                $scope.createOption(option);
                e.preventDefault();
            }
        }

        $scope.createOption = function (option) {
            $(".options-input").before('<li class="valid-option" value="' + option + '"><div>' + option +
                '</div><a onclick="angular.element(\'#optionList\').scope().removeOption($(this));" class="question-close select2-search-choice-close"></a></li>');
        }

        $scope.removeOption = function (t) {
            var pos = $.inArray(t.parent().val().toString(), $scope.question.options);
            $scope.question.options.splice(pos, 1 );
            $(t).parent().remove();
        }

        $scope.addQuestionModal = function () {
            $scope.clearQuestion();
            $scope.clearErrorSpans();
            $scope.question.options = [];
            $('#questionModal').modal('show');
        }

        $scope.updateQuestionModal = function () {
            var index = $scope.questionIndex;
            $scope.question = {};
            $(".valid-option").remove();
            $scope.question.name = $scope.quiz.questions[index].name;
            $scope.question.description = $scope.quiz.questions[index].description;
            $scope.question.correctOption = parseInt($scope.quiz.questions[index].correctOption);
            $scope.question.options = $scope.quiz.questions[index].options;
            for (var i=0; i < $scope.question.options.length; i++) {
                $scope.createOption($scope.question.options[i]);
            }

            $scope.question.externalId = $scope.quiz.questions[index].externalId;
            $scope.question.explainingAnswerFilename = $scope.quiz.questions[index].explainingAnswerFilename;
            $scope.errorQuestion = undefined;
            $scope.clearErrorSpans();
            $('#questionModal').modal('show');
        }

        $scope.cancelUpdate = function () {
            $scope.clearErrorSpans();
            $('#questionModal').modal('hide');
        }

        $scope.clearQuiz();
    }]);

    controllers.controller('fileUploadController', function ($scope, fileUpload) {
        $scope.uploadedCourse = false;
        $scope.uploadedProvider = false;
        $scope.uploadingCourse = false;
        $scope.uploadingProvider = false;
        $scope.uploadingCourseConfig = false;
        $scope.response = undefined;

        $scope.uploadCourse = function () {
            $scope.uploadedCourse = true;
            $scope.uploadingCourse = true;
            upload("../mtraining/web-api/course-structure/import");
        };

        $scope.uploadProvider = function () {
            $scope.uploadingProvider = true;
            $scope.uploadedProvider = true;
            upload("../mtraining/web-api/provider/import");
        };

        $scope.uploadCourseConfig = function () {
            $scope.uploadedCourse = true;
            $scope.uploadingCourseConfig = true;
            upload("../mtraining/web-api/course-config/import");
        };

        var upload = function (uploadUrl) {
            fileUpload.uploadFileToUrl($scope.multipartFile, uploadUrl,
                function success(data) {
                    $scope.response = data;
                    clearFileName();
                }, function () {
                    clearFileName();
                });
        };

        var clearFileName = function () {
            $scope.uploadingCourse = false;
            $scope.uploadingProvider = false;
            $scope.uploadingCourseConfig = false;
            $scope.multipartFile = undefined;
            $scope.provider = undefined;
            angular.forEach(
                angular.element("input[type='file']"),
                function (inputElem) {
                    angular.element(inputElem).val(null);
                });
        };
    });

    controllers.controller('errorLogController', function ($scope, $http) {
         $http({method: 'GET', url: '../mtraining/web-api/errorLog'}).
            success(function(data, status, headers, config) {
              $scope.errorLog = data;
            });
    });

    controllers.controller('callLogController', function ($scope) {
        $.getJSON('../mtraining/web-api/callLogs', function(data) {
            $scope.callLogs = data;
        });
    });

    controllers.controller('coursePublicationController', function ($scope) {

    });

    controllers.controller('bookmarkRequestController', function ($scope) {

    });

    controllers.controller('providersController', ['$scope', 'Provider', function ($scope, Provider) {

        $scope.getLocations = function() {
            $scope.fetchingLocations = true;
            $.getJSON('../mtraining/web-api/locations', function(data) {
                $scope.locations = data;
                $scope.fetchingLocations = false;
                $scope.$apply();
                $("#location").select2({
                    allowClear: true,
                    placeholder: "Select a location"
                    });
            });
        }

        $scope.clearProvider = function() {
            $scope.creatingProvider = false;
            $scope.updatingProvider = false;
            $scope.savingProvider = false;
            $scope.selectedLocation = undefined;
            $scope.createProvider();
            $scope.getLocations();
        }

        $scope.getLocationFromLocations = function () {
            var idx = $scope.selectedLocation;
            $scope.provider.location = $scope.locations[idx];
        }

        $scope.$on('providerClick', function(event, providerId, remediId, callerId) {
            $scope.alertMessage = undefined;
            $scope.errorRemediId = undefined;
            $scope.errorCallerId = undefined;
            $scope.errorStatus = undefined;
            $scope.provider = Provider.get({ id: providerId }, function () {
                if ($scope.provider.location) {
                    var result = $.grep($scope.locations, function(e) {
                        return e.id == $scope.provider.location.id;
                    });
                    var idx = $scope.locations.indexOf(result[0]);
                    $scope.selectedLocation = idx;
                }
                else {
                    $scope.selectedLocation = undefined;
                }
                $("#location").select2('val', $scope.selectedLocation);
            });
            $scope.oldRemediId = remediId;
            $scope.oldCallerId = callerId;
            $scope.updatingProvider = true;
            $scope.creatingProvider = false;
        });

        $scope.createProvider = function() {
            $scope.alertMessage = undefined;
            $scope.errorRemediId = undefined;
            $scope.errorCallerId = undefined;
            $scope.errorStatus = undefined;
            $("#callerId").val('');
            $scope.creatingProvider = true;
            $scope.provider = new Provider();
        }

        $scope.saveProvider = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingProvider = true;
            $scope.getLocationFromLocations();
            $scope.provider.$save(function(c) {
                // c => saved provider object
                $scope.alertMessage = $scope.msg('mtraining.createdProvider');
                $("#providersListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearProvider();
        }

        $scope.updateProvider = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingProvider = true;
            $scope.getLocationFromLocations();
            $scope.provider.$update({ id:$scope.provider.id }, function (c) {
                // c => updated provider object
                $scope.alertMessage = $scope.msg('mtraining.updatedProvider');
                $scope.location = null;
                $("#providersListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearProvider();
        }

        $scope.deleteProvider = function() {
            jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.provider'), $scope.provider.remediId), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                if (val) {
                    $scope.savingProvider = true;
                    $scope.provider.$delete({ id:$scope.provider.id }, function () {
                        $scope.alertMessage = $scope.msg('mtraining.deletedProvider');
                        $("#providersListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
                    });
                    $scope.clearProvider();
                }
            });
        }

        $scope.validate = function() {
            var data = $("#providersListTable").jqGrid('getGridParam','data');
            if (!$scope.provider.remediId) {
                $scope.errorRemediId = $scope.msg('mtraining.field.required', $scope.msg('mtraining.remediId'));
            }
            else {
                $scope.errorRemediId = undefined;
                if ($scope.creatingProvider || $scope.provider.remediId != $scope.oldRemediId) {
                    data.every(function(row) {
                        if (row.remediId == $scope.provider.remediId) {
                            $scope.errorRemediId = $scope.msg('mtraining.field.unique', $scope.msg('mtraining.remediId'));
                            return false;
                        }
                        return true;
                    });
                }
            }

            if (!$scope.provider.callerId) {
                $scope.errorCallerId = $scope.msg('mtraining.field.required', $scope.msg('mtraining.callerId'));
            }
            else {
                $scope.errorCallerId = undefined;
                if ($scope.creatingProvider || $scope.provider.callerId != $scope.oldCallerId) {
                    data.every(function(row) {
                        if (row.callerId == $scope.provider.callerId) {
                            $scope.errorCallerId = $scope.msg('mtraining.field.unique', $scope.msg('mtraining.callerId'));
                            return false;
                        }
                        return true;
                    });
                }
            }

            if (!$scope.provider.providerStatus) {
                $scope.errorStatus = $scope.msg('mtraining.field.required', $scope.msg('mtraining.providerStatus'));
            }
            else {
                $scope.errorStatus = undefined;
            }

            if (!$scope.errorRemediId && !$scope.errorCallerId && !$scope.errorStatus) {
                return true;
            }
            $scope.alertMessage = undefined;
            return false;
        }

        $scope.clearProvider();
    }]);


    controllers.controller('locationsController', ['$scope', 'Location', function ($scope, Location) {

        $scope.clearLocation = function() {
            $scope.creatingLocation = false;
            $scope.updatingLocation = false;
            $scope.savingLocation = false;
            $scope.createLocation();
        }

        $scope.createLocation = function() {
            $scope.errorState = undefined;
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingLocation = true;
            $scope.location = new Location();
        }

        $scope.saveLocation = function() {
            if (!$scope.validate()) {
                return;
            }
            $scope.savingLocation = true;
            $scope.location.$save(function(c) {
                // c => saved location object
                $scope.alertMessage = $scope.msg('mtraining.createdLocation');
                $("#locationsListTable").setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
            $scope.clearLocation();
        }

        $scope.validate = function() {
            if (!$scope.location.state) {
                $scope.errorState = $scope.msg('mtraining.field.required', $scope.msg('mtraining.state'));
                return false;
            } else {
                return true;
            }
        }

        $scope.clearLocation();
    }]);

}());
