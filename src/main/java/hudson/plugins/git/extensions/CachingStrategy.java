package hudson.plugins.git.extensions;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;

public abstract class CachingStrategy extends AbstractDescribableImpl<CachingStrategy> implements ExtensionPoint{
	
	public final void callCacheImpl() {
		cacheImpl();
	}
	public abstract boolean cacheImpl();
	  
	    @Override
	 public CachingStrategyDescriptor getDescriptor() {
	        return (CachingStrategyDescriptor) super.getDescriptor();
	    }
}
