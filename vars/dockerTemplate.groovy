import org.jcloudcodes.templates.DockerTemplate

def buildImage(Map config) { new DockerTemplate(this).buildImage(config) }
def pushDockerHub(Map config) { new DockerTemplate(this).pushDockerHub(config) }
