import org.jcloudcodes.templates.GitOpsAksTemplate

def update(Map config) { new GitOpsAksTemplate(this).update(config) }
def refreshVaultToken(Map config) { new GitOpsAksTemplate(this).refreshVaultToken(config) }
def bootstrapApp(Map config) { new GitOpsAksTemplate(this).bootstrapApp(config) }
def sync(Map config) { new GitOpsAksTemplate(this).sync(config) }
def verify(Map config) { new GitOpsAksTemplate(this).verify(config) }
