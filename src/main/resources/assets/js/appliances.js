angular.module('homieApp')
    .controller('AppliancesController', function ($scope, $http) {

        $scope.getAllAppliances = function () {
            $http.get('/appliances')
                .success(function (data, status, headers, config) {
                    $scope.appliances = data;
                })
                .error(function (data, status, headers, config) {
                    alert('error getting all appliances ' + status);
                });
        };
        $scope.getAllAppliances();

        // TODO get these from server
        $scope.applianceTypes = ['LIGHT'];

        $scope.alerts = [];
        $scope.newAppliance = function () {
            $http.post('/appliances', $scope.appliance)
                .success(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status + " Location: " + headers('Location'),
                        "type": "success"
                    }];
                    $scope.getAllAppliances();
                })
                .error(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status + " Error: " + angular.toJson(data),
                        "type": "danger"
                    }];
                });
        };

        $scope.updateProperty = function (appliance, propertyName, propertyValue) {
            $http({
                url: "/appliances/" + appliance.id,
                method: "PATCH",
                headers: {
                    'Content-Type': "application/json-patch+json"
                },
                data: {
                    op: "replace",
                    path: "/" + propertyName,
                    value: propertyValue
                }
            })
                .success(function (data, status, headers, config) {
                    $scope.alerts = [{
                        "msg": "Http Status: " + status,
                        "type": "success"
                    }];
                    $scope.getAllAppliances();
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
