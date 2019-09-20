pipeline {
    agent {
        kubernetes {
            label 'terradoc'
            defaultContainer 'jnlp'
            inheritFrom 'default'
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: gradle
    image: gradle:5.6.2-jdk8
    command:
    - cat
    tty: true
"""
        }
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install terraform') {
            steps {
                container('gradle') {
                    sh 'TER_VER="0.12.9" && apt-get update && apt-get install -y wget && wget https://releases.hashicorp.com/terraform/${TER_VER}/terraform_${TER_VER}_linux_amd64.zip && unzip terraform_${TER_VER}_linux_amd64.zip && mv terraform /usr/local/bin/'
                }
            }
        }

        stage('Build & Test') {
            steps {
                container('gradle') {
                    sh 'gradle build'
                }
            }
            post {
                always {
                    container('gradle') {
                        junit '**/build/test-results/*/TEST-*.xml'
                    }
                }
            }
        }

        stage('Build Docker') {
            when {
                branch 'develop'
            }
            steps {
                container('docker') {
                    sh "docker build -t ${DOCKER_REPO}terradoc:doc0.0.1-tf0.12.9-8u212-jre-alpine -t ${DOCKER_REPO}terradoc:latest ."
                    sh "docker push ${DOCKER_REPO}terradoc:doc0.0.1-tf0.12.9-8u212-jre-alpine"
                    sh "docker push ${DOCKER_REPO}terradoc:latest"
                }
            }
        }

        stage('Archive Artifacts') {
            when {
                branch 'develop'
            }
            steps {
                container('gralde') {
                    archiveArtifacts artifacts: 'terradoc/build/distributions/*', fingerprint: true, onlyIfSuccessful: true
                }
            }
        }

    }

}