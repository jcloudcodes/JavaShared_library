package org.jcloudcodes.global

class ArgoCdOps implements Serializable {
    private final def steps
    private final VaultOps vaultOps
    private final GitOpsOps gitOpsOps

    ArgoCdOps(def steps) {
        this.steps = steps
        this.vaultOps = new VaultOps(steps)
        this.gitOpsOps = new GitOpsOps(steps)
    }

    void bootstrapApplication(Map config) {
        vaultOps.writeKubeconfig(config)
        gitOpsOps.cloneRepo(config) {
            steps.sh """
                test -f '${config.argocdAppManifestFile}'
                kubectl apply -f '${config.argocdAppManifestFile}'
            """
        }
    }

    void syncApplication(Map config) {
        String username = config.get('argocdUsername', '')
        String password = config.get('argocdPassword', '')

        if (config.argocdCredentialId) {
            steps.withCredentials([
                steps.usernamePassword(
                    credentialsId: config.argocdCredentialId,
                    usernameVariable: 'ARGOCD_USERNAME',
                    passwordVariable: 'ARGOCD_PASSWORD'
                )
            ]) {
                steps.withEnv([
                    "ARGOCD_USERNAME=${steps.env.ARGOCD_USERNAME}",
                    "ARGOCD_PASSWORD=${steps.env.ARGOCD_PASSWORD}"
                ]) {
                    doSync(config)
                }
            }
            return
        }

        if (!username || !password) {
            username = vaultOps.readKvField(config, 'ARGOCD_USERNAME')
            password = vaultOps.readKvField(config, 'ARGOCD_PASSWORD')
        }

        steps.withEnv([
            "ARGOCD_USERNAME=${username}",
            "ARGOCD_PASSWORD=${password}"
        ]) {
            doSync(config)
        }
    }

    private void doSync(Map config) {
        steps.sh """
            argocd login '${config.argocdServer}' \
              --username "$ARGOCD_USERNAME" \
              --password "$ARGOCD_PASSWORD" \
              --insecure \
              --grpc-web
            argocd app get '${config.argocdAppName}' --grpc-web
            argocd app sync '${config.argocdAppName}' --grpc-web
            argocd app wait '${config.argocdAppName}' --health --sync --timeout 600 --grpc-web
        """
    }
}
