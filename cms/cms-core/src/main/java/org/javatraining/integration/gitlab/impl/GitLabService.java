package org.javatraining.integration.gitlab.impl;

import org.javatraining.config.system.props.ApplicationProperty;
import org.javatraining.config.system.GitLabPropKeys;
import org.javatraining.integration.gitlab.api.interfaces.GitLabAPIClient;
import org.javatraining.integration.gitlab.api.model.*;
import org.javatraining.integration.gitlab.converter.GitUserConverter;
import org.javatraining.integration.gitlab.exception.ResourceNotFoundException;
import org.javatraining.integration.gitlab.exception.UserRequiredPropertiesIsNotComparable;
import org.javatraining.model.CourseVO;
import org.javatraining.model.PersonVO;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

/**
 * The project name is cms.
 * Created by sergey on 09.06.15 at 16:03.
 * For more information you should send mail to codedealerb@gmail.com
 */
@Dependent
public class GitLabService {
    private static final String ROOT = "root";
    private static final int PROJECTS_LIMIT_FOR_STUDENT = 10;

    @Inject
    @ApplicationProperty(name = GitLabPropKeys.PROP_HOST)
    private String gitLabHost;
    @Inject
    @ApplicationProperty(name = GitLabPropKeys.PROP_LOGIN)
    private String gitLabLogin;
    @Inject
    @ApplicationProperty(name = GitLabPropKeys.PROP_PSWRD)
    private String gitLabPswrd;
    @Inject
    @ApplicationProperty(name = GitLabPropKeys.PROP_EMAIL)
    private String gitLabEmail;

    @Inject
    private GitUserConverter gitUserConverter;
    private GitLabSessionParameters params;
    private GitLabAPIClient gitLabClient;
    private String pToken;

    public GitLabService() {
    }

    @PostConstruct
    public void init() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(gitLabHost);
        setParams(new GitLabSessionParameters(gitLabLogin, gitLabPswrd, gitLabEmail));
        this.gitLabClient = target.proxy(GitLabAPIClient.class);
        this.pToken = getPrivateToken();
    }

    public Collection<PersonVO> getAllPersons() {
        return gitUserConverter.convertAllEntities(gitLabClient.getAllUsers(pToken, ROOT));
    }

    public PersonVO getPerson(String email) {
        GitLabUser user = gitLabClient.getUser(pToken, ROOT, email).get(0);

        return gitUserConverter.convertGitLabUserEntity(user);
    }

    public GitLabUser getGitlabPerson(String email) {
        List<GitLabUser> result = gitLabClient.getUser(pToken, ROOT, email);
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    public boolean addPerson(PersonVO personVO) {
        if (getGitlabPerson(personVO.getEmail()) == null) {
            GitLabUser gitLabUser = gitUserConverter.convertPerson(personVO);
            gitLabUser.setProjectsLimit(PROJECTS_LIMIT_FOR_STUDENT);
            Response.Status status = gitLabClient.createUser(pToken, ROOT, gitLabUser);
            return status.getStatusCode() == 201;
        }
        return true;
    }


    public boolean updatePerson(PersonVO personVO) throws UserRequiredPropertiesIsNotComparable {
        PersonVO person = new GitUserConverter().convertGitLabUserEntity(
                gitLabClient.getUser(pToken, ROOT, personVO.getEmail()).get(0));

        try {
            person = new GitUserConverter().mergePersons(person, personVO);
            Response.Status status = gitLabClient.updateUser(pToken, ROOT, gitUserConverter.convertPerson(person));
            return status.getStatusCode() == 200;//FIXME check 200 or 201
        } catch (UserRequiredPropertiesIsNotComparable e) {
            throw e;
        }
    }

    public void removePerson(PersonVO personVO) {
        PersonVO gitLabUserVO = getPerson(personVO.getEmail());
        gitLabClient.removeUser(pToken, ROOT, gitLabUserVO.getId());
    }

    public boolean createProject(PersonVO personVO, CourseVO courseVO) {
        GitLabProject defaultProject = getDefaultProject();
        defaultProject.setName(String.format("%s - %s", courseVO.getName(), defaultProject.getName()));
        GitLabUser gitlabPerson = getGitlabPerson(personVO.getEmail());
        Response.Status status = gitLabClient.createProject(pToken, ROOT, gitlabPerson.getId(), defaultProject);
        return status.getStatusCode() == 201;
    }

    public boolean createProjectAndAddTeachers(PersonVO personVO, CourseVO courseVO, List<PersonVO> teachers) {
        GitLabProject defaultProject = getDefaultProject();
        defaultProject.setName(String.format("%s - %s", courseVO.getName(), defaultProject.getName()));
        GitLabUser gitlabPerson = getGitlabPerson(personVO.getEmail());
        GitLabProject createdProject = gitLabClient.createProjectReturnsProject(pToken, ROOT, gitlabPerson.getId(), defaultProject);
        if (createdProject == null) {
            return false;
        }
        boolean result = true;
        GitLabProjectMember projectMember = new GitLabProjectMember();
        projectMember.setAccessLevel(GitLabAccessLevel.Developer);
        for (PersonVO teacher : teachers) {
            GitLabUser gitLabUser = getGitlabPerson(teacher.getEmail());
            if (gitLabUser == null) {
                result &= false;
                break;
            }
            projectMember.setMemberId(gitLabUser.getId());
            Response.Status addMemberStatus = gitLabClient.addProjectTeamMember(pToken, ROOT, projectMember, createdProject.getId());
            result &= addMemberStatus.getStatusCode() == 201;
        }
        return result;
    }

    public Collection<GitLabProject> getAllProjects() {
        return gitLabClient.getAllProjects(pToken, ROOT);
    }

    public boolean addProjectMember(PersonVO personVO, Integer projId) throws ResourceNotFoundException {
        GitLabProjectMember projectMember =
                (GitLabProjectMember) gitUserConverter.convertPerson(personVO);
        projectMember.setAccessLevel(GitLabAccessLevel.Reporter);
        Response.Status status = gitLabClient.addProjectTeamMember(pToken, ROOT, projectMember, projId);

        if (status.getStatusCode() != 201) {
            throw new ResourceNotFoundException("user or project not found");
        }

        return true;
    }

    public boolean removeProjectMember(PersonVO personVO, GitLabProject project) throws ResourceNotFoundException {
        PersonVO gitLabEntityVO = getPerson(personVO.getEmail());
        GitLabProjectMember projectMember =
                (GitLabProjectMember) gitUserConverter.convertPerson(gitLabEntityVO);

        Response.Status status = gitLabClient.removeProjectTeamMember(pToken, ROOT, project.getId(), personVO.getId());

        if (status.getStatusCode() != 200) {
            throw new ResourceNotFoundException("user or project not found");
        }

        return true;
    }

    public GitLabSessionParameters getParams() {
        return params;
    }

    public void setParams(GitLabSessionParameters params) {
        this.params = params;
    }

    private String getPrivateToken() {
        GitLabSession session = gitLabClient.getSession(params, "root");
        return session.getPrivateToken();
    }

    private GitLabProject getDefaultProject() {
        GitLabProject projectEntity = new GitLabProject();

        projectEntity.setDescription("Personal project");
        projectEntity.setIssuesEnabled(true);
        projectEntity.setMergeRequestsEnabled(false);
        projectEntity.setName("Personal");
        projectEntity.setVisibilityLevel(GitLabProject.VISIBILITY_PRIVATE_LEVEL);
        projectEntity.setPublic(false);
        projectEntity.setWikiEnabled(true);
        projectEntity.setSnippetsEnabled(true);

        return projectEntity;
    }
}
