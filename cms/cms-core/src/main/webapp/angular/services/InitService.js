function InitService($window, Restangular) {

    this.initRestangular = function () {
        Restangular.setBaseUrl($window.location.origin + $window.location.pathname);
    };

    this.getGoogleClientId = function () {
        return Restangular.all("resources").customGET('angular', {'param': 'client_id'});
    };
}