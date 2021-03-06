package org.javatraining.ws.service;

import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.model.CourseVO;
import org.javatraining.model.NewsVO;
import org.javatraining.service.CourseService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by vika on 02.07.15.
 */
@Stateless
@Path("course/news")
public class NewsWebService {
    @EJB
    private CourseService courseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNewsList() {
        List<NewsVO> news = courseService.getAllNews();
        return Response.ok(news).build();
    }

    @GET
    @Path("{person_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getPersonNews(@PathParam("person_id") Long personId) {
        return Response.ok(courseService.getAllPersonsNews(personId)).build();
    }

    @GET
    @Path("{news_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getNewsById(@PathParam("course_id") Long courseId, @PathParam("news_id") Long news_id) {
        return Response.ok(courseService.getNewsByIdFromCourse(courseId, news_id)).build();
    }

    @GET
    @Path("{course_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getNewsByCourseId(@PathParam("course_id") Long courseId) {
        return Response.ok(courseService.getNewsByCourseId(courseId)).build();
    }


    @POST
    @Path("{course_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response saveNews(@Context UriInfo uriInfo, @PathParam("course_id") Long courseId, NewsVO newsVO) {
        Response.ResponseBuilder r;
        try {
            newsVO.setId(null);
            newsVO.setCourseId(courseId);
            CourseVO courseVO = courseService.getCourseById(courseId);
            courseService.addNewsToCourse(courseVO, newsVO);
            String newsUri = uriInfo.getRequestUri().toString() + "/" + newsVO.getId();
            r = Response.created(new URI(newsUri));
        } catch (URISyntaxException e) {
            r = Response.serverError();
        }
        return r.build();
    }

    @DELETE
    @Path("{news_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response deleteNews(@PathParam("news_id") Long newsId) {
        courseService.removeNewsById(newsId);
        return Response.ok().build();
    }


}
