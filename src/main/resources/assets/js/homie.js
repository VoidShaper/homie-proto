angular.module('homieApp', ['ui.bootstrap', 'ngRoute']);
angular.module('homieApp')
    .controller('MainController', function ($scope, $route, $routeParams, $location) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    })
    .directive("test", function() {
        return {
            restrict: 'A',
            link: function (scope, element) {
                window.alert("aaa");
            },
            templateUrl: '/html/test.html'
        };
    })
    .directive("roomshape", function () {
        return {
            restrict: "A",
            link: function (scope, element) {
                var shape = scope.room.shape;
                var canvas = element[0];
                var ctx = canvas.getContext('2d');

                draw(ctx, shape);

                function draw(ctx, shape) {
                    ctx.lineWidth = "1";
                    ctx.strokeStyle = '#000';
                    ctx.beginPath();
                    ctx.moveTo(shape[0].x, shape[0].y);

                    var arrayLength = shape.length;
                    for (var i = 1; i < arrayLength; i++) {
                        ctx.lineTo(shape[i].x, shape[i].y);
                    }

                    ctx.closePath();
                    ctx.stroke();
                }

                scope.$watch('room.shape', function(newShape) {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    draw(ctx, newShape);
                }, true);
            }
        };
    })
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/html/intro.html',
                controller: 'IntroController'
            })
            .when('/appliances', {
                templateUrl: '/html/appliances.html',
                controller: 'AppliancesController'
            })
            .when('/rooms', {
                templateUrl: '/html/rooms.html',
                controller: 'RoomsController'
            });
    })
    .controller('IntroController', function ($scope) {
        $scope.msg = "Hello world";
    });
