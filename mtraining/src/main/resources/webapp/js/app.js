(function () {
    'use strict';

    if(!$('#chartJs').length) {
        var s = document.createElement("script");
        s.id = "chartJs";
        s.type = "text/javascript";
        s.src = "../mtraining/js/Chart.js";
        $("head").append(s);
    };

    if(!$('#angularChartJs').length) {
        var s = document.createElement("script");
        s.id = "angularChartJs";
        s.type = "text/javascript";
        s.src = "../mtraining/js/angular-chart.js";
        $("head").append(s);
    };

    var mtraining = angular.module('mtraining', ['motech-dashboard', 'mtraining.controllers', 'mtraining.directives' ,'mtraining.treeviewlib',
        'ngRoute', 'ngResource', 'mtraining.services', 'chart.js']);

    mtraining.config(function ($routeProvider) {
        $routeProvider.when('/mtraining/treeView', {
                templateUrl: '../mtraining/partials/treeView.html',
                controller: 'treeViewController'
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
              }).when('/mtraining/quizzes', {
                templateUrl: '../mtraining/partials/quizzes.html',
                controller: 'quizzesController'
              }).when('/mtraining/upload', {
                templateUrl: '../mtraining/partials/upload.html',
                controller: 'fileUploadController'
              }).when('/mtraining/errorLog', {
                templateUrl: '../mtraining/partials/errorLog.html',
                controller: 'errorLogController'
              }).when('/mtraining/callLog', {
                templateUrl: '../mtraining/partials/callLog.html',
              }).when('/mtraining/coursePublication', {
                templateUrl: '../mtraining/partials/coursePublication.html',
              }).when('/mtraining/bookmarkRequest', {
                templateUrl: '../mtraining/partials/bookmarkRequest.html',
              }).when('/mtraining/providers', {
                templateUrl: '../mtraining/partials/providers.html',
                controller: 'providersController'
              }).when('/mtraining/locations', {
                templateUrl: '../mtraining/partials/locations.html',
                controller: 'locationsController'
              }).when('/mtraining/quizAttempt', {
                templateUrl: '../mtraining/partials/quizAttempt.html'
              }).when('/mtraining/trainingStatusReport', {
                templateUrl: '../mtraining/partials/trainingStatusReport.html',
                controller: 'reportsController'
              }).when('/mtraining/wiseStatusReport', {
                templateUrl: '../mtraining/partials/wiseStatusReport.html',
                controller: 'reportsController'
              }).when('/mtraining/statusDetailedReport', {
                templateUrl: '../mtraining/partials/statusDetailedReport.html',
                controller: 'reportsController'
              });
    });

}());
