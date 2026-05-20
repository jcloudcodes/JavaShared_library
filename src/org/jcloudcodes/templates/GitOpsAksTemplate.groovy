package org.jcloudcodes.templates

import org.jcloudcodes.global.AksOps
import org.jcloudcodes.global.ArgoCdOps
import org.jcloudcodes.global.GitOpsOps

class GitOpsAksTemplate implements Serializable {
    private final GitOpsOps gitOpsOps
    private final AksOps aksOps
    private final ArgoCdOps argoCdOps

    GitOpsAksTemplate(def steps) {
        this.gitOpsOps = new GitOpsOps(steps)
        this.aksOps = new AksOps(steps)
        this.argoCdOps = new ArgoCdOps(steps)
    }

    void update(Map config) {
        gitOpsOps.updateImageValues(config)
    }

    void refreshVaultToken(Map config) {
        aksOps.refreshEsoVaultToken(config)
    }

    void bootstrapApp(Map config) {
        argoCdOps.bootstrapApplication(config)
    }

    void sync(Map config) {
        argoCdOps.syncApplication(config)
    }

    void verify(Map config) {
        aksOps.verifyNamespace(config)
    }
}
