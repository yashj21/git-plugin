package hudson.plugins.git.extensions;

import java.util.List;

import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.Job;

public abstract class  CachingStrategyDescriptor extends Descriptor<CachingStrategy> {

	public static final List<CachingStrategyDescriptor> isApplicable(Job project) {
         return ExtensionList.lookup(CachingStrategyDescriptor.class);
         }
}
