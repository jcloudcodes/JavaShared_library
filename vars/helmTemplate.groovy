import org.jcloudcodes.templates.HelmTemplate

def packageChart(Map config) { new HelmTemplate(this).packageChart(config) }
def publishChart(Map config) { new HelmTemplate(this).publishChart(config) }
