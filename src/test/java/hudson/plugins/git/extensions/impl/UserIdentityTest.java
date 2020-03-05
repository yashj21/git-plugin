package hudson.plugins.git.extensions.impl;

import hudson.EnvVars;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.TestGitRepo;
import hudson.plugins.git.UserRemoteConfig;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jenkinsci.plugins.gitclient.Git;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UserIdentityTest extends GitSCMExtensionTest  {

    TestGitRepo repo;
    GitClient git;

    @Override
    public void before() throws Exception {
        repo = new TestGitRepo("repo", tmp.newFolder(), listener);
        git = Git.with(listener, new EnvVars()).in(repo.gitDir).getClient();
    }

    @Override
    protected GitSCMExtension getExtension() {
        return new UserIdentity("Jane Doe", "janeDoe@xyz.com");
    }

    @Test
    public void testUserIdentity() throws Exception {
        FreeStyleProject projectWithMaster = setupBasicProject(repo);
        UserIdentity userIdentity = new UserIdentity("Jane Doe", "janeDoe@xyz.com");
        ((GitSCM)projectWithMaster.getScm()).getExtensions().add(userIdentity);

        git.commit("First commit");

        FreeStyleBuild build = build(projectWithMaster, Result.SUCCESS);
        EnvVars envVars = build.getEnvironment(listener);
        assertThat("Jane Doe", is(envVars.get("GIT_AUTHOR_NAME")));
        assertThat("janeDoe@xyz.com", is(envVars.get("GIT_AUTHOR_EMAIL")));
    }

    @Test
    public void testGetNameAndEmail(){
        UserIdentity userIdentity = new UserIdentity("Jane Doe", "janeDoe@xyz.com");

        assertThat("Jane Doe", is(userIdentity.getName()));
        assertThat("janeDoe@xyz.com", is(userIdentity.getEmail()));
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserIdentity.class)
                .usingGetClass()
                .verify();
    }
}
