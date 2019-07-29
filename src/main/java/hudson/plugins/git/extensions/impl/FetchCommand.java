package hudson.plugins.git.extensions.impl;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import java.io.IOException;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Force a git fetch before git checkout.
 *
 * @author Nementon
 */
public class FetchCommandExt extends GitSCMExtension {
    @DataBoundConstructor
    public FetchCommandExt() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeCheckout(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener) throws IOException, InterruptedException, GitException {
        listener.getLogger().println("Exec git fetch command.");

        List<RemoteConfig> repos = scm.getParamExpandedRepos(build, listener);
        if (repos.isEmpty()) {
            return;
        }

        for (RemoteConfig remoteRepository : repos) {
            try {
                scm.fetchFrom(git, listener, remoteRepository)

            } catch (GitException ex) {
                ex.printStackTrace(listener.error("Error fetching remote repo '" + remoteRepository.getName() + "'"));
                throw
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return o instanceof FetchCommandExt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return FetchCommandExt.class.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FetchCommandExt{}";
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Exec Git fetch command before checkout";
        }
    }
}
