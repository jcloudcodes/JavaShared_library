package org.jcloudcodes.pipelines

class PlatformJavaAksPipeline implements Serializable {

    Map prepareConfig(Map userConfig = [:], String buildNumber = 'latest') {
        [
            agentLabel               : userConfig.get('agentLabel', ''),
            mavenToolName            : userConfig.get('mavenToolName', ''),
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
            imageTag                 : userConfig.get('imageTag', buildNumber ?: 'latest')
        ] + userConfig
    }

    List<String> validateConfig(Map config) {
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

        required.findAll { !config[it]?.toString()?.trim() }
    }
}
