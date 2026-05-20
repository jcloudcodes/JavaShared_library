import org.jcloudcodes.templates.NexusTemplate

def publishMaven(Map config) { new NexusTemplate(this).publishMaven(config) }
