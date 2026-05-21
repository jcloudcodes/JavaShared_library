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
            runKubectl(config, "test -f '${config.argocdAppManifestFile}'")
            runKubectl(config, "kubectl apply --validate=false -f '${config.argocdAppManifestFile}'")
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
        runArgoCd(config, "argocd login '${config.argocdServer}' --username \"\$ARGOCD_USERNAME\" --password \"\$ARGOCD_PASSWORD\" --insecure --grpc-web")
        runArgoCd(config, "argocd app get '${config.argocdAppName}' --grpc-web")
        runArgoCd(config, "argocd app sync '${config.argocdAppName}' --grpc-web")
        runArgoCd(config, "argocd app wait '${config.argocdAppName}' --health --sync --timeout 600 --grpc-web")
    }

    private void runKubectl(Map config, String command) {
        String escaped = command.replace("'", "'\"'\"'")
        String kubeDir = config.get('workspaceKubeDir', '.kube')
        steps.sh """
            docker run --rm \
              --network host \
              -v "\$PWD:/workdir" \
              -w /workdir \
              -v "\$PWD/${kubeDir}:/root/.kube" \
              '${config.get('helmKubectlImage', 'dtzar/helm-kubectl:3.19.1')}' \
              sh -lc '${escaped}'
        """
    }

    private void runArgoCd(Map config, String command) {
        String escaped = command.replace("\"", "\\\"")
        steps.sh """
            docker run --rm \
              --network host \
              -e ARGOCD_USERNAME="\$ARGOCD_USERNAME" \
              -e ARGOCD_PASSWORD="\$ARGOCD_PASSWORD" \
              '${config.get('argocdCliImage', 'quay.io/argoproj/argocd:v3.4.1')}' \
              sh -lc "${escaped}"
        """
    }
}
