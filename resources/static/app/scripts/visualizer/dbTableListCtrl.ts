module app.DbTableList{
    import IScope = ng.IScope;

    export interface IGraphScope  extends ng.IScope
    {
        graph: OrientGraph.graph;
    }

    class DbTableListCtrl {
        databases: app.domain.IDatabase[];
        selectedDB: app.domain.IDatabase;
        title: string;
        selectedTables: string[];
        toggle: boolean;
        singleTable: app.domain.ITable;

        static $inject = ["dataAccessService","$scope"]
        constructor(private dataAccessService: app.common.DataAccessService, private $scope: IGraphScope) {
            this.title = "Select Database";
            this.toggle = false;
            this.databases = [];	

            var databaseResource = dataAccessService.getDatabaseResource();
            databaseResource.query((data: app.domain.IDatabase[]) => {
                this.databases = data;
            })
        }

        submit(): void{
            this.toggle = !this.toggle;
            $(".widget-content").slideToggle();
            $(".btn-block").text(function(i, v){
               return v === 'Show Relations' ? 'Select Tables' : 'Show Relations'
            })
            if(this.toggle){
                var tableResource = this.dataAccessService
                    .getTableResource("http://localhost:8080/user/gettable/" + this.selectedDB.databaseName + "/" + this.selectedTables[0]);
                tableResource.get((data: app.domain.ITable) => {
                    this.singleTable = data;});
                var temp = {
                            "edges": [
                                {
                                "@class": "Paths",
                                "@rid": "#1:1",
                                "in": "#Table1",
                                "out": "#Table2"
                                }
                            ],
                            "vertices": [
                                {
                                "@class": "Tables",
                                "@rid": "#Table1",
                                "name": "Table_1"
                                },
                                {
                                "@class": "Tables",
                                "@rid": "#Table2",
                                "name": "Table_2"
                                }
                            ]
                            } 
                this.$scope.graph.data(temp).redraw();
            }

        }
    }

    angular.module("OrientDBStudioApp").controller("DbTableListCtrl",DbTableListCtrl);
}