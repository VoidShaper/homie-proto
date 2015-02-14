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

        $scope.room = null;

        $scope.retrieveRoom();
    });