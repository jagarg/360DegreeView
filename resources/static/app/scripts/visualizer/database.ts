module app.domain{
    export interface IDatabase {
        databaseName: string;
        tables: string[];
    }

    export interface IColumn {
        columnName: string,
        foreignTable: string,
        foreignColumn: string,
        foreignKey: string,
        primaryKey: string
    }

    export interface ITable {
        tableName: string;
        columns: IColumn[];
    }

    export class Database implements IDatabase{

        constructor(public databaseName: string,
                    public tables: string[]){}
    } 
}