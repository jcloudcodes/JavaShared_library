package org.jcloudcodes.global

class VaultOps implements Serializable {
    private final def steps

    VaultOps(def steps) {
        this.steps = steps
    }

    void withAppRole(Map config, Closure body) {
        steps.withCredentials([
            steps.string(credentialsId: config.vaultRoleIdCredentialId, variable: 'VAULT_ROLE_ID'),
            steps.string(credentialsId: config.vaultSecretIdCredentialId, variable: 'VAULT_SECRET_ID')
        ]) {
            steps.withEnv([
                "VAULT_ADDR=${config.vaultAddr}",
                "VAULT_NAMESPACE=${config.get('vaultNamespace', 'admin')}"
            ]) {
                body.call()
            }
        }
    }

    void withVaultToken(Map config, Closure body) {
        withAppRole(config) {
            def token = steps.sh(
                script: '''
                    vault write -field=token auth/approle/login \
                      role_id="$VAULT_ROLE_ID" \
                      secret_id="$VAULT_SECRET_ID"
                ''',
                returnStdout: true
            ).trim()

            steps.withEnv(["VAULT_TOKEN=${token}"]) {
                body.call()
            }
        }
    }

    String readKvField(Map config, String fieldName) {
        String value = ''
        withVaultToken(config) {
            value = steps.sh(
                script: """
                    vault kv get -mount='${config.vaultKvMount}' \
                      -field='${fieldName}' \
                      '${config.vaultSecretPath}' | tr -d '\\r'
                """,
                returnStdout: true
            ).trim()
        }
        value
    }

    void writeKubeconfig(Map config) {
        withVaultToken(config) {
            steps.sh 'mkdir -p ~/.kube'
            steps.sh(
                script: """
                    vault kv get -mount='${config.vaultKvMount}' \
                      -field='AKS_KUBECONFIG_B64' \
                      '${config.vaultSecretPath}' | base64 -d > ~/.kube/config
                """
            )
            steps.sh 'kubectl config current-context'
        }
    }
}
