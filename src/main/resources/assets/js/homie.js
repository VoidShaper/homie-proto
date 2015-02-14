angular.module('homieApp', ['ui.bootstrap', 'ngRoute']);
angular.module('homieApp')
    .controller('MainController', function ($scope, $route, $routeParams, $location) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    })
    .directive("test", function () {
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
                var scale = 1.0 * Math.min(canvas.width, canvas.height) / 100;

                draw(ctx, shape);

                function draw(ctx, shape) {
                    ctx.lineWidth = "1";
                    ctx.strokeStyle = '#000';
                    ctx.beginPath();
                    ctx.moveTo(shape[0].x * scale, shape[0].y * scale);

                    var arrayLength = shape.length;
                    for (var i = 1; i < arrayLength; i++) {
                        ctx.lineTo(shape[i].x * scale, shape[i].y * scale);
                    }

                    ctx.closePath();
                    ctx.stroke();
                }

                function drawAppliances(ctx, appliances) {
                    for (var applianceId in appliances) {
                        if (appliances.hasOwnProperty(applianceId)) {
                            ctx.beginPath();
                            ctx.arc(appliances[applianceId].x * scale,
                                appliances[applianceId].y * scale,
                                3 * scale,
                                0,
                                2 * Math.PI,
                                false);
                            ctx.fillStyle = 'green';
                            ctx.fill();
                        }
                    }
                };

                scope.$watch('room', function (newRoom) {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    draw(ctx, newRoom.shape);
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
            })
            .when('/rooms/:roomId', {
                templateUrl: '/html/room.html',
                controller: 'RoomDetailController'
            });

    })
    .controller('IntroController', function ($scope) {
        $scope.msg = "Hello world";
    });
