package hudson.scm;

import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.envinject.EnvInjectJobProperty;
import org.jenkinsci.plugins.envinject.EnvInjectJobPropertyInfo;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertTrue;

public class SubversionEnvInjectTest {

    public static String REPO_URL = "https://svn.jenkins-ci.org/trunk/hudson/test-projects/${REPO}";

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void pollingWithEnvInject() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();

        EnvInjectJobPropertyInfo jobPropertyInfo = new EnvInjectJobPropertyInfo(null, "REPO=trivial-maven", null, null, null, false);
        EnvInjectJobProperty envInjectJobProperty = new EnvInjectJobProperty();
        envInjectJobProperty.setOn(true);
        envInjectJobProperty.setInfo(jobPropertyInfo);
        project.addProperty(envInjectJobProperty);

        project.setScm(new SubversionSCM(REPO_URL));

        TaskListener listener = jenkins.createTaskListener();
        PollingResult poll = project.poll(listener);
        assertTrue(poll.hasChanges());

        jenkins.assertBuildStatusSuccess(project.scheduleBuild2(0).get());
    }
}
