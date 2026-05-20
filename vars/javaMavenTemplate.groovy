import org.jcloudcodes.templates.JavaMavenTemplate

def validate(Map config) { new JavaMavenTemplate(this).validate(config) }
def build(Map config) { new JavaMavenTemplate(this).build(config) }
def test(Map config) { new JavaMavenTemplate(this).test(config) }
def packageArtifact(Map config) { new JavaMavenTemplate(this).packageArtifact(config) }
