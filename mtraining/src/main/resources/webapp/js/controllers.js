(function () {
    'use strict';

    /* Controllers */

    var controllers = angular.module("mtraining.controllers", []);

    controllers.controller('treeViewController', function ($scope) {

    });

    controllers.controller('schedulesController', function ($scope) {

     });

    controllers.controller('coursesController', ['$scope', 'Course', function ($scope, Course) {
        $scope.creatingCourse = false;
        $scope.updatingCourse = false;

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
                $scope.savingCourse = false;
                $scope.creatingCourse = false;
                $scope.alertMessage = $scope.msg('mtraining.createdCourse');
                $("#coursesListTable").trigger("reloadGrid");
            });
        }

        $scope.updateCourse = function() {
            $scope.savingCourse = true;
            $scope.course.$update({ id:$scope.course.id }, function (c) {
                // c => updated course object
                $scope.savingCourse = false;
                $scope.updatingCourse = false;
                $scope.alertMessage = $scope.msg('mtraining.updatedCourse');
                $("#coursesListTable").trigger("reloadGrid");
            });
        }

        $scope.deleteCourse = function() {
            $scope.savingCourse = true;
            $scope.course.$delete({ id:$scope.course.id }, function () {
                $scope.savingCourse = false;
                $scope.updatingCourse = false;
                $scope.alertMessage = $scope.msg('mtraining.deletedCourse');
                $("#coursesListTable").trigger("reloadGrid");
            });
        }

        $scope.createCourse();
    }]);

    controllers.controller('modulesController', ['$scope', 'Module', function ($scope, Module) {
        $scope.creatingModule = false;
        $scope.updatingModule = false;

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
                $scope.savingModule = false;
                $scope.creatingModule = false;
                $scope.alertMessage = $scope.msg('mtraining.createdModule');
                $("#modulesListTable").trigger("reloadGrid");
            });
        }

        $scope.updateModule = function() {
            $scope.savingModule = true;
            $scope.module.$update({ id:$scope.module.id }, function (m) {
                // m => updated module object
                $scope.savingModule = false;
                $scope.updatingModule = false;
                $scope.alertMessage = $scope.msg('mtraining.updatedModule');
                $("#modulesListTable").trigger("reloadGrid");
            });
        }

        $scope.deleteModule = function() {
            $scope.savingModule = true;
            $scope.module.$delete({ id:$scope.module.id }, function () {
                $scope.savingModule = false;
                $scope.updatingModule = false;
                $scope.alertMessage = $scope.msg('mtraining.deletedModule');
                $("#modulesListTable").trigger("reloadGrid");
            });
        }

        $scope.createModule();
    }]);

    controllers.controller('chaptersController', ['$scope', 'Chapter', function ($scope, Chapter) {
        $scope.creatingChapter = false;
        $scope.updatingChapter = false;

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
                $scope.savingChapter = false;
                $scope.creatingChapter = false;
                $scope.alertMessage = $scope.msg('mtraining.createdChapter');
                $("#chaptersListTable").trigger("reloadGrid");
            });
        }

        $scope.updateChapter = function() {
            $scope.savingChapter = true;
            $scope.chapter.$update({ id:$scope.chapter.id }, function (c) {
                // c => updated chapter object
                $scope.savingChapter = false;
                $scope.updatingChapter = false;
                $scope.alertMessage = $scope.msg('mtraining.updatedChapter');
                $("#chaptersListTable").trigger("reloadGrid");
            });
        }

        $scope.deleteChapter = function() {
            $scope.savingChapter = true;
            $scope.chapter.$delete({ id:$scope.chapter.id }, function () {
                $scope.savingChapter = false;
                $scope.updatingChapter = false;
                $scope.alertMessage = $scope.msg('mtraining.deletedChapter');
                $("#chaptersListTable").trigger("reloadGrid");
            });
        }

        $scope.createChapter();
    }]);

    controllers.controller('messagesController', ['$scope', 'Lesson', function ($scope, Lesson) {
        $scope.creatingMessage = false;
        $scope.updatingMessage = false;

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
                $scope.savingMessage = false;
                $scope.creatingMessage = false;
                $scope.alertMessage = $scope.msg('mtraining.createdMessage');
                $("#messageListTable").trigger("reloadGrid");
            });
        }

        $scope.updateMessage = function() {
            $scope.savingMessage = true;
            $scope.message.$update({ id:$scope.message.id }, function (m) {
                // m => updated message object
                $scope.savingMessage = false;
                $scope.updatingMessage = false;
                $scope.alertMessage = $scope.msg('mtraining.updatedMessage');
                $("#messageListTable").trigger("reloadGrid");
            });
        }

        $scope.deleteMessage = function() {
            $scope.savingMessage = true;
            $scope.message.$delete({ id:$scope.message.id }, function () {
                $scope.savingMessage = false;
                $scope.updatingMessage = false;
                $scope.alertMessage = $scope.msg('mtraining.deletedMessage');
                $("#messageListTable").trigger("reloadGrid");
            });
        }

        $scope.createMessage();
    }]);

    controllers.controller('quizzesController', ['$scope', 'Quiz', function ($scope, Quiz) {
        $scope.creatingQuiz = false;
        $scope.updatingQuiz = false;
        $scope.question = {};
        $scope.questionIndex = -1;

        $scope.$on('quizClick', function(event, quizId) {
            $scope.alertMessage = undefined;
            $scope.quiz = Quiz.get({ id: quizId });
            $scope.updatingQuiz = true;
            $scope.creatingQuiz = false;
        });

        $scope.clearQuestion = function() {
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
            if ($scope.quiz.questions == undefined) {
                $scope.quiz.questions = [];
            }
            var question = {};
            question.question = $scope.question.question;
            question.answer = $scope.question.answer;
            $scope.quiz.questions.push(question)
            $scope.clearQuestion();
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
            $scope.clearQuestion();
        }

        $scope.createQuiz = function() {
            $scope.alertQuiz = undefined;
            $scope.creatingQuiz = true;
            $scope.quiz = new Quiz();
        }

        $scope.saveQuiz = function() {
            $scope.savingQuiz = true;
            $scope.quiz.state = 'Inactive';
            $scope.quiz.$save(function(q) {
                // q => saved quiz object
                $scope.savingQuiz = false;
                $scope.creatingQuiz = false;
                $scope.alertMessage = $scope.msg('mtraining.createdQuiz');
                $("#emailLoggingTable").trigger("reloadGrid");
            });
            $scope.clearQuestion();
        }

        $scope.updateQuiz = function() {
            $scope.savingQuiz = true;
            $scope.quiz.$update({ id:$scope.quiz.id }, function (q) {
                // q => updated quiz object
                $scope.savingQuiz = false;
                $scope.updatingQuiz = false;
                $scope.alertQuiz = $scope.msg('mtraining.updatedQuiz');
                $("#emailLoggingTable").trigger("reloadGrid");
            });
            $scope.clearQuestion();
        }

        $scope.deleteQuiz = function() {
            $scope.savingQuiz = true;
            $scope.quiz.$delete({ id:$scope.quiz.id }, function () {
                $scope.savingQuiz = false;
                $scope.updatingQuiz = false;
                $scope.alertQuiz = $scope.msg('mtraining.deletedQuiz');
                $("#emailLoggingTable").trigger("reloadGrid");
            });
            $scope.clearQuestion();
        }

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
