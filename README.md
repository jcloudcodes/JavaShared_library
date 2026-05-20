# jcloudcodes Jenkins Shared Library Template

This repository is now structured in the standard Jenkins production shared-library format:

- `vars/`
- `src/org/jcloudcodes/global/...`
- `src/org/jcloudcodes/templates/...`
- `src/org/jcloudcodes/pipelines/...`

That means consuming application repositories can use the library cleanly, while the heavy implementation stays in typed Groovy classes under `src/`.

## Production structure

### Public entrypoints

- [vars/platformJavaAksPipeline.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/platformJavaAksPipeline.groovy)
- [vars/javaMavenTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/javaMavenTemplate.groovy)
- [vars/dockerTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/dockerTemplate.groovy)
- [vars/gitopsAksTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/gitopsAksTemplate.groovy)
- [vars/gitopsGkeTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/gitopsGkeTemplate.groovy)
- [vars/helmTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/helmTemplate.groovy)
- [vars/nexusTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/nexusTemplate.groovy)
- [vars/sonarTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/sonarTemplate.groovy)
- [vars/gitlabMavenRegistryTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/gitlabMavenRegistryTemplate.groovy)
- [vars/tomcatDeployTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/tomcatDeployTemplate.groovy)
- [vars/jcloudVault.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/jcloudVault.groovy)
- [vars/jcloudGitOps.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/jcloudGitOps.groovy)
- [vars/jcloudAks.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/jcloudAks.groovy)
- [vars/jcloudArgoCd.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/vars/jcloudArgoCd.groovy)

### Global helpers

- [VaultOps.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/global/VaultOps.groovy)
- [GitOpsOps.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/global/GitOpsOps.groovy)
- [AksOps.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/global/AksOps.groovy)
- [ArgoCdOps.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/global/ArgoCdOps.groovy)

### Template modules

- [JavaMavenTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/JavaMavenTemplate.groovy)
- [DockerTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/DockerTemplate.groovy)
- [GitOpsAksTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/GitOpsAksTemplate.groovy)
- [GitOpsGkeTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/GitOpsGkeTemplate.groovy)
- [HelmTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/HelmTemplate.groovy)
- [NexusTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/NexusTemplate.groovy)
- [SonarTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/SonarTemplate.groovy)
- [GitlabMavenRegistryTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/GitlabMavenRegistryTemplate.groovy)
- [TomcatDeployTemplate.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/templates/TomcatDeployTemplate.groovy)

### Pipeline entrypoint

- [PlatformJavaAksPipeline.groovy](/Users/makutaworldmpm/Desktop/eagunu_2025/jcloudcodes/jenkins/jcloudcodes_template/JavaShared_library/src/org/jcloudcodes/pipelines/PlatformJavaAksPipeline.groovy)

## How app repos consume it

Application repositories should own their own `Jenkinsfile` and call the shared library like this:

```groovy
@Library('jcloudcodes_template') _

platformJavaAksPipeline(
  appName: 'jcloud-springboot-aks-app',
  imageRepository: 'jcloudcodes/jcloud-springboot-aks-app',
  imageTag: env.BUILD_NUMBER,
  gitopsRepoUrl: 'https://git.example.com/your-org/jcloud_argocd.git',
  gitopsBranch: 'main',
  helmValuesFile: 'environments/dev/values.yaml',
  argocdAppManifestFile: 'applications/mss-dev.yaml',
  argocdAppName: 'jcloud-springboot-aks-app',
  argocdServer: 'argocd.example.com',
  kubeNamespace: 'mss-dev',
  aksClusterName: 'sap-dev-aksdemo1',
  vaultAddr: 'https://vault.example.com:8200',
  vaultNamespace: 'admin',
  vaultKvMount: 'kv/jcloudcodes/java-web-app',
  vaultSecretPath: 'jcloudcodes/java-web-app',
  vaultRoleIdCredentialId: 'vault-approle-role-id',
  vaultSecretIdCredentialId: 'vault-approle-secret-id',
  dockerCredentialId: 'dockerhub-creds',
  gitopsRepoTokenCredentialId: 'gitops-repo-token'
)
```

## Why this is production-ready

- `vars/` is now thin and only exposes public entry steps
- real implementation lives in namespaced classes under `src/`
- pipeline orchestration is separated from helper logic
- global helpers, templates, and pipeline classes are all cleanly isolated
- the structure now matches Jenkins shared-library conventions instead of a custom nested layout
