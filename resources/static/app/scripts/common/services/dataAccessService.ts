module app.common {
    interface IDataAccessService{
        getDatabaseResource(): ng.resource.IResourceClass<IDatabaseResource>;
    }

    interface IDatabaseResource 
        extends ng.resource.IResource<app.domain.IDatabase>{
    }

    interface ITableResource 
        extends ng.resource.IResource<app.domain.ITable>{
    }

    export class DataAccessService 
        implements IDataAccessService {

            static $inject = ["$resource"];
            constructor(private $resource: ng.resource.IResourceService){

            }

            getDatabaseResource(): ng.resource.IResourceClass<IDatabaseResource> {
                return this.$resource("http://localhost:8080/user/listdb");
            }

            getTableResource(url: string): ng.resource.IResourceClass<IDatabaseResource> {
                return this.$resource(url);
            }

            getTableMappings(url: string): ng.resource.IResourceClass<IDatabaseResource> {
                return this.$resource(url);
            }
        }

    angular.module("common.services").service("dataAccessService",DataAccessService);
}