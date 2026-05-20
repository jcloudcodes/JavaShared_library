import org.jcloudcodes.templates.GitOpsGkeTemplate

def update(Map config) { new GitOpsGkeTemplate(this).update(config) }
def refreshVaultToken(Map config) { new GitOpsGkeTemplate(this).refreshVaultToken(config) }
def bootstrapApp(Map config) { new GitOpsGkeTemplate(this).bootstrapApp(config) }
def sync(Map config) { new GitOpsGkeTemplate(this).sync(config) }
def verify(Map config) { new GitOpsGkeTemplate(this).verify(config) }
