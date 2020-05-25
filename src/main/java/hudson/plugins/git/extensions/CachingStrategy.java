package hudson.plugins.git.extensions;

import org.jenkinsci.plugins.gitclient.GitClient;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;

public abstract class CachingStrategy extends AbstractDescribableImpl<CachingStrategy> implements ExtensionPoint{
	
	public final void callCacheImpl(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener) {
		cacheImpl(scm,build,git,listener);
	}
	public abstract void cacheImpl(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener);
	  
	    @Override
	 public CachingStrategyDescriptor getDescriptor() {
	        return (CachingStrategyDescriptor) super.getDescriptor();
	    }
}
