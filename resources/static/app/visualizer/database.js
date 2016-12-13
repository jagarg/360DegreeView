var app;
(function (app) {
    var domain;
    (function (domain) {
        var Database = (function () {
            function Database(databaseName, tables) {
                this.databaseName = databaseName;
                this.tables = tables;
            }
            return Database;
        }());
        domain.Database = Database;
    })(domain = app.domain || (app.domain = {}));
})(app || (app = {}));
