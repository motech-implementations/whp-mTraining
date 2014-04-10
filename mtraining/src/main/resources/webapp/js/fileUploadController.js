var mtrainingModule = angular.module("mtraining");

mtrainingModule.controller('fileUploadController', ['$scope', 'fileUpload', function ($scope, fileUpload) {
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
}]);

