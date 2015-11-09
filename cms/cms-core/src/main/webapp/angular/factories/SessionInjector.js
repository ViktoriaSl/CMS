function SessionInjector($rootScope, sessionService) {
    var setServerProcState = function(state) {
        $rootScope.$broadcast('app.serverProcessing', {'processed': state});
    };
    var sessionInjector = {
        request: function (config) {
            if (!sessionService.isAnonymous()) {
                config.headers['x-session-token'] = sessionService.getAccessToken();
                config.headers['x-session-id'] = sessionService.getUserId();
            }
            setServerProcState(true);
            return config;
        },
        response: function (response) {
            response.data = response.data || {};
            response.data.responseStatus = response.status;
            setServerProcState(false);
            return response;
        },
        responseError: function (response) {
            if (response.status === 401) {
                $rootScope.$broadcast('app.unauthorized');
            }
            response.data = response.data || {};
            response.data.responseStatus = response.status;
            setServerProcState(false);
            return response;
        }
    };
    return sessionInjector;
}