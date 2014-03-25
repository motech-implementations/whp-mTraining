var mtrainingModule = angular.module("mtraining");

mtrainingModule.controller('fileUploadController', ['$scope', 'fileUpload', function ($scope, fileUpload) {
    $scope.uploading = false;
    $scope.response = undefined;
    $scope.uploadCourse = function () {
        $scope.uploading = true;
        var uploadUrl = "../mtraining/web-api/course-structure/import";
        fileUpload.uploadFileToUrl($scope.multipartFile, uploadUrl,
            function success(data) {
                $scope.response = data;
                clearFileName();
            }, function () {
                clearFileName();
            });
    };

    $scope.uploadProvider = function () {
        $scope.uploading = true;
        var uploadUrl = "../mtraining/web-api/provider/import";
        fileUpload.uploadFileToUrl($scope.multipartFile, uploadUrl,
            function success(data) {
                $scope.response = data;
                clearFileName();
            }, function () {
                clearFileName();
            });
    };

    var clearFileName = function () {
        $scope.uploading = false;
        $scope.multipartFile = undefined;
        angular.forEach(
            angular.element("input[type='file']"),
            function (inputElem) {
                angular.element(inputElem).val(null);
            });
    };
}]);

