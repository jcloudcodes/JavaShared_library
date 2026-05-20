import org.jcloudcodes.global.AksOps

def refreshEsoVaultToken(Map config) { new AksOps(this).refreshEsoVaultToken(config) }
def verifyNamespace(Map config) { new AksOps(this).verifyNamespace(config) }
