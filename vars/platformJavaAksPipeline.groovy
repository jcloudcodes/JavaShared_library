import org.jcloudcodes.pipelines.PlatformJavaAksPipeline

def call(Map userConfig = [:]) {
    new PlatformJavaAksPipeline(this).run(userConfig)
}
