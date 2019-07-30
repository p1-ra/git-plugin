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
import org.eclipse.jgit.transport.RemoteConfig;
import java.util.List;
import hudson.util.DescribableList;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.FetchCommand;
/**
 * Force a git fetch before git checkout.
 *
 * @author Nementon
 */
public class FetchAllAfterCheckout extends GitSCMExtension {
    @DataBoundConstructor
    public FetchAllAfterCheckout() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCheckoutCompleted(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener) throws IOException, InterruptedException, GitException {
        listener.getLogger().println("Exec git fetch command.");

        List<RemoteConfig> repos = scm.getParamExpandedRepos(build, listener);
        if (repos.isEmpty()) {
            return;
        }

        for (RemoteConfig remoteRepository : repos) {
            try {
	    	for (URIish url : remoteRepository.getURIs()) {
			FetchCommand fetch = git.fetch_().from(url, remoteRepository.getFetchRefSpecs());
			listener.getLogger().println("Fetch repository >>>>>>>> " + url);  
			fetch.execute();
	    	}
            } catch (GitException|InterruptedException ex) {
                ex.printStackTrace(listener.error("Error fetching remote repo '" + remoteRepository.getName() + "'"));
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
        return o instanceof FetchAllAfterCheckout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return FetchAllAfterCheckout.class.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FetchAllAfterCheckout{}";
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "Git fetch after checkout";
        }
    }
}
