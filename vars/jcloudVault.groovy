import org.jcloudcodes.global.VaultOps

def withAppRole(Map config, Closure body) { new VaultOps(this).withAppRole(config, body) }
def withVaultToken(Map config, Closure body) { new VaultOps(this).withVaultToken(config, body) }
String readKvField(Map config, String fieldName) { new VaultOps(this).readKvField(config, fieldName) }
void writeKubeconfig(Map config) { new VaultOps(this).writeKubeconfig(config) }
