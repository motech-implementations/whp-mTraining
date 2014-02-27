/* put your angular services here */
 var mtrainingModule = angular.module("mtraining");
    mtrainingModule.service('$fileUpload', ['$http','$q', function ($http,$q) {
    this.uploadFileToUrl = function(multipartFile, uploadUrl){
        var fd = new FormData();
        fd.append('multipartFile', multipartFile);
        var deferred=$q.defer();
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
        .success(function(data){
             deferred.resolve(data);

        })
        .error(function(data){
              console.log("there is something wrong with csv");
        });
        return deferred.promise;
    }
}]);
