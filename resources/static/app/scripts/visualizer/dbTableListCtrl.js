var app;
(function (app) {
    var DbTableList;
    (function (DbTableList) {
        var DbTableListCtrl = (function () {
            function DbTableListCtrl(dataAccessService, $scope, $rootScope) {
                var _this = this;
                this.dataAccessService = dataAccessService;
                this.$scope = $scope;
                this.$scope.displayGraph = function(t) {
                    $rootScope.$emit("DisplayGraph", {test : t});
                }
                this.title = "Select Database";
                this.toggle = false;
                this.databases = [];
                var databaseResource = dataAccessService.getDatabaseResource();
                databaseResource.query(function (data) {
                    _this.databases = data;
                });
            }
            DbTableListCtrl.prototype.submit = function () {
                var _this = this;
                this.toggle = !this.toggle;
                $(".widget-content").slideToggle();
                $(".btn-block").text(function (i, v) {
                    return v === 'Show Relations' ? 'Select Tables' : 'Show Relations';
                });
                if (this.toggle) {
                    var url = "http://localhost:8080/user/getmappings/" + this.selectedDB.dataModelName + "/" + this.selectedTables[0];
                    var len = this.selectedTables.length;
                    if(len > 1){
                        for(var i=1;i<len;i++){
                            url = url + "," + this.selectedTables[i];
                        }
                    }  
                    var tableResource = this.dataAccessService
                            .getTableResource(url);
                        tableResource.get(function (data) {
                            _this.$scope.displayGraph(data);
                        });                 
                }
            };
            DbTableListCtrl.$inject = ["dataAccessService", "$scope","$rootScope"];
            return DbTableListCtrl;
        }());
        angular.module("OrientDBStudioApp").controller("DbTableListCtrl", DbTableListCtrl);
    })(DbTableList = app.DbTableList || (app.DbTableList = {}));
})(app || (app = {}));
