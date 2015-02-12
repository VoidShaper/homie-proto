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
        $scope.initialStates = ['ON', 'OFF'];

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


        $scope.setOperationValue = function (operation, value) {
            operation.value = value;
        };

        $scope.otherFieldsFrom = function (appliance) {
            var otherFields = {};
            for (var property in appliance) {
                if (appliance.hasOwnProperty(property)
                && property != 'operations'
                && property != 'id'
                && property != 'type'
                && property != 'name') {
                    otherFields[property] = appliance[property];
                }
            }
            return otherFields;
        };

        $scope.perform = function (operation) {
            $http({
                url: operation.uri,
                method: operation.method,
                headers: {
                    'Content-Type': operation.contentType
                },
                data: {
                    op: operation.op,
                    path: operation.property,
                    value: operation.value
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
