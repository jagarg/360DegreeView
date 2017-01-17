var app;
(function (app) {
    var common;
    (function (common) {
        var DataAccessService = (function () {
            function DataAccessService($resource) {
                this.$resource = $resource;
            }
            DataAccessService.prototype.getDatabaseResource = function () {
                return this.$resource(ConfigOptions.baseURL + "user/listConfig");
            };
            DataAccessService.prototype.getTableResource = function (url) {
                return this.$resource(url);
            };
            DataAccessService.prototype.getTableMappings = function (url) {
                return this.$resource(url);
            };
            DataAccessService.$inject = ["$resource"];
            return DataAccessService;
        }());
        common.DataAccessService = DataAccessService;
        angular.module("common.services").service("dataAccessService", DataAccessService);
    })(common = app.common || (app.common = {}));
})(app || (app = {}));
