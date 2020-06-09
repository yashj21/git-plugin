package hudson.plugins.git.extensions.impl;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.FetchCommand;
import org.jenkinsci.plugins.gitclient.GitClient;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.ExtensionPoint;
import hudson.FilePath;
import hudson.Util;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtension;

public abstract class CachingStrategy extends GitSCMExtension implements ExtensionPoint{
//	HashMap<> which contains branches loaded till now
	//ref spec map
	protected String url;
	public CachingStrategy(String repoWorkspace) {
		this.url = repoWorkspace;
	}
//	public void setRepoWorkspace(String repoWorkspace) {
//		this.repoWorkspace = repoWorkspace;
//	}
	@Override
	public FilePath hasCachingStrategy(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener,
			List<RemoteConfig> repos, Node node) {
		RemoteConfig rc = repos.get(0);
		String cacheEntry = createCacheEntry(rc.getURIs());
		FilePath cacheDir = new FilePath(node.getRootPath(),url);
		cacheDir = cacheDir.child(cacheEntry);
		try {
			if(!cacheDir.isDirectory()) {
				cacheDir.mkdirs();
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			EnvVars env =build.getEnvironment(listener);
			GitClient cacheClient = scm.createClient(listener, env, build, cacheDir);
			if(!cacheClient.hasGitRepo()) {
				cacheImpl(scm,build,cacheClient,listener,repos);
			}
			//else {
//				List<RefSpec> refspecs = rc.getFetchRefSpecs();
//				for(RefSpec refspec: refspecs) {
//					if(!cacheClient.refExists(refspec.toString())) {
////						/debug again on this behaviour
//						//don't think this is needed
//						//cligit api handles this
//						listener.getLogger().println("Updating ref spec");
//						rc.addFetchRefSpec(new RefSpec(refspec.toString()));
//					}
//				}
//			}
			try {
			fetchChanges(scm,build,cacheClient,listener,repos);
			}catch (GitException ex) {
                ex.printStackTrace(listener.error("Error cloning remote repo '" + rc.getName() + "'"));
                throw new AbortException("Error cloning remote repo '" + rc.getName() + "'");
            }
		}catch (IOException | InterruptedException e) {
			// TODO: handle exception
		}
		return cacheDir;
		//check if extension has sparse or shallow clone
		//create cache entry
		//does it exist?
		//if yes,cacheclient
		//scm.getParamExpandedRepos(build).get(0).
		//we always need to call fetch on local repo no matter what
	}
	
	public abstract void fetchChanges(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener,List<RemoteConfig> repos);
	
	public abstract void cacheImpl(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener,List<RemoteConfig> repos);
	
	protected String createCacheEntry(List<URIish> urIs) {
		String remotename = "";
		if(urIs!=null && urIs.size()>0) {
			remotename = urIs.get(0).toPrivateString();
		}
		return "git-" + Util.getDigestOf(remotename);
	}
	
	protected void fetchFrom(GitSCM scm, GitClient git,
            @CheckForNull Run<?, ?> run,
            TaskListener listener,
            RemoteConfig remoteRepository) throws InterruptedException, IOException {

        boolean first = true;
        for (URIish url : remoteRepository.getURIs()) {
            try {
                if (first) {
                    git.setRemoteUrl(remoteRepository.getName(), url.toPrivateASCIIString());
                    first = false;
                } else {
                    git.addRemoteUrl(remoteRepository.getName(), url.toPrivateASCIIString());
                }

                FetchCommand fetch = git.fetch_().from(url, remoteRepository.getFetchRefSpecs());
                for (GitSCMExtension extension : scm.getExtensions()) {
                    extension.decorateFetchCommand(scm, run, git, listener, fetch);
                }
                fetch.execute();
            } catch (GitException ex) {
                throw new GitException("Failed to fetch from "+url.toString(), ex);
            }
        }
    }
}
