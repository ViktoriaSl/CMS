function CourseSubscribersCtrl($scope, $modal, courseService, courseId, subscribers) {
    var subscribersNotUpdated = angular.copy(subscribers);
    $scope.hideProcessed = true;
    $scope.subscribers = subscribers.filter(courseService.isStatusNotProcessed);
    $scope.getLabelForStatus = function (status) {
        return courseService.convertStatusToLabel(status);
    };
    $scope.availableStatuses = courseService.getAvailableStatuses();
    $scope.updateStatuses = function () {
        var subscribersToUpdate = [];
        for (var i = 0; i < subscribersNotUpdated.length; i++) {
            for (var j = 0; j < $scope.subscribers.length; j++) {
                if ((subscribersNotUpdated[i].id == $scope.subscribers[j].id) &&
                    (subscribersNotUpdated[i].courseStatus != $scope.subscribers[j].courseStatus)) {
                    subscribersToUpdate.push(courseService.normalizeSubscriber($scope.subscribers[j]));
                    subscribersNotUpdated[i].courseStatus = $scope.subscribers[j].courseStatus;
                }
            }
        }
        courseService.updateCourseSubscribers(courseId, subscribersToUpdate).then(
            function (success) {
                if (success.responseStatus == 202) {
                    if ($scope.hideProcessed) {
                        $scope.showHideProcessed();
                    }
                } else {
                    alertData.textAlert = success;
                    showAlertWithError(alertData);
                }
            },
            function (error) {
                alertData.textAlert = error;
                showAlertWithError(alertData);
            });

    };
    var alertData = {
        boldTextTitle: "Ошибка",
        mode: 'danger'
    };
    var showAlertWithError = function (alertData) {
        var modalInstance = $modal.open(
            {
                templateUrl: 'angular/templates/alertModal.html',
                controller: function ($scope, $modalInstance) {
                    $scope.data = alertData;
                    $scope.close = function () {
                        $modalInstance.close();
                    }
                },
                backdrop: true,
                keyboard: true,
                backdropClick: true,
                size: 'lg'
            }
        );
    };

    $scope.showHideProcessed = function () {
        $scope.subscribers = $scope.hideProcessed ? subscribers.filter(courseService.isStatusNotProcessed) : subscribers;
    };
    // ----------------  search -------------------------
    $scope.search = '';
    var searchFunction = function (value) {
        return value.personLastName.indexOf($scope.search) == 0;
    };
    $scope.searchNames = function () {
        $scope.showHideProcessed();
        $scope.subscribers = $scope.subscribers.filter(searchFunction);
    };
    // ----------------  pagination -------------------------
    $scope.currentPage = 1;
    $scope.maxPersonsPerPage = 2;
    $scope.numPages = 5;
}
