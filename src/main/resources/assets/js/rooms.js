angular.module('homieApp')
    .controller('RoomsController', function($scope, $http) {
        $scope.getAllRooms = function () {
            $http.get('/rooms')
                .success(function (data, status, headers, config) {
                    $scope.rooms = data;
                })
                .error(function (data, status, headers, config) {
                    alert('error getting all rooms ' + status);
                });
        };
        $scope.getAllRooms();

        $scope.alerts = [];
        $scope.newRoom = function () {
            $http.post('/rooms', $scope.room)
                .success(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status + " Location: " + headers('Location'),
                        "type": "success"
                    }];
                    $scope.getAllRooms();
                })
                .error(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status + " Error: " + angular.toJson(data),
                        "type": "danger"
                    }];
                });
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        }
    });