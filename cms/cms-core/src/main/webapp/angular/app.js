// Declare app level module which depends on views, and components
var myApp = angular.module('myApp', [
    'ui.router',
    //'ui.router.tabs',
    'restangular',
    'angular-google-gapi',
    'ui.bootstrap',
    'angularFileUpload',
    'angular-redactor',
    'LocalStorageModule',
    'myApp.main',
    'myApp.home',
    'myApp.person',
    'myApp.news',
    'myApp.about'
]);
myApp.service('sessionService', ['$window', '$rootScope', SessionService]);
myApp.service('sessionService', ['PersonPersistenceService', SessionService]);
myApp.service('PersonService', ['Restangular', PersonService]);
myApp.service('AuthService', ['PersonService', 'Restangular', AuthService]);
myApp.service('PersonPersistenceService', ['localStorageService', PersonPersistenceService]);
myApp.service('InitService', ['$window', 'Restangular', InitService]);
myApp.factory('sessionInjector', ['$rootScope', 'sessionService', SessionInjector]);

myApp.config(function (localStorageServiceProvider) {
    localStorageServiceProvider
        .setPrefix('cms')
        .setStorageType('localStorage')
        .setNotify(true, true);
});
myApp.config(['$stateProvider', '$urlRouterProvider', '$httpProvider',
    function ($stateProvider, $urlRouterProvider, $httpProvider) {
        $urlRouterProvider.otherwise('/home');
        $httpProvider.interceptors.push('sessionInjector');
    }]);

myApp.run(['GAuth', 'GApi', 'GData', '$state', '$rootScope', '$window', '$timeout', 'AuthService', 'PersonPersistenceService', 'InitService',
    function (GAuth, GApi, GData, $state, $rootScope, $window, $timeout, AuthService, PersonPersistenceService, InitService) {

        // ----------------  Restangular initialization -------------------------
        InitService.initRestangular();

        // ---------------------  Login / Logout  -------------------------
        $rootScope.doLogin = function (fromUrl) {
            InitService.getGoogleClientId().then(function (clientId) {
                GAuth.setClient(clientId);
                GAuth.setScope('https://www.googleapis.com/auth/userinfo.email');
                GAuth.login().then(
                    function (success) {
                        AuthService.goAuth(GData.getUser()).then(
                            function (data) {
                                PersonPersistenceService.saveInfo(
                                    data.id,
                                    data.personRole.toLowerCase(),
                                    data.name + " " + data.lastName,
                                    data.email.toLowerCase(),
                                    $window.gapi.auth.getToken().access_token);
                                AuthService.getEmailHash($rootScope.getEmail()).then(function (hash) {
                                    PersonPersistenceService.saveHash(hash.hash);
                                });
                                if (!fromUrl) {
                                    $rootScope.toState = 'person';
                                    $rootScope.toStateParams = '';
                                }
                                console.log("login success");
                                $state.go($rootScope.toState, $rootScope.toStateParams);
                            });
                    },
                    function () {
                        console.log('login fail');
                        $rootScope.goHome();
                    });
            });
        };
        $rootScope.doLogOut = function () {
            PersonPersistenceService.clearInfo();
            GAuth.logout().then(function () {
                $rootScope.goHome();
            });
        };
        $rootScope.isLogin = function () {
            return !(($rootScope.getUserId() == undefined) || ($rootScope.getUserId() == null));
        };

        $rootScope.goHome = function () {
            $rootScope.toState = 'home';
            $rootScope.toStateParams = '';
            $state.go($rootScope.toState, $rootScope.toStateParams);
        };

        // -----  Info for inputs and hrefs blocking while server processed the request ------
        $rootScope.blockUntilServerAnswered = false;

        // --------------------  Main person info -------------------------
        $rootScope.getEmail = function () {
            return PersonPersistenceService.getEmail();
        };
        $rootScope.getUserId = function () {
            return PersonPersistenceService.getId();
        };
        $rootScope.getUsername = function () {
            return PersonPersistenceService.getName();
        };
        $rootScope.isTeacher = function () {
            return PersonPersistenceService.isTeacher();
        };
        $rootScope.getEmailHash = function () {
            return PersonPersistenceService.getEmailHash();
        };
        // ------------------  Broadcast receivers -------------------------
        $rootScope.$on('app.unauthorized', function () {
            $rootScope.doLogOut();
        });
        $rootScope.$on('app.serverProcessing', function (event, args) {
            $timeout(function () {
                $rootScope.blockUntilServerAnswered = args.processed;
            }, 0);
        });
        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
            $rootScope.toState = toState;
            $rootScope.toStateParams = toStateParams;

            if (toState.data && toState.data.authRequired && !$rootScope.isLogin()) {
                event.preventDefault();
                $rootScope.doLogin(true);
            }
            if ((toState.name == 'home') && $rootScope.isLogin()) {
                event.preventDefault();
                $state.go('person');
            }
        });
    }]);


