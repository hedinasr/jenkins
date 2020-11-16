pipeline {
    agent {
        docker { image 'maven:3-alpine' }
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/jabedhasan21/java-hello-world-with-maven.git',
                    branch: 'master'
            }
        }
        stage('Test') {
            steps {
                configFileProvider(
                    [configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -X -s "$MAVEN_SETTINGS" clean test'
                }
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }
    }

    post {
        always {
            archiveArtifacts 'target/*.jar'
            deleteDir()
        }
    }
}
