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

        $scope.room = {
           shape: [
               {x: 0, y: 0},
               {x: 80, y: 0},
               {x: 80, y: 80},
               {x: 0, y: 80}
           ]
        };

        $scope.removePoint = function(index) {
            $scope.room.shape.splice(index, 1);
        };

        $scope.addPoint = function() {
            $scope.room.shape.push({x: 0, y: 0})
        };

        $scope.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        }
    });