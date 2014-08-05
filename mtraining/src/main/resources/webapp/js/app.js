(function () {
    'use strict';

    var mtraining = angular.module('mtraining', ['motech-dashboard', 'mtraining.controllers', 'mtraining.directives',
        'ngRoute', 'ngResource', 'mtraining.services']);

    mtraining.config(function ($routeProvider) {
            $routeProvider.when('/mtraining/treeView', {
                    templateUrl: '../mtraining/partials/treeView.html',
                    controller: 'treeViewController'
                  }).when('/mtraining/schedules', {
                    templateUrl: '../mtraining/partials/schedules.html',
                    controller: 'schedulesController'
                  }).when('/mtraining/courses', {
                    templateUrl: '../mtraining/partials/courses.html',
                    controller: 'coursesController'
                  }).when('/mtraining/modules', {
                    templateUrl: '../mtraining/partials/modules.html',
                    controller: 'modulesController'
                  }).when('/mtraining/chapters', {
                    templateUrl: '../mtraining/partials/chapters.html',
                    controller: 'chaptersController'
                  }).when('/mtraining/messages', {
                    templateUrl: '../mtraining/partials/messages.html',
                    controller: 'messagesController'
                  }).when('/mtraining/upload', {
                    templateUrl: '../mtraining/partials/upload.html',
                    controller: 'fileUploadController'
                  });
    });

}());