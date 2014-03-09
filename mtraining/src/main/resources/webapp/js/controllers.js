 var mtrainingModule = angular.module("mtraining");

    mtrainingModule.controller("mTrainingController", function ($scope) {
        $scope.message = "WHP M-Training Bundle";
    });

        mtrainingModule.controller('fileUploadController', ['$scope', '$fileUpload', function($scope, $fileUpload){
            $scope.response=null;
            $scope.uploadFile = function(){
                var multipartFile = $scope.multipartFile;
                console.log('multipartFile is ' + JSON.stringify(multipartFile));
                var uploadUrl = "../mtraining/web-api/course-structure/import";

                $fileUpload.uploadFileToUrl(multipartFile, uploadUrl).then(function(data){
                        $scope.response=data;
                        }, function(error){
                        console.log("there was an error getting new cool data");
                });

            };

        }]);





