var graph = angular.module('graph', []);

graph.directive('ngOGraph', function () {

  var linker = function (scope, element, attrs) {


    var opts = angular.extend({}, scope.$eval(attrs.ngOGraph));

    scope.$watch(attrs.ngOGraph, function (data) {

      if (data) {
        loadGraph();
      }
    })
    function loadGraph() {
      var opts = angular.extend({}, scope.$eval(attrs.ngOGraph));
      var ograph = OrientGraph.create(element[0], opts.config, opts.metadata, opts.menu, opts.edgeMenu);
      ograph.data(opts.data).draw();

      if (opts.onLoad) {
        opts.onLoad(ograph);
      }
    }

    if (opts.config) {
      loadGraph();
    }
  }
  return {
    restrict: 'A',
    link: linker
  }
});
