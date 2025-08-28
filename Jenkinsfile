pipeline {
    agent any
    stages {
        stage('Checkout and Set PR Status') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
                        git url: 'https://github.com/KitanoB/hello-jenkins.git', credentialsId: 'github'
                    }
                }
            }
        }
    }
}

