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
                $("#emailLoggingTable").trigger("reloadGrid");
            });
        }

        $scope.updateCourse = function() {
            $scope.savingCourse = true;
            $scope.course.$update({ id:$scope.course.id }, function (c) {
                // c => updated course object
                $scope.savingCourse = false;
                $scope.updatingCourse = false;
                $scope.alertMessage = $scope.msg('mtraining.updatedCourse');
                $("#emailLoggingTable").trigger("reloadGrid");
            });
        }

        $scope.deleteCourse = function() {
            $scope.savingCourse = true;
            $scope.course.$delete({ id:$scope.course.id }, function () {
                $scope.savingCourse = false;
                $scope.updatingCourse = false;
                $scope.alertMessage = $scope.msg('mtraining.deletedCourse');
                $("#emailLoggingTable").trigger("reloadGrid");
            });
        }

    }]);

    controllers.controller('modulesController', ['$scope', 'Module', function ($scope, Module) {

    }]);

    controllers.controller('chaptersController', ['$scope', 'Chapter', function ($scope, Chapter) {

    }]);

    controllers.controller('messagesController', ['$scope', 'Lesson', function ($scope, Lesson) {

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
