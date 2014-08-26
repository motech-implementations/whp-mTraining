(function () {
    'use strict';

    /* Services */

    var services = angular.module("mtraining.services", []);

    services.service('fileUpload', function ($http) {
        this.uploadFileToUrl = function (multipartFile, uploadUrl, successCallback, errorCallback) {
            var fd = new FormData();
            fd.append('multipartFile', multipartFile);
            $http.post(uploadUrl, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).success(function (data) {
                    successCallback(data)
                })
                .error(function (data) {
                    errorCallback(data)
                });
        }
    });

    services.factory('Course', ['$resource', function($resource) {
        return $resource('/motech-platform-server/module/mtraining/web-api/course/:id', null,
        {
           'update': { method:'PUT' }
        });
    }]);

    services.factory('Chapter', ['$resource', function($resource) {
        return $resource('/motech-platform-server/module/mtraining/web-api/chapter/:id', null,
        {
           'update': { method:'PUT' }
        });
     }]);

    services.factory('Lesson', ['$resource', function($resource) {
        return $resource('/motech-platform-server/module/mtraining/web-api/lesson/:id', null,
        {
           'update': { method:'PUT' }
        });
    }]);

    services.factory('Module', ['$resource', function($resource) {
        return $resource('/motech-platform-server/module/mtraining/web-api/module/:id', null,
        {
           'update': { method:'PUT' }
        });
    }]);

    services.factory('Quiz', ['$resource', function($resource) {
        return $resource('/motech-platform-server/module/mtraining/web-api/quiz/:id', null,
        {
           'update': { method:'PUT' }
        });
    }]);

    services.factory('Provider', ['$resource', function($resource) {
        return $resource('/motech-platform-server/module/mtraining/web-api/provider/:id', null,
        {
           'update': { method:'PUT' }
        });
    }]);

}());
