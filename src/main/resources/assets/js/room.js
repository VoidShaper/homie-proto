angular.module('homieApp')
    .controller('RoomDetailController', function ($scope, $http, $routeParams) {
        $scope.retrieveRoom = function () {
            $http.get('/rooms/' + $routeParams.roomId)
                .success(function (data, status, headers, config) {
                    $scope.room = data;
                })
                .error(function (data, status, headers, config) {
                    alert('error getting room ' + status);
                });
        };

        $scope.placeInRoom = function () {
            $http({
                url: '/rooms/' + $routeParams.roomId,
                method: 'patch',
                headers: {
                    'Content-Type': 'application/json-patch+json'
                },
                data: {
                    op: 'add',
                    path: '/appliances',
                    value: $scope.appliancePlacement.data
                }
            })
                .success(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status,
                        "type": "success"
                    }];

                    $scope.retrieveRoom();
                    $scope.cancelPlacing();
                })
                .error(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status + " Error: " + angular.toJson(data),
                        "type": "danger"
                    }];
                });
        };

        $scope.retrieveAppliances = function () {
            $http.get('/appliances')
                .success(function (data, status, headers, config) {
                    $scope.appliances = data;
                })
                .error(function (data, status, headers, config) {
                    alert('error getting room ' + status);
                });
        };

        $scope.room = null;

        $scope.retrieveRoom();
        $scope.retrieveAppliances();

        $scope.startPlacing = function () {
            $scope.appliancePlacement.data.point = {
                x: 20,
                y: 20
            };
            $scope.appliancePlacement.started = true;
        };

        $scope.cancelPlacing = function () {
            $scope.appliancePlacement.started = false;
        };

        $scope.appliancePlacement = {
            started: false,
            data: {
                applianceId: null,
                point: {
                    x: 20,
                    y: 20
                }
            }
        };
    });