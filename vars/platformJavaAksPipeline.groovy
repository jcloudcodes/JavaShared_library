import org.jcloudcodes.pipelines.PlatformJavaAksPipeline
import org.jcloudcodes.templates.JavaMavenTemplate

def call(Map userConfig = [:]) {
    def pipelineConfig = new PlatformJavaAksPipeline()
    Map config = pipelineConfig.prepareConfig(userConfig, env.BUILD_NUMBER ?: 'latest')
    List<String> missing = pipelineConfig.validateConfig(config)

    if (!missing.isEmpty()) {
        error "Missing required pipeline config: ${missing.join(', ')}"
    }

    def javaTemplate = new JavaMavenTemplate(this)

    pipeline {
        agent {
            if (config.agentLabel?.trim()) {
                label config.agentLabel
            } else {
                any
            }
        }

        options {
            timestamps()
            ansiColor('xterm')
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: '20'))
        }

        environment {
            APP_NAME = "${config.appName}"
            JAVA_VERSION = "${config.javaVersion}"
            MAVEN_CLI_OPTS = "${config.mavenCliOpts}"
            MAVEN_REPO_LOCAL = "${config.mavenRepoLocal}"
            IMAGE_REPOSITORY = "${config.imageRepository}"
            IMAGE_TAG = "${config.imageTag}"
            GITOPS_REPO_URL = "${config.gitopsRepoUrl}"
            GITOPS_BRANCH = "${config.gitopsBranch}"
            HELM_VALUES_FILE = "${config.helmValuesFile}"
            ARGOCD_APP_MANIFEST_FILE = "${config.argocdAppManifestFile ?: ''}"
            ARGOCD_APP_NAME = "${config.argocdAppName ?: ''}"
            ARGOCD_SERVER = "${config.argocdServer ?: ''}"
            KUBE_NAMESPACE = "${config.kubeNamespace ?: ''}"
            AKS_CLUSTER_NAME = "${config.aksClusterName ?: ''}"
            EXTERNAL_SECRETS_NAMESPACE = "${config.externalSecretsNamespace}"
            VAULT_ADDR = "${config.vaultAddr}"
            VAULT_NAMESPACE = "${config.vaultNamespace ?: ''}"
            VAULT_KV_MOUNT = "${config.vaultKvMount}"
            VAULT_SECRET_PATH = "${config.vaultSecretPath}"
        }

        stages {
            stage('Validate') {
                steps { script { javaTemplate.validate(config) } }
            }
            stage('Build') {
                steps { script { javaTemplate.build(config) } }
            }
            stage('Test') {
                steps { script { javaTemplate.test(config) } }
            }
            stage('Package') {
                steps { script { javaTemplate.packageArtifact(config) } }
            }

            /*
             * First-time test mode:
             * leave only the core Java lifecycle stages enabled.
             * Uncomment the later stages one at a time as you expand testing.
             */
        }
    }
}
