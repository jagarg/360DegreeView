var login = angular.module('login.controller', ['database.services']);
login.controller("LoginController", ['$scope', '$rootScope', '$routeParams', '$location', '$modal', '$q', 'Database', 'DatabaseApi', 'Notification', '$http', 'Spinner', 'localStorageService', 'DatabaseAlterApi', function ($scope, $rootScope, $routeParams, $location, $modal, $q, Database, DatabaseApi, Notification, $http, Spinner, localStorageService, DatabaseAlterApi) {

  $scope.server = "http://localhost:2480"

  var doc = "http://www.orientdb.com/docs/" + Database.getVersion() + "/Security.html"
  $scope.link = {link: doc};

  $scope.sso = false;
  DatabaseApi.isSSO().then(function (data) {
    $scope.sso = data.enabled;
  }).catch(function (err) {

  })
  $scope.databases = [];
  DatabaseApi.listDatabases(function (data) {
    $scope.databases = data.databases;
    if ($scope.databases.length > 0) {
      $scope.database = $scope.databases[0];
    }
  });

  $scope.connect = function (callback) {
    $scope.$broadcast("autofill:update");
    Database.connect($scope.database, $scope.username, $scope.password, function () {
      $location.path("/database/" + $scope.database + "/browse");
      Spinner.stopSpinner();
      if (callback) {
        callback();
      }
    }, function () {
      var noti = "Invalid username or password";
      Notification.push({content: noti, error: true});
      $scope.errorMsg = "Invalid username or password";
      Spinner.stopSpinner();
    });
  }
  $scope.createNew = function () {

    var modalScope = $scope.$new(true);
    modalScope.name = null;
    modalScope.creating = false;
    modalScope.stype = "plocal";
    modalScope.type = "graph";
    if (!$scope.sso) {
      modalScope.username = "root";
    }
    modalScope.lightweight = false;
    modalScope.sso = $scope.sso;
    modalScope.types = ['document', 'graph']
    modalScope.stypes = ['plocal', 'memory']
    var modalPromise = $modal({template: 'views/database/newDatabase.html', scope: modalScope, show: false});
    modalScope.createNew = function () {
      modalScope.creating = true;
      DatabaseApi.createDatabase(modalPromise.$scope.name, modalPromise.$scope.type, modalPromise.$scope.stype, modalPromise.$scope.username, modalPromise.$scope.password, function (data) {

        $scope.databases.push(modalPromise.$scope.name);
        $scope.database = modalPromise.$scope.name;

        modalScope.creating = false;
        modalPromise.hide();
        var noti = "Database " + modalPromise.$scope.name + " created.";
        $scope.username = modalPromise.$scope.username;
        $scope.password =  modalPromise.$scope.password;
        $scope.connect(function () {
          if (!modalPromise.$scope.lightweight) {
            DatabaseAlterApi.changeCustomProperty(modalPromise.$scope.name, {
              name: "useLightweightEdges",
              value: false
            });
          }
        });
        Notification.push({content: noti});
      }, function (data) {
        modalScope.creating = false;
        modalScope.error = data;
      });
    }

    modalPromise.$promise.then(modalPromise.show);
  }
  $scope.importPublic = function () {


    $.ajax({
      type: "GET",
      url: "http://www.orientechnologies.com/public-databases/config.json",
      crossDomain: true
    }).done(handleResponse).fail(function () {
      var noti = "An error occurred when trying to reach public databases repository. Please check your connection.";
      Notification.push({content: noti, error: true});
    });


    function handleResponse(data) {
      var modalScope = $scope.$new(true);

      modalScope.databases = data;
      Object.keys(data).forEach(function (k) {
        data[k].url = data[k].versions['02.00.00'].url;
      })
      modalScope.import = function (k, v, u, p) {
        modalScope.name = k;
        modalScope.creating = true;
        DatabaseApi.install(v, u, p).then(function (data) {
          var noti = "Database " + k + " imported.";
          $scope.databases.push(k);
          $scope.database = k;
          Notification.push({content: noti});
          modalPromise.hide();
        }, function err(data) {
          modalScope.creating = false;
          modalScope.error = data;
        })
      }
      var modalPromise = $modal({template: 'views/database/importCloud.html', scope: modalScope, show: false});
      modalPromise.$promise.then(modalPromise.show);
    }

  }

  $rootScope.$broadcast("request:logout");
  $scope.deleteDb = function () {
    var modalScope = $scope.$new(true);
    modalScope.name = $scope.database;
    modalScope.sso = $scope.sso;
    var modalPromise = $modal({template: 'views/database/deleteDatabase.html', scope: modalScope, show: false});
    modalScope.delete = function () {
      modalScope.creating = true;
      DatabaseApi.deleteDatabase(modalPromise.$scope.name, modalPromise.$scope.username, modalPromise.$scope.password).then(function (data) {
        var noti = "Database " + modalPromise.$scope.name + " dropped.";
        var idx = $scope.databases.indexOf(modalPromise.$scope.name);
        $scope.databases.splice(idx, 1);
        if ($scope.databases.length > 0) {
          $scope.database = $scope.databases[0];
        }
        var timeline = localStorageService.get("Timeline");
        if (timeline) {
          delete timeline[modalPromise.$scope.name];
          localStorageService.add("Timeline", timeline);
        }
        Notification.push({content: noti});
        modalPromise.hide();
      }, function (data) {
        modalScope.creating = false;
        modalScope.error = data;
      })
    }

    modalPromise.$promise.then(modalPromise.show);
  }
}]);
