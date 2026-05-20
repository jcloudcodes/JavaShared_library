import org.jcloudcodes.templates.GitlabMavenRegistryTemplate

def publish(Map config) { new GitlabMavenRegistryTemplate(this).publish(config) }
