(function () {
    'use strict';

    /* Controllers */

    var controllers = angular.module("mtraining.controllers", []);

    controllers.controller('treeViewController', function ($scope) {

    });

    controllers.controller('schedulesController', function ($scope) {

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
            $scope.course.courses = [];
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
                $("#coursesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
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
                $("#coursesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearCourse();
        }

        $scope.deleteCourse = function() {
            jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.course'), $scope.course.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                if (val) {
                    $scope.savingCourse = true;
                    $scope.course.$delete({ id:$scope.course.id }, function () {
                        $scope.alertMessage = $scope.msg('mtraining.deletedCourse');
                        $("#coursesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
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
            return true;
        }

        $scope.clearCourse();
    }]);

    controllers.controller('modulesController', ['$scope', 'Module', 'Course', function ($scope, Module, Course) {

        $scope.getCourses = function() {
            $.getJSON('../mtraining/web-api/courses', function(data) {
                $scope.courses = data;
                $scope.$apply();
            });
        }

        $scope.clearModule = function() {
            $scope.creatingModule = false;
            $scope.updatingModule = false;
            $scope.savingModule = false;
            $scope.selectedCourse = undefined;
            $scope.createModule();
            $scope.getCourses();
        }

        $(document).on('click', '#courses li a', function () {
            var idx = $(this).attr("idx");
            $scope.selectedCourse = $scope.courses[idx];
            $scope.$apply();
        });

        $scope.$on('moduleClick', function(event, moduleId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.updatingModule = true;
            $scope.creatingModule = false;
            $scope.module = Module.get({ id: moduleId }, function () {
                $.each($scope.courses, function(i, course) {
                    $.each(course.courses, function(j, module) {
                        if(module.id == $scope.module.id) {
                            $scope.selectedCourse = course;
                            return false;
                        }
                    });
                });
            });
        });

        $scope.createModule = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingModule = true;
            $scope.module = new Module();
        }

        $scope.saveModule = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingModule = true;
            $scope.module.state = 'Inactive';
             if ($scope.selectedCourse != undefined) {
                 Course.get({ id: $scope.selectedCourse.id }, function (course) {
                     if (course.courses == undefined) {
                         course.courses = [];
                     }
                     course.courses.push($scope.module);
                     course.$update({ id: course.id }, function (c) {
                         // c => updated course object
                         $scope.clearModule();
                         $scope.alertMessage = $scope.msg('mtraining.createdModule');
                     });
                 });
             } else {
                 $scope.module.$save(function(m) {
                     // m => updated module object
                     $scope.clearModule();
                     $scope.alertMessage = $scope.msg('mtraining.createdModule');
                 });
             }
             $("#modulesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
        }

        $scope.updateModule = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingModule = true;
            if ($scope.selectedCourse != undefined) {
                Course.get({ id: $scope.selectedCourse.id }, function (course) {
                    if (course.courses == undefined) {
                        course.courses = [];
                    }
                    var hasParent = false;
                    $.each(course.courses, function(j, module) {
                        if (module.id == $scope.module.id) {
                            hasParent = true;
                            module = $scope.module;
                            return false;
                        }
                    });
                    if (!hasParent) {
                        course.courses.push($scope.module);
                    }
                    course.$update({ id: course.id }, function (c) {
                        // c => updated course object
                        $scope.clearModule();
                        $scope.alertMessage = $scope.msg('mtraining.updatedModule');
                    });
                });
            } else {
                $scope.module.$update({ id:$scope.module.id }, function (m) {
                    // m => updated module object
                    $scope.clearModule();
                    $scope.alertMessage = $scope.msg('mtraining.updatedModule');
                });
            }
            $("#modulesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
        }

        $scope.deleteModule = function() {
            if ($scope.selectedCourse != undefined) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteModule'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.moduleWhp'), $scope.module.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingModule = true;
                        $scope.module.$delete({ id:$scope.module.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedModule');
                            $("#modulesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
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
            return true;
        }

        $scope.clearModule();
    }]);

    controllers.controller('chaptersController', ['$scope', 'Chapter', function ($scope, Chapter) {

        $scope.clearChapter = function() {
            $scope.creatingChapter = false;
            $scope.updatingChapter = false;
            $scope.savingChapter = false;
            $scope.createChapter();
        }

        $scope.$on('chapterClick', function(event, chapterId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.chapter = Chapter.get({ id: chapterId });
            $scope.updatingChapter = true;
            $scope.creatingChapter = false;
        });

        $scope.createChapter = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingChapter = true;
            $scope.chapter = new Chapter();
        }

        $scope.saveChapter = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingChapter = true;
            $scope.chapter.state = 'Inactive';
            $scope.chapter.$save(function(c) {
                // c => saved chapter object
                $scope.alertMessage = $scope.msg('mtraining.createdChapter');
                $("#chaptersListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearChapter();
        }

        $scope.updateChapter = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingChapter = true;
            $scope.chapter.$update({ id:$scope.chapter.id }, function (c) {
                // c => updated chapter object
                $scope.alertMessage = $scope.msg('mtraining.updatedChapter');
                $("#chaptersListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearChapter();
        }

        $scope.deleteChapter = function() {
            if ($scope.selectedModule != undefined) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteChapter'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.chapter'), $scope.chapter.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingChapter = true;
                        $scope.chapter.$delete({ id:$scope.chapter.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedChapter');
                            $("#chaptersListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
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
            return true;
        }

        $scope.clearChapter();
    }]);

    controllers.controller('messagesController', ['$scope', 'Lesson', function ($scope, Lesson) {

        $scope.clearMessage = function() {
            $scope.creatingMessage = false;
            $scope.updatingMessage = false;
            $scope.savingMessage = false;
            $scope.createMessage();
        }

        $scope.$on('messageClick', function(event, messageId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.message = Lesson.get({ id: messageId });
            $scope.updatingMessage = true;
            $scope.creatingMessage = false;
        });

        $scope.createMessage = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingMessage = true;
            $scope.message = new Lesson();
        }

        $scope.saveMessage = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingMessage = true;
            $scope.message.state = 'Inactive';
            $scope.message.$save(function(m) {
                // m => saved message object
                $scope.alertMessage = $scope.msg('mtraining.createdMessage');
                $("#messagesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearMessage();
        }

        $scope.updateMessage = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingMessage = true;
            $scope.message.$update({ id:$scope.message.id }, function (m) {
                // m => updated message object
                $scope.alertMessage = $scope.msg('mtraining.updatedMessage');
                $("#messagesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearMessage();
        }

        $scope.deleteMessage = function() {
            if ($scope.selectedChapter != undefined) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteMessage'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.message'), $scope.message.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingMessage = true;
                        $scope.message.$delete({ id:$scope.message.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedMessage');
                            $("#messagesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
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
            return true;
        }

        $scope.clearMessage();
    }]);

    controllers.controller('quizzesController', ['$scope', 'Quiz', function ($scope, Quiz) {

        $scope.$on('quizClick', function(event, quizId) {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.quiz = Quiz.get({ id: quizId });
            $scope.updatingQuiz = true;
            $scope.creatingQuiz = false;
        });

        $scope.clearQuiz = function() {
            $scope.creatingQuiz = false;
            $scope.updatingQuiz = false;
            $scope.savingQuiz = false;
            $scope.questionIndex = -1;
            $scope.question = {};
            $scope.createQuiz();
        }

        $scope.clearQuestion = function() {
            $scope.question = {};
            $scope.questionIndex = -1;
        }

        $scope.questionClick = function(index) {
            $scope.questionIndex = index;
            $scope.question = {};
            $scope.question.name = $scope.quiz.questions[index].name;
            $scope.question.correctAnswer = $scope.quiz.questions[index].correctAnswer;
        }

        $scope.addQuestion = function() {
            if (!$scope.validateQuestion()) {
                return;
            }
            var question = {};
            question.name = $scope.question.name;
            question.correctAnswer = $scope.question.correctAnswer;
            $scope.quiz.questions.push(question);
            $scope.clearQuestion();
        }

        $scope.updateQuestion = function() {
            if (!$scope.validateQuestion()) {
                return;
            }
            var question = {};
            question.name = $scope.question.name;
            question.correctAnswer = $scope.question.correctAnswer;
            $scope.quiz.questions[$scope.questionIndex] = question;
            $scope.clearQuestion();
        }

        $scope.deleteQuestion = function() {
            $scope.quiz.questions.splice($scope.questionIndex, 1);
            $scope.clearQuestion();
        }

        $scope.createQuiz = function() {
            $scope.alertMessage = undefined;
            $scope.errorName = undefined;
            $scope.creatingQuiz = true;
            $scope.quiz = new Quiz();
            if ($scope.quiz.questions == undefined) {
                $scope.quiz.questions = [];
            }
        }

        $scope.saveQuiz = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingQuiz = true;
            $scope.quiz.state = 'Inactive';
            $scope.quiz.$save(function(q) {
                // q => saved quiz object
                $scope.alertMessage = $scope.msg('mtraining.createdQuiz');
                $("#quizzesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearQuiz();
        }

        $scope.updateQuiz = function() {
            if (!$scope.validate()){
                return;
            }
            $scope.savingQuiz = true;
            $scope.quiz.$update({ id:$scope.quiz.id }, function (q) {
                // q => updated quiz object
                $scope.alertMessage = $scope.msg('mtraining.updatedQuiz');
                $("#quizzesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
            });
            $scope.clearQuiz();
        }

        $scope.deleteQuiz = function() {
            if ($scope.selectedChapter != undefined) {
                $("#errorMessage").text($scope.msg('mtraining.cannotDeleteQuiz'));
                $("#errorDialog").modal('show');
            } else {
                jConfirm($scope.msg('mtraining.confirm.remove', $scope.msg('mtraining.quiz'), $scope.quiz.name), $scope.msg('mtraining.confirm.remove.header'), function (val) {
                    if (val) {
                        $scope.savingQuiz = true;
                        $scope.quiz.$delete({ id:$scope.quiz.id }, function () {
                            $scope.alertMessage = $scope.msg('mtraining.deletedQuiz');
                            $("#quizzesListTable").setGridParam({datatype:'json'}).trigger('reloadGrid');
                        });
                        $scope.clearQuiz();
                    }
                });
            }
        }

        $scope.validate = function() {
            $scope.errorQuestion = undefined;
            $scope.errorAnswer = undefined;
            if (!$scope.quiz.name) {
                $scope.alertMessage = undefined;
                $scope.errorName = $scope.msg('mtraining.field.required', $scope.msg('mtraining.quizName'));
                return false;
            }
            return true;
        }

        $scope.validateQuestion = function() {
            var toReturn = true;
            if (!$scope.question.name) {
                $scope.errorQuestion = $scope.msg('mtraining.field.required', $scope.msg('mtraining.question'));
                toReturn = false;
            }
            else {
                $scope.errorQuestion = undefined;
            }
            if (!$scope.question.correctAnswer) {
                $scope.errorAnswer = $scope.msg('mtraining.field.required', $scope.msg('mtraining.answer'));
                toReturn = false;
            }
            else {
                $scope.errorAnswer = undefined;
            }
            return toReturn;
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

}());
