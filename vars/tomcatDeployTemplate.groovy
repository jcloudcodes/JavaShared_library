import org.jcloudcodes.templates.TomcatDeployTemplate

def deployLinux(Map config) { new TomcatDeployTemplate(this).deployLinux(config) }
def deployWindows(Map config) { new TomcatDeployTemplate(this).deployWindows(config) }
