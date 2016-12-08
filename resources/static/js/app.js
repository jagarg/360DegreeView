var app =  angular.module('app', ['ngRoute','ngMaterial']);

app.config(function($routeProvider) {
    $routeProvider

        // route for the home page
        .when('/', {
            templateUrl : 'views/view.html',
            controller  : 'mainController'
        })

        .when('/view', {
            templateUrl : 'views/view.html',
            controller  : 'ViewController'
        })
        
        // route for the about page
        .when('/manage', {
            templateUrl : 'views/manage.html',
            controller  : 'ManageController'
        })

        // route for the contact page
        .when('/setting', {
            templateUrl : 'views/setting.html',
            controller  : 'mainController'
        });
});

app.controller('mainController', function($scope) {


});
