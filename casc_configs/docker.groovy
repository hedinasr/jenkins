pipelineJob('Docker Pipeline') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/hedinasr/jenkins.git')
                    }
                }
                scriptPath('examples/pipeline-docker.groovy')
            }
        }
    }
}
