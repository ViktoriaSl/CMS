package org.javatraining.ws.service;

import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.model.LessonWithDetailsVO;
import org.javatraining.service.LessonService;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

/**
 * Created by asudak on 6/24/15.
 */
@Path("course/{course_id}/lesson")
public class LessonWebService {
    private static final String PARAM_REMOVED_LINK_IDS = "removedLinks";
    private static final String PARAM_REMOVED_PRACTICE_IDS = "removedPractices";

    @EJB
    LessonService lessonService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getLessons(@PathParam("course_id") Long courseId) {
        Set<LessonWithDetailsVO> lessons = lessonService.getWithPracticesByCourseId(courseId);
        return Response.ok(lessons).build();
    }

    @GET
    @Path("{order_num}")
    @Produces(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.STUDENT, AuthRole.TEACHER})
    public Response getLessonByOrderNum(@PathParam("course_id") Long courseId, @PathParam("order_num") Long orderNum) {
        return Response.ok(lessonService.getByOrderNum(courseId, orderNum, true)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response saveLesson(@Context UriInfo uriInfo, @PathParam("course_id") Long courseId, LessonWithDetailsVO lesson) {
        Response.ResponseBuilder r;
        try {
            lesson.setCourseId(courseId);
            lesson.setId(null);
            lessonService.save(lesson);
            String lessonUri = uriInfo.getRequestUri().toString() + "/" + lesson.getId();
            r = Response.created(new URI(lessonUri));
        } catch (URISyntaxException e) {
            r = Response.serverError();
        }
        return r.build();
    }


    @PUT
    @Path("{order_num}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    public Response updateLessonByOrderNum(@QueryParam(PARAM_REMOVED_LINK_IDS) List<Long> removedLinks,
                                           @QueryParam(PARAM_REMOVED_PRACTICE_IDS) List<Long> removedPractices,
                                           LessonWithDetailsVO lesson) {
        lessonService.updateByOrderNum(lesson, removedLinks, removedPractices);
        return Response.ok().build();
    }

    @DELETE
    @Path("{order_num}")
    @Auth(roles = {AuthRole.TEACHER})
    public Response deleteLessonByOrderNum(@PathParam("course_id") Long courseId, @PathParam("order_num") Long orderNum) {
        lessonService.deleteByOrderNum(courseId, orderNum);
        return Response.ok().build();
    }
}
