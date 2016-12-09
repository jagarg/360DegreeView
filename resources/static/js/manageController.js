app.config(function($mdThemingProvider) {

    // Configure a dark theme with primary foreground yellow

    $mdThemingProvider.theme('docs-dark', 'default')
      .primaryPalette('yellow')
      .dark();

  });

app.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
                scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

app.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function(fd, uploadUrl){
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}//'multipart/form-data;boundary=gc0p4Jq0M2Yt08jU534c0p'}
        })
        .success(function(response){
        	console.log(response);
        })
        .error(function(response){
        	console.log(response);
        }).finally(function() {
        // called no matter success or failure
            return response;
      });
    }
}]);

app.controller('ManageController', function($scope,$http,$timeout,fileUpload,$sce,$rootScope) {

	$scope.welcomeNext=true;
	$scope.databaseNext=true;
	$scope.disabledatabase=true;
	//$scope.disableschema=true;
	$scope.disableschemadetails=true;
	$scope.disablecomplete=true;
	$scope.schemaOptions = ['JDO','HIBERNATE','ORACLE','MYSQL','CUSTOM JAR'];
	
	$scope.addConfig=false;		// 1
	$scope.viewConfig=true;	// 0
	$scope.showSucess = false;
	$scope.showError = false;
	
	function displayError(msg)
	{
		$scope.showError = true;
		$scope.errorMsg = $sce.trustAsHtml(msg);
	}
	
	function displaySuccess(msg) 
	{
		$scope.showSucess = true;
		$scope.successMsg = $sce.trustAsHtml(msg);
	}
	
	function clearAll() 
	{
		$scope.disablecomplete=true;
		$scope.summary=$sce.trustAsHtml("");
		$scope.newConfigName = "";
		$scope.dbName = "";
		$scope.dbuser ="";
		$scope.dbpasswd="";
		$scope.jdoFile="";
		$scope.jarFile="";
		$scope.schemaSelect="";
	}
	
	$scope.beginDataLoad=function() {
	
		$rootScope.disableDIV = true;
		$rootScope.toggle = 'disabled';
		console.log("Data Processing initiated !!!");		
		
		if($scope.dbuser == null || $scope.dbuser == '')
			$scope.dbuser = 'Admin';
		
		if($scope.dbpasswd == null || $scope.dbpasswd == '')
			$scope.dbpasswd = 'Admin';
		
		var data = {
			configName: $scope.newConfigName,
			databaseName: $scope.dbName,
			dbUserName:$scope.dbuser,
			dbPassword:$scope.dbpasswd,
			schemaName:$scope.schemaSelect,
			schemaDetail:$scope.newConfigName
        };
        
        var config = {
            headers : {
                'Content-Type': 'application/json'
            }
        }

        $http.post('/startProcess/', data, config)
        .success(function (response) {
        	console.log(response);
    		$scope.selectedIndex = 0;
        	$rootScope.disableDIV = false;
    		$rootScope.toggle = 'enabled';
        	displaySuccess("Configuration created !!!");
        })
        .error(function (response) {
            console.log(response);
    		$scope.selectedIndex = 0;
            $rootScope.disableDIV = false;
    		$rootScope.toggle = 'enabled';
            displayError("Configuration not created !!!");

        }).finally(function() {
        // called no matter success or failure
        	Configuration = restGET(configList);
            return response;
      });
	
	}
	
	$scope.brosweJDO=function() {
		$timeout(function() {
			angular.element('#jdo').triggerHandler('click');
		});
	};
	
	$scope.jdoFile=function(item) {
		
		console.log(" File : "+item+"\t"+$scope.jdoFileSelected);
	};
	$scope.$watch('schemaSelect', function() {

		console.log("here"+$scope.schemaSelect+""+$scope.opt);
	});
	
	$scope.$watch('selectedIndex', function(current, old){
		
		if(old != current && old > current)
		{
			if(1 == old || current == 0)
			{
				$scope.disabledatabase=true;
				$scope.disableschema=true;
				$scope.disablecomplete=true;
				clearAll();
			}
			else if(2 == old || current == 1)
			{
				$scope.disableschema=true;
				$scope.disablecomplete=true;
			}
			else if(3 == old || current == 2)
				$scope.disablecomplete=true;
		}
		else
		{
			if(0 == old && 1 == current)
				$scope.disabledatabase=false;
			else if(1 == old && 2 == current)
				$scope.disableschema=false;
			else if(2 == old && 3 == current)
			{
				$scope.disablecomplete=false;
				
				// display summary
				var summary = "<table class='table borderless'><thead>"
				
				summary	= summary + "<tr><th>Configuration Name</th><th>"+$scope.newConfigName+"</th></tr>";
				summary	= summary + "<tr><th>Databse Name</th><th>"+$scope.dbName+"</th></tr>";
				summary	= summary + "<tr><th>Schema Type</th><th>"+$scope.schemaSelect+"</th></tr>";
				
				if($scope.schemaSelect == 'JDO')
				{
					summary	= summary + "<tr><th>JDO zip</th><th>"+$scope.jdoFile.name+"</th></tr>";
					summary	= summary + "<tr><th>Code jar</th><th>"+$scope.jarFile.name+"</th></tr>";
				}
				
				summary = summary + "</thead></table>"
				$scope.summary = $sce.trustAsHtml(summary);
			}
		}
	});
	
	$scope.$watch('newConfigName', function(name){
		console.log(" Config Name : "+name);
		
		if(typeof(name) != undefined && name != null && name != '')
		{
			var isExist = false;
			angular.forEach(Configuration, function(value, key) {
				if(!isExist && typeof(value.configName) != undefined && value.configName != null )
				{
					if(value.configName === name)
					{
						isExist = true;
					}
					console.log(key + ' : ' + value.configName);
				}
			});
			
			if(isExist)
			{
				//show error
				$scope.inValidConfigName = true;
				$scope.welcomeNext=true;
			}
			else
			{
				//enable begin button
				$scope.inValidConfigName = false;
				$scope.welcomeNext=false;
			}
		}
		else
		{
			$scope.welcomeNext=true;
		}
	});
	
	$scope.$watch('dbName', function(name){
		console.log(" DataBase Name : "+name);
		
		if(typeof(name) != undefined && name != null && name != '')
		{
			var isExist = false;
			angular.forEach(Configuration, function(value, key) {
				if(!isExist && typeof(value.databaseName) != undefined && value.databaseName != null )
				{
					if(value.databaseName === name)
					{
						isExist = true;
					}
					console.log(key + ' : ' + value.databaseName);
				}
			});
			
			if(isExist)
			{
				//show error
				$scope.inValidDBName = true;
				$scope.databaseNext=true;
			}
			else
			{
				//enable begin button
				$scope.inValidDBName = false;
				$scope.databaseNext=false;
			}
		}
		else
		{
			$scope.databaseNext=true;
		}
	});
	
	
	
    function restGET(url) 
    {
        $http({
            method : 'GET',
            url : url
        }).then(function successCallback(response) {
            console.log(response);
        }, function errorCallback(response) {
            console.log(response.statusText);
        }).finally(function() {
        // called no matter success or failure
            return response;
      });
    } 
    
    $scope.uploadFile = function(){
        var fileone = $scope.fileOne;
        
        var fd = new FormData();
        fd.append('configName',$scope.newConfigName);
        fd.append('fileOne', fileone);

        var uploadUrl = "/upload";
        //fileUpload.uploadFileToUrl(fd, uploadUrl);
        
        
        $http.post(uploadUrl, fd, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}//'multipart/form-data;boundary=gc0p4Jq0M2Yt08jU534c0p'}
        })
        .success(function(response){
        	console.log(response);
        	displaySuccess("File uploaded !!");
        })
        .error(function(response){
        	console.log(response);
        	displayError("File not uploaded !!");
        	
        }).finally(function() {
        // called no matter success or failure
            return response;
      });
        
    };
});