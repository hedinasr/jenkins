def job(String name = 'BUILD', String command = 'clean install') {
    return {
        stage("${name}") {
            if (params.ACTION == name) {
                configFileProvider([configFile(fileId: 'maven-settings',
                                               variable: 'MAVEN_SETTINGS')]) {
                    sshagent(['gitlab']) {
                        sh "mvn -s \"$MAVEN_SETTINGS\" ${command}"
                    }
                }
            }
        }
    }
}

def call() {
    pipeline {
        agent any

        tools {
            maven 'maven'
        }

        environment {
            HOTFIX_VERSION = sh(script: 'git branch -r | grep hotfix | cut -d "/" -f3', returnStdout: true).trim()
        }

        stages {
            stage('GitFlow') {
                steps {
                    script {
                        job('BUILD').call()
                        job('RELEASE_START', '-B gitflow:release-start').call()
                        job('RELEASE_FINISH', '-B gitflow:release-finish').call()
                        job('HOTFIX_START', '-B gitflow:hotfix-start').call()
                        job('HOTFIX_FINISH', '-B gitflow:hotfix-finish -DhotfixVersion=$HOTFIX_VERSION').call()
                    }
                }
            }
        }

        post {
            success {
                deleteDir()
                cleanWs()
            }
        }
    }
}
