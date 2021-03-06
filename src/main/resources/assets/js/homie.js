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
    .directive('appliancePopover', function ($compile) {
        var quickInfoTemplate =
            "<appliance-quick-info appliance='appliance' update-property='updateProperty'></appliance-quick-info>";

        return {
            restrict: "A",
            link: function (scope, element, attrs) {
                scope.placement = scope.placement || "right";
                var popOverContent = $compile(quickInfoTemplate)(scope);
                var options = {
                    content: popOverContent,
                    placement: scope.placement,
                    html: true
                };
                $(element).popover(options);
            },
            scope: {
                appliance: '=',
                updateProperty: '=',
                placement: '@?'
            }
        };
    })
    .directive("appliancesTab", function () {
        return {
            restrict: 'E',
            scope: {
                appliances: '=',
                updateProperty: '='
            },
            templateUrl: '/html/templates/appliances-tab.html'
        };
    })
    .directive("applianceProperty", function () {
        return {
            restrict: 'E',
            scope: {
                applianceUrl: '=',
                propertyName: '=',
                property: '=',
                updateProperty: '='
            },
            templateUrl: "/html/templates/appliance-property.html"
        }
    })
    .directive("conciseApplianceProperty", function () {
        var conciseAppPropertyIndex = 0;
        return {
            restrict: 'E',
            scope: {
                applianceUrl: '=',
                propertyName: '=',
                property: '=',
                updateProperty: '='
            },
            templateUrl: "/html/templates/appliance-property-concise.html",
            link: function (scope, element, attrs) {
                scope.index = conciseAppPropertyIndex;
                conciseAppPropertyIndex++;
            }
        }
    })
    .directive("applianceQuickInfo", function () {
        return {
            restrict: 'E',
            scope: {
                appliance: '=',
                updateProperty: '='
            },
            templateUrl: '/html/templates/appliance-quick-info.html',
            controller: function ($scope, $element, $http) {
                $scope.updateInfo = function () {
                    $http.get($scope.appliance.self)
                        .success(function (data, status, headers, config) {
                            $scope.appliance = data;
                        })
                        .error(function (data, status, headers, config) {
                            alert('error getting appliance ' + status);
                        });
                }

                $scope.iconOf = function (type) {
                    if (type == 'LIGHT') {
                        return 'glyphicon-lamp';
                    }
                    return 'glyphicon-question-sign';
                }
            }
        };
    })
    .directive("roomShape", function () {
        return {
            restrict: "E",
            scope: {
                width: "@",
                height: "@",
                room: "=",
                newPlacement: "=?",
                view: "@?"
            },
            template: "<canvas></canvas>",
            link: function (scope, element, attrs) {
                scope.view = scope.view || "justShape";
                scope.newPlacement = scope.newPlacement || null;
                var room = scope.room;
                var canvas = element.find("canvas")[0];
                canvas.width = scope.width;
                canvas.height = scope.height;
                var ctx = canvas.getContext('2d');
                var scale = 1.0 * Math.min(canvas.width, canvas.height) / 100;

                draw(ctx, room, scope.newPlacement);

                function draw(ctx, room, newplacement) {
                    drawShape(ctx, room.shape);
                    if (newplacement && newplacement.started) {
                        drawAppliance(ctx, newplacement.data.applianceId, newplacement.data.point, 'gray');
                    }
                    if (scope.view == "full") {
                        drawAppliances(ctx, room.appliances);
                    }
                };

                function drawAppliance(ctx, applianceId, point, fillStyle) {
                    ctx.beginPath();
                    ctx.arc(point.x * scale,
                        point.y * scale,
                        1 * scale,
                        0,
                        2 * Math.PI,
                        false);
                    ctx.fillStyle = fillStyle;
                    ctx.fill();
                }

                function drawAppliances(ctx, appliances) {
                    angular.forEach(appliances, function (placement, applianceId) {
                        drawAppliance(ctx, applianceId, placement.point, 'black');
                    });
                };

                function drawShape(ctx, shape) {
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
                };

                scope.$watch('room', function (newRoom) {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    draw(ctx, newRoom, scope.newPlacement);
                }, true);
                if (scope.newPlacement) {
                    scope.$watch('newPlacement', function (newplacement) {
                        ctx.clearRect(0, 0, canvas.width, canvas.height);
                        draw(ctx, scope.room, newplacement);
                    }, true);
                }
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
