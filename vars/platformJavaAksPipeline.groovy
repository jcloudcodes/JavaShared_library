import org.jcloudcodes.pipelines.PlatformJavaAksPipeline
import org.jcloudcodes.templates.DockerTemplate
import org.jcloudcodes.templates.GitOpsAksTemplate
import org.jcloudcodes.templates.GitlabMavenRegistryTemplate
import org.jcloudcodes.templates.HelmTemplate
import org.jcloudcodes.templates.JavaMavenTemplate
import org.jcloudcodes.templates.NexusTemplate
import org.jcloudcodes.templates.SonarTemplate
import org.jcloudcodes.templates.TomcatDeployTemplate

def call(Map userConfig = [:]) {
    def pipelineConfig = new PlatformJavaAksPipeline()
    Map config = pipelineConfig.prepareConfig(userConfig, env.BUILD_NUMBER ?: 'latest')
    List<String> missing = pipelineConfig.validateConfig(config)

    if (!missing.isEmpty()) {
        error "Missing required pipeline config: ${missing.join(', ')}"
    }

    def javaTemplate = new JavaMavenTemplate(this)
    def dockerTemplate = new DockerTemplate(this)
    def sonarTemplate = new SonarTemplate(this)
    def nexusTemplate = new NexusTemplate(this)
    def gitlabRegistryTemplate = new GitlabMavenRegistryTemplate(this)
    def helmTemplate = new HelmTemplate(this)
    def aksTemplate = new GitOpsAksTemplate(this)
    def tomcatTemplate = new TomcatDeployTemplate(this)

    pipeline {
        agent {
            label "${config.agentLabel}"
        }

        options {
            timestamps()
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: '5'))
        }

        tools {
            maven "${config.mavenToolName}"
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
            stage('Docker Build') {
                    steps { script { dockerTemplate.buildImage(config) } }
            }
            stage('Docker Push') {
                    steps { script { dockerTemplate.pushDockerHub(config) } }
            }
            stage('GitLab Maven Registry Publish') {
                    when { expression { config.gitlabRegistryEnabled || config.gitlabMavenRegistryEnabled } }
                    steps { script { gitlabRegistryTemplate.publish(config) } }
            }
            stage('Helm Package') {
                    when { expression { config.helmChartPath?.trim() } }
                    steps { script { helmTemplate.packageChart(config) } }
            }
            stage('Helm Publish') {
                    when { expression { config.helmPublishEnabled } }
                    steps { script { helmTemplate.publishChart(config) } }
            }
            stage('GitOps Update') {
                    steps { script { aksTemplate.update(config) } }
            }
            stage('Refresh Vault Token') {
                    when { expression { config.refreshVaultToken && config.aksClusterName?.trim() } }
                    steps { script { aksTemplate.refreshVaultToken(config) } }
            }
            /*
             * First-time test mode:
             * leave only the core Java lifecycle stages enabled.
             * Uncomment the later stages one at a time as you expand testing.
             *
             * Notes:
             * - this version now expects a Jenkins agent label in config.agentLabel
             * - this version now expects a Jenkins Maven tool name in config.mavenToolName
             * - ansiColor was removed because your current Jenkins setup
             *   does not accept it in declarative options
             */
        }
    }
}
