import org.jcloudcodes.global.ArgoCdOps

def bootstrapApplication(Map config) { new ArgoCdOps(this).bootstrapApplication(config) }
def syncApplication(Map config) { new ArgoCdOps(this).syncApplication(config) }
