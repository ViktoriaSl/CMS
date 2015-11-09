function NewsService(Restangular) {
    var restBase = 'resources/course/news';
    var News = Restangular.all(restBase);
    this.getNews = function () {
        return News.getList();
    };

    this.getPersonNews = function (personId) {
        if (personId === undefined) {
            return {};
        }
        return Restangular.one(restBase, personId).getList();
    };

    this.getNewsFromCourse = function (courseId) {
        if (courseId === undefined) {
            return {};
        }
        return Restangular.one(restBase, courseId).getList();
    };


    this.isNewsSuccessfullyCreated = function (returnedObject) {
        return returnedObject.responseStatus == 201;
    };


    this.createNews = function (newNews, courseId) {
        return Restangular.one(restBase, courseId).post(newNews);
    };


    this.removeNews = function (newsId) {
        return Restangular.one(restBase, newsId).remove();
    };


}