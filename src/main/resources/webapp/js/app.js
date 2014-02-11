'use strict';

/* put your routes here */

angular.module('motech-mtraining', ['motech-dashboard', 'YourModuleServices', 'ngCookies', 'bootstrap'])
    .config(['$routeProvider', function ($routeProvider) {

        $routeProvider
            .when('/welcome', { templateUrl: '../motech-mtraining/resources/partials/welcome.html', controller: YourController })
            .otherwise({redirectTo: '/welcome'});
    }]);
