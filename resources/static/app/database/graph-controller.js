var GrapgController = angular.module('vertex.controller', []);
GrapgController.controller("GraphController", ['$scope', '$routeParams', '$location', '$q', 'Graph','$rootScope', '$timeout', function ($scope, $routeParams, $location, $q, Graph, $rootScope, $timeout) {

  var data = [];
  $scope.currentIndex = -1;

  $scope.physics = true;
  $scope.fullscreen = false;
  $scope.additionalClass = '';




  $scope.graphClass = "svg-container-collapsed";

  $scope.$watch("linkDistance", function (data) {
    if (data) {
      $scope.graph.changeLinkDistance(data);
    }
  });

  $scope.$watch("physics", function (newVal, oldVal) {


    if (newVal != undefined && oldVal != undefined && newVal !== oldVal) {

      if (newVal) {
        $scope.releasePhysics();
      } else {
        $scope.freezePhysics();
      }
    }


  });

 /*if (Database.hasClass(GraphConfig.CLAZZ)) {

    GraphConfig.get().then(function (data) {
      if (!data) {
        var newCfg = DocumentApi.createNewDoc(GraphConfig.CLAZZ);
        GraphConfig.set(newCfg).then(function (data) {
          $scope.gConfig = data;
        })
      } else {
        $scope.gConfig = data;
      }

    });
  } else {

    GraphConfig.init().then(function () {
      var newCfg = DocumentApi.createNewDoc(GraphConfig.CLAZZ);
      GraphConfig.set(newCfg).then(function (data) {
        $scope.gConfig = data;
      })
    });
  }*/

  $scope.$watch('gConfig', function (data) {
    if (data) {
      $scope.tmpGraphOptions.config = data.config;
      $scope.graphOptions = $scope.tmpGraphOptions;
    }
  })


  var config = {
    height: 500,
    width: 1200,
    classes: {},
    node: {
      r: 30
    }

  }


  $scope.resetZoom = function () {
    $scope.graph.resetZoom();
  }

  $scope.download = function () {

    var html = d3.select(".graph-container svg")
      .attr("version", 1.1)
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .node().parentNode.innerHTML;


    var blob = new Blob([html], {type: "image/svg+xml"});
    saveAs(blob, "myProfile.svg");


  }
  $scope.clear = function () {
    $scope.graph.clear();
    Graph.clear()
  }


  $scope.nodesLen = Graph.data.vertices.length;
  $scope.edgesLen = Graph.data.edges.length;
  
  $scope.tmpGraphOptions = {
    data: Graph.data,
    onLoad: function (graph) {
      $scope.graph = graph;

      $scope.graph.on('data/changed', function (graph) {
        $scope.nodesLen = graph.nodes.length;
        $scope.edgesLen = graph.links.length;
      })
      $scope.graph.on('node/click', function (v) {
        alert("Hello world");
        /*var q = "SELECT outE().type.asSet() as out, inE().type.asSet() as in from " + v.source["rid"];
        CommandApi.queryText({
          database: $routeParams.database,
          contentType: 'JSON',
          language: 'sql',
          text: q,
          limit: -1,
          shallow: false,
          verbose: false
        }, function (data) {
          v.relationships = data.result[0];

          var query = "";
          var projection = "{{direction}}('{{label}}').size() as `{{direction}}_{{label}}`"
          var q = "SELECT {{projection}} from " + v.source["rid"];

          data.result[0].out.forEach(function (out) {
            query += S(projection).template({direction: "out", label: out}).s + ",";
          })
          data.result[0].in.forEach(function (in_) {
            query += S(projection).template({direction: "in", label: in_}).s + ",";
          })


          if (query !== "") {
            var pos = query.lastIndexOf(',');
            query = query.substring(0, pos);
            query = S(q).template({projection: query}).s;
            CommandApi.queryText({
              database: $routeParams.database,
              contentType: 'JSON',
              language: 'sql',
              text: query,
              limit: -1,
              shallow: false,
              verbose: false
            }, function (data) {
              v.relCardinality = data.result[0];
            });
          }

        })
        if (Aside.isOpen()) {
          $timeout(function () {
            $scope.doc = v.source;
            var title = $scope.doc['type'] + "-" + $scope.doc['rid'];
            Aside.show({
              scope: $scope,
              title: title,
              template: 'views/database/graph/asideVertex.html',
              show: true,
              absolute: false,
              fullscreen: $scope.fullscreen
            });
          }, 200);
        }*/
      });
      
      /*$scope.graph.on('edge/create', function (v1, v2) {

        if (v2['rid'].startsWith("#-")) {
          $scope.$apply(function () {
            Notification.push({
              content: 'Cannot connect ' + v1['rid'] + ' to a temporary node',
              autoHide: true,
              warning: true
            });
            $scope.graph.endEdgeCreation();
          });
        } else {
          $scope.showModalNewEdge(v1, v2);
        }
      });

      function openEdge(scope, e) {
        scope.doc = e.edge;
        var title = "Edge (" + e.label + ")";
        Aside.show({
          scope: scope,
          title: title,
          template: 'views/database/graph/asideEdge.html',
          show: true,
          absolute: false,
          fullscreen: scope.fullscreen
        });
      }*/

      $scope.graph.on('edge/click', function (e) {

        alert("edge");
        /*if (typeof e.edge == 'string') {
          DocumentApi.get({database: $routeParams.database, document: e.edge}, function (doc) {
            e.edge = doc;
            if (Aside.isOpen()) {
              openEdge($scope, e);
            }
          });
        }
        if (Aside.isOpen()) {
          openEdge($scope, e);
        }*/


      });
      /*$scope.graph.on('node/dblclick', function (v) {

        if (v['rid'].startsWith("#-")) {

          $scope.$apply(function () {
            Notification.push({content: 'Cannot expand a temporary node', autoHide: true, warning: true});
          });

        } else {

          var q = "traverse both() from " + v['rid'] + " while $depth < 2"
          CommandApi.graphQuery(q, {
            database: $routeParams.database,
            language: 'sql',
            limit: -1
          }).then(function (data) {
            $scope.graph.data(data.graph).redraw();
          })
        }
      });*/

      if ($routeParams.q) {
        if (!$scope.queryText) {
          $scope.queryText = $routeParams.q;
        }
        $scope.query();
      }
    },
    config: config
  }
  
  $scope.toggleQuery = function () {
    $scope.queryClass = !$scope.queryClass;
  }
  $scope.hideLegend = function () {
    $scope.graph.toggleLegend();
  }
  
  $rootScope.$on("DisplayGraph", function(event, args){
           $scope.query(args.test);
        });

  $scope.query = function (temp) {
      $scope.graph.data(temp).redraw();
      $scope.currentIndex = -1;
  }

  /*$scope.query = function () {

    Spinner.start();
    if ($scope.queryText.startsWith('g.')) {
      $scope.language = 'gremlin';
    } else {
      $scope.language = 'sql';
    }

    var queryBuffer = $scope.queryText;
    var selection = $scope.cm.getSelection();
    if (selection && selection != "") {
      queryBuffer = "" + selection;
    }
    CommandApi.graphQuery(queryBuffer, {
      database: $routeParams.database,
      language: $scope.language,
      limit: $scope.config.limit
    }).then(function (data) {
      var temp = {
                            "edges": [
                                {
                                "type": "Paths",
                                "rid": "#1:1",
                                "in": "#Table1",
                                "out": "#Table2"
                                },
                                {
                                "type": "Paths",
                                "rid": "#2:1",
                                "in": "#Table1",
                                "out": "#Table3"
                                }
                            ],
                            "vertices": [
                                {
                                "type": "Tables",
                                "rid": "#Table1",
                                "name": "Table_1"
                                },
                                {
                                "type": "Tables",
                                "rid": "#Table2",
                                "name": "Table_2"
                                },
                                {
                                "type": "Tables",
                                "rid": "#Table3",
                                "name": "Table_3"
                                }
                            ]
                            } ;
      $scope.graph.data(temp).redraw();
      $scope.history = History.push(queryBuffer);
      $scope.currentIndex = -1;
      Spinner.stopSpinner();
      $timeout(function () {
        Graph.add(data.graph);
      }, 1000);
    }).catch(function (err) {
      Spinner.stopSpinner();
      Notification.push({content: err, error: true, autoHide: true});
    })


  }*/


}])
;