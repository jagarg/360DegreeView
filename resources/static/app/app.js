var app;
(function (app) {
    var main = angular.module("dbVisualizer", [
        "common.services",
        "ngRoute",
        "graph",
        "graph.services",
        "vertex.controller",
        "frapontillo.bootstrap-duallistbox"
    ]);
    main.config(routeConfig);
    routeConfig.$inject = ["$routeProvider"];
    function routeConfig($routeProvide) {
        $routeProvide
            .when("/", {
            templateUrl: "/app/database/graph.html",
            controller: "GraphController"
        })
            .otherwise("/");
    }
})(app || (app = {}));
