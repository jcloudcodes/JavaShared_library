package org.jcloudcodes.pipelines

import org.jcloudcodes.templates.DockerTemplate
import org.jcloudcodes.templates.GitOpsAksTemplate
import org.jcloudcodes.templates.GitlabMavenRegistryTemplate
import org.jcloudcodes.templates.HelmTemplate
import org.jcloudcodes.templates.JavaMavenTemplate
import org.jcloudcodes.templates.NexusTemplate
import org.jcloudcodes.templates.SonarTemplate
import org.jcloudcodes.templates.TomcatDeployTemplate

class PlatformJavaAksPipeline implements Serializable {
    private final def steps

    PlatformJavaAksPipeline(def steps) {
        this.steps = steps
    }

    void run(Map userConfig = [:]) {
        Map config = [
            agentLabel               : userConfig.get('agentLabel', ''),
            javaVersion              : userConfig.get('javaVersion', '17'),
            mavenCommand             : userConfig.get('mavenCommand', 'mvn'),
            mavenCliOpts             : userConfig.get('mavenCliOpts', '-B -DskipTests=false'),
            mavenRepoLocal           : userConfig.get('mavenRepoLocal', '.m2/repository'),
            gitopsBranch             : userConfig.get('gitopsBranch', 'main'),
            externalSecretsNamespace : userConfig.get('externalSecretsNamespace', 'external-secrets'),
            verifyEnvironment        : userConfig.get('verifyEnvironment', true),
            refreshVaultToken        : userConfig.get('refreshVaultToken', true),
            bootstrapArgoCdApp       : userConfig.get('bootstrapArgoCdApp', true),
            argocdCredentialId       : userConfig.get('argocdCredentialId', ''),
            sonarEnabled             : userConfig.get('sonarEnabled', false),
            nexusEnabled             : userConfig.get('nexusEnabled', false),
            helmPublishEnabled       : userConfig.get('helmPublishEnabled', false),
            gitlabRegistryEnabled    : userConfig.get('gitlabRegistryEnabled', false),
            deployLinuxTomcat        : userConfig.get('deployLinuxTomcat', false),
            deployWindowsTomcat      : userConfig.get('deployWindowsTomcat', false),
            imageTag                 : userConfig.get('imageTag', steps.env.BUILD_NUMBER ?: 'latest')
        ] + userConfig

        validateConfig(config)

        def javaTemplate = new JavaMavenTemplate(steps)
        def dockerTemplate = new DockerTemplate(steps)
        def sonarTemplate = new SonarTemplate(steps)
        def nexusTemplate = new NexusTemplate(steps)
        def gitlabRegistryTemplate = new GitlabMavenRegistryTemplate(steps)
        def helmTemplate = new HelmTemplate(steps)
        def aksTemplate = new GitOpsAksTemplate(steps)
        def tomcatTemplate = new TomcatDeployTemplate(steps)

        steps.pipeline {
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
                stage('Sonar Scan') {
                    when { expression { config.sonarEnabled } }
                    steps { script { sonarTemplate.scan(config) } }
                }
                stage('Nexus Publish') {
                    when { expression { config.nexusEnabled } }
                    steps { script { nexusTemplate.publishMaven(config) } }
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
                stage('Bootstrap Argo CD App') {
                    when { expression { config.bootstrapArgoCdApp && config.argocdAppManifestFile?.trim() } }
                    steps { script { aksTemplate.bootstrapApp(config) } }
                }
                stage('Argo CD Sync') {
                    when { expression { config.argocdServer?.trim() && config.argocdAppName?.trim() } }
                    steps { script { aksTemplate.sync(config) } }
                }
                stage('Verify Environment') {
                    when { expression { config.verifyEnvironment && config.kubeNamespace?.trim() && config.aksClusterName?.trim() } }
                    steps { script { aksTemplate.verify(config) } }
                }
                stage('Tomcat Deploy Linux') {
                    when { expression { config.deployLinuxTomcat } }
                    steps { script { tomcatTemplate.deployLinux(config) } }
                }
                stage('Tomcat Deploy Windows') {
                    when { expression { config.deployWindowsTomcat } }
                    steps { script { tomcatTemplate.deployWindows(config) } }
                }
            }
        }
    }

    private void validateConfig(Map config) {
        List<String> required = [
            'appName',
            'imageRepository',
            'gitopsRepoUrl',
            'helmValuesFile',
            'vaultAddr',
            'vaultKvMount',
            'vaultSecretPath',
            'vaultRoleIdCredentialId',
            'vaultSecretIdCredentialId',
            'dockerCredentialId',
            'gitopsRepoTokenCredentialId'
        ]

        List<String> missing = required.findAll { !config[it]?.toString()?.trim() }
        if (!missing.isEmpty()) {
            steps.error("Missing required pipeline config: ${missing.join(', ')}")
        }
    }
}
