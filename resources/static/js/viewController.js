var configList = "http://localhost:8080/config/list";
Configuration = [];

app.config(function($mdThemingProvider) {

    // Configure a dark theme with primary foreground yellow

    $mdThemingProvider.theme('docs-dark', 'default')
      .primaryPalette('yellow')
      .dark();

  });

app.controller('ViewController', function($scope,$http,$timeout) {

	restGET(configList);
	
    function restGET(url) 
    {
        $http({
            method : 'GET',
            url : url
        }).then(function successCallback(response) {
            console.log(response);
            $scope.Configuration = response.data;
            Configuration = response.data;
        }, function errorCallback(response) {
            console.log(response.statusText);
        }).finally(function() {
        // called no matter success or failure
            return response;
      });
    } 
});