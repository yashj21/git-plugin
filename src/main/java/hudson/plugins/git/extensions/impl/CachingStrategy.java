package hudson.plugins.git.extensions.impl;

import org.jenkinsci.plugins.gitclient.GitClient;

import hudson.ExtensionPoint;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtension;

public abstract class CachingStrategy extends GitSCMExtension implements ExtensionPoint{
	
	@Override
	public boolean hasCachingStrategy(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener) {
		cacheImpl(scm,build,git,listener);
		return true;
	}
	public abstract void cacheImpl(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener);
	  
}
