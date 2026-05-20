import org.jcloudcodes.global.GitOpsOps

def updateImageValues(Map config) { new GitOpsOps(this).updateImageValues(config) }
def cloneRepo(Map config, Closure body) { new GitOpsOps(this).cloneRepo(config, body) }
