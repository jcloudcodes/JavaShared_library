package org.jcloudcodes.templates

class GitOpsGkeTemplate implements Serializable {
    private final def steps

    GitOpsGkeTemplate(def steps) {
        this.steps = steps
    }

    void update(Map config) {
        steps.error('GKE GitOps template is not implemented yet in this Jenkins template')
    }

    void refreshVaultToken(Map config) {
        steps.error('GKE Vault refresh is not implemented yet in this Jenkins template')
    }

    void bootstrapApp(Map config) {
        steps.error('GKE Argo CD bootstrap is not implemented yet in this Jenkins template')
    }

    void sync(Map config) {
        steps.error('GKE Argo CD sync is not implemented yet in this Jenkins template')
    }

    void verify(Map config) {
        steps.error('GKE verification is not implemented yet in this Jenkins template')
    }
}
