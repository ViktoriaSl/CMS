package org.javatraining.ws.service;

import org.javatraining.auth.Auth;
import org.javatraining.config.AuthRole;
import org.javatraining.model.CourseVO;
import org.javatraining.model.MarkVO;
import org.javatraining.model.PersonVO;
import org.javatraining.service.PersonService;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by asudak on 6/11/15.
 */
@Path("person")
public class MarkWebService {
    @EJB
    private PersonService personService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{person_id}/course/{course_id}/marks")
    @Auth(roles = {AuthRole.TEACHER, AuthRole.STUDENT})
    public Response getMarks(@PathParam("person_id") long personId, @PathParam("course_id") long courseId) {
        List<MarkVO> marks = personService.getMarks(new PersonVO(personId), new CourseVO(courseId));
        return Response.ok(marks).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Auth(roles = {AuthRole.TEACHER})
    @Path("{person_id}/marks") // FIXME: add {course_id} to path
    public Response setMarks(@PathParam("person_id") long personId, List<MarkVO> marks) {
        PersonVO personVO = new PersonVO(personId);
        personService.setMarks(personVO, marks);
        return Response.accepted().build();
    }
}
