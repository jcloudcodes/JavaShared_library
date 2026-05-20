import org.jcloudcodes.templates.SonarTemplate

def scan(Map config) { new SonarTemplate(this).scan(config) }
