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
        }

        $scope.$on('courseClick', function(event, courseId) {
            $scope.alertMessage = undefined;
            $scope.course = Course.get({ id: courseId });
            $scope.updatingCourse = true;
            $scope.creatingCourse = false;
        });

        $scope.createCourse = function() {
            $scope.alertMessage = undefined;
            $scope.creatingCourse = true;
            $scope.course = new Course();
        }

        $scope.saveCourse = function() {
            $scope.savingCourse = true;
            $scope.course.state = 'Inactive';
            $scope.course.$save(function(c) {
                // c => saved course object
                $scope.alertMessage = $scope.msg('mtraining.createdCourse');
                $("#coursesListTable").trigger("reloadGrid");
            });
            $scope.clearCourse();
            $scope.createCourse();
        }

        $scope.updateCourse = function() {
            $scope.savingCourse = true;
            $scope.course.$update({ id:$scope.course.id }, function (c) {
                // c => updated course object
                $scope.alertMessage = $scope.msg('mtraining.updatedCourse');
                $("#coursesListTable").trigger("reloadGrid");
            });
            $scope.clearCourse();
            $scope.createCourse();
        }

        $scope.deleteCourse = function() {
            $scope.savingCourse = true;
            $scope.course.$delete({ id:$scope.course.id }, function () {
                $scope.clearCourse();
                $scope.alertMessage = $scope.msg('mtraining.deletedCourse');
                $("#coursesListTable").trigger("reloadGrid");
            });
            $scope.clearCourse();
            $scope.createCourse();
        }

        $scope.clearCourse();
        $scope.createCourse();
    }]);

    controllers.controller('modulesController', ['$scope', 'Module', 'Course', function ($scope, Module, Course) {

        $.getJSON('../mtraining/web-api/courses', function(data) {
            $scope.courses = data;
            $scope.$apply();
        });

        $scope.clearModule = function() {
            $scope.creatingModule = false;
            $scope.updatingModule = false;
            $scope.savingModule = false;
            $scope.selectedCourse = undefined;
            $scope.$apply();
        }

        $(document).on('click', '#courses li a', function () {
            var idx = $(this).attr("idx");
            $scope.selectedCourse = $scope.courses[idx];
            $scope.$apply();
        });


        $scope.$on('moduleClick', function(event, moduleId) {
            $scope.alertMessage = undefined;
            $scope.module = Module.get({ id: moduleId });
            $scope.updatingModule = true;
            $scope.creatingModule = false;
        });

        $scope.createModule = function() {
            $scope.alertMessage = undefined;
            $scope.creatingModule = true;
            $scope.module = new Module();
        }

        $scope.saveModule = function() {
            $scope.savingModule = true;
            $scope.module.state = 'Inactive';
                 $scope.module.$save(function(m) {
                // m => saved module object
                if ($scope.selectedCourse != undefined) {
                    Course.get({ id: $scope.selectedCourse.id }, function (course) {
                        if (course.courses == undefined) {
                            course.courses = [];
                        }
                        course.courses.push(m);
                        course.$update({ id: course.id }, function (c) {
                            // c => updated course object
                        });
                    });
                }
                $scope.alertMessage = $scope.msg('mtraining.createdModule');
                $("#modulesListTable").trigger("reloadGrid");
            });
            $scope.clearModule();
            $scope.createModule();
        }

        $scope.updateModule = function() {
            $scope.savingModule = true;
            $scope.module.$update({ id:$scope.module.id }, function (m) {
                // m => updated module object
                if ($scope.selectedCourse != undefined) {
                    Course.get({ id: $scope.selectedCourse.id }, function (course) {
                        if (course.courses == undefined) {
                            course.courses = [];
                        }
                        course.courses.push(m);
                        course.$update({ id: course.id }, function (c) {
                            // c => updated course object
                        });
                    });
                }
                $scope.alertMessage = $scope.msg('mtraining.updatedModule');
                $("#modulesListTable").trigger("reloadGrid");
            });
            $scope.clearModule();
            $scope.createModule();
        }

        $scope.deleteModule = function() {
            $scope.savingModule = true;
            $scope.module.$delete({ id:$scope.module.id }, function () {
                $scope.alertMessage = $scope.msg('mtraining.deletedModule');
                $("#modulesListTable").trigger("reloadGrid");
            });
            $scope.clearModule();
            $scope.createModule();
        }

        $scope.clearModule();
        $scope.createModule();
    }]);

    controllers.controller('chaptersController', ['$scope', 'Chapter', function ($scope, Chapter) {

        $scope.clearChapter = function() {
            $scope.creatingChapter = false;
            $scope.updatingChapter = false;
            $scope.savingChapter = false;
        }

        $scope.$on('chapterClick', function(event, chapterId) {
            $scope.alertMessage = undefined;
            $scope.chapter = Chapter.get({ id: chapterId });
            $scope.updatingChapter = true;
            $scope.creatingChapter = false;
        });

        $scope.createChapter = function() {
            $scope.alertMessage = undefined;
            $scope.creatingChapter = true;
            $scope.chapter = new Chapter();
        }

        $scope.saveChapter = function() {
            $scope.savingChapter = true;
            $scope.chapter.state = 'Inactive';
            $scope.chapter.$save(function(c) {
                // c => saved chapter object
                $scope.alertMessage = $scope.msg('mtraining.createdChapter');
                $("#chaptersListTable").trigger("reloadGrid");
            });
            $scope.clearChapter();
            $scope.createChapter();
        }

        $scope.updateChapter = function() {
            $scope.savingChapter = true;
            $scope.chapter.$update({ id:$scope.chapter.id }, function (c) {
                // c => updated chapter object
                $scope.alertMessage = $scope.msg('mtraining.updatedChapter');
                $("#chaptersListTable").trigger("reloadGrid");
            });
            $scope.clearChapter();
            $scope.createChapter();
        }

        $scope.deleteChapter = function() {
            $scope.savingChapter = true;
            $scope.chapter.$delete({ id:$scope.chapter.id }, function () {
                $scope.alertMessage = $scope.msg('mtraining.deletedChapter');
                $("#chaptersListTable").trigger("reloadGrid");
            });
            $scope.clearChapter();
            $scope.createChapter();
        }

        $scope.clearChapter();
        $scope.createChapter();
    }]);

    controllers.controller('messagesController', ['$scope', 'Lesson', function ($scope, Lesson) {

        $scope.clearMessage = function() {
            $scope.creatingMessage = false;
            $scope.updatingMessage = false;
            $scope.savingMessage = false;
        }

        $scope.$on('messageClick', function(event, messageId) {
            $scope.alertMessage = undefined;
            $scope.message = Lesson.get({ id: messageId });
            $scope.updatingMessage = true;
            $scope.creatingMessage = false;
        });

        $scope.createMessage = function() {
            $scope.alertMessage = undefined;
            $scope.creatingMessage = true;
            $scope.message = new Lesson();
        }

        $scope.saveMessage = function() {
            $scope.savingMessage = true;
            $scope.message.state = 'Inactive';
            $scope.message.$save(function(m) {
                // m => saved message object
                $scope.alertMessage = $scope.msg('mtraining.createdMessage');
                $("#messagesListTable").trigger("reloadGrid");
            });
            $scope.clearMessage();
            $scope.createMessage();
        }

        $scope.updateMessage = function() {
            $scope.savingMessage = true;
            $scope.message.$update({ id:$scope.message.id }, function (m) {
                // m => updated message object
                $scope.alertMessage = $scope.msg('mtraining.updatedMessage');
                $("#messagesListTable").trigger("reloadGrid");
            });
            $scope.clearMessage();
            $scope.createMessage();
        }

        $scope.deleteMessage = function() {
            $scope.savingMessage = true;
            $scope.message.$delete({ id:$scope.message.id }, function () {
                $scope.alertMessage = $scope.msg('mtraining.deletedMessage');
                $("#messagesListTable").trigger("reloadGrid");
            });
            $scope.clearMessage();
            $scope.createMessage();
        }

        $scope.clearMessage();
        $scope.createMessage();
    }]);

    controllers.controller('quizzesController', ['$scope', 'Quiz', function ($scope, Quiz) {

        $scope.$on('quizClick', function(event, quizId) {
            $scope.alertMessage = undefined;
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
        }

        $scope.questionClick = function(index) {
            $scope.questionIndex = index;
            $scope.question = {};
            $scope.question.question = $scope.quiz.questions[index].question;
            $scope.question.answer = $scope.quiz.questions[index].answer;
        }

        $scope.addQuestion = function() {
            var question = {};
            question.question = $scope.question.question;
            question.answer = $scope.question.answer;
            $scope.quiz.questions.push(question)
        }

        $scope.updateQuestion = function() {
            var question = {};
            question.question = $scope.question.question;
            question.answer = $scope.question.answer;
            $scope.quiz.questions[$scope.questionIndex] = question;
        }

        $scope.deleteQuestion = function() {
            var index = $scope.quiz.questions.indexOf($scope.questionIndex);
            $scope.quiz.questions.splice(index, 1);
        }

        $scope.createQuiz = function() {
            $scope.alertMessage = undefined;
            $scope.creatingQuiz = true;
            $scope.quiz = new Quiz();
            if ($scope.quiz.questions == undefined) {
                $scope.quiz.questions = [];
            }
        }

        $scope.saveQuiz = function() {
            $scope.savingQuiz = true;
            $scope.quiz.state = 'Inactive';
            $scope.quiz.$save(function(q) {
                // q => saved quiz object
                $scope.alertMessage = $scope.msg('mtraining.createdQuiz');
                $("#quizzesListTable").trigger("reloadGrid");
            });
            $scope.clearQuiz();
            $scope.createQuiz();
        }

        $scope.updateQuiz = function() {
            $scope.savingQuiz = true;
            $scope.quiz.$update({ id:$scope.quiz.id }, function (q) {
                // q => updated quiz object
                $scope.alertMessage = $scope.msg('mtraining.updatedQuiz');
                $("#quizzesListTable").trigger("reloadGrid");
            });
            $scope.clearQuiz();
            $scope.createQuiz();
        }

        $scope.deleteQuiz = function() {
            $scope.savingQuiz = true;
            $scope.quiz.$delete({ id:$scope.quiz.id }, function () {
                $scope.alertMessage = $scope.msg('mtraining.deletedQuiz');
                $("#quizzesListTable").trigger("reloadGrid");
            });
            $scope.clearQuiz();
            $scope.createQuiz();
        }

        $scope.clearQuiz();
        $scope.createQuiz();
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
