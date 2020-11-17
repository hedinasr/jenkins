def call() {
    pipeline {
        agent any

        tools {
            maven 'maven'
        }

        stages {
            stage('Build') {
                when { equals expected: 'BUILD', actual: params.ACTION }

                steps {
                    configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                        sh 'mvn -s "$MAVEN_SETTINGS" clean install'
                    }
                }
            }

            stage('Release Start') {
                when { equals expected: 'RELEASE_START', actual: params.ACTION }

                steps {
                    configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                        sshagent(['gitlab']) {
                            sh 'mvn -s "$MAVEN_SETTINGS" -B gitflow:release-start'
                        }
                    }
                }
            }

            stage('Release Finish') {
                when { equals expected: 'RELEASE_FINISH', actual: params.ACTION }

                steps {
                    configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                        sshagent(['gitlab']) {
                            sh 'mvn -s "$MAVEN_SETTINGS" -B gitflow:release-finish'
                        }
                    }
                }
            }

            stage('Hotfix Start') {
                when { equals expected: 'HOTFIX_START', actual: params.ACTION }

                steps {
                    configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                        sshagent(['gitlab']) {
                            sh 'mvn -s "$MAVEN_SETTINGS" -B gitflow:hotfix-start'
                        }
                    }
                }
            }

            stage('Hotfix Finish') {
                when { equals expected: 'HOTFIX_FINISH', actual: params.ACTION }

                environment {
                    HOTFIX_VERSION = sh(script: 'git branch -r | grep hotfix | cut -d "/" -f3', returnStdout: true).trim()
                }

                steps {
                    configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                        sshagent(['gitlab']) {
                            git branch: "hotfix/$HOTFIX_VERSION", url: "$GIT_URL", credentialsId: "gitlab"
                            sh 'mvn -s "$MAVEN_SETTINGS" -B gitflow:hotfix-finish -DhotfixVersion=$HOTFIX_VERSION'
                        }
                    }
                }
            }
        }

        post {
            always {
                deleteDir()
                cleanWs()
            }
        }
    }
}
