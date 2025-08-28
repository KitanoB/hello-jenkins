pipeline {
  agent { label 'Docker-Agent' }
  options {
    skipDefaultCheckout(true)
    timestamps()
  }

  environment {
    REPO_FULL = 'KitanoB/hello-jenkins'
    DEFAULT_BRANCH = 'main'
  }

  stages {
    stage('Checkout (SSH)') {
      steps {
        // Sécurise la clé hôte GitHub (utile sur agents propres)
        sh '''
          mkdir -p ~/.ssh
          ssh-keyscan github.com >> ~/.ssh/known_hosts
        '''

        // Checkout via SSH avec ta clé "kitanoB"
        checkout([
          $class: 'GitSCM',
          branches: [[name: env.BRANCH_NAME ? "*/${env.BRANCH_NAME}" : "*/${env.DEFAULT_BRANCH}"]],
          doGenerateSubmoduleConfigurations: false,
          extensions: [],
          userRemoteConfigs: [[
            url: "git@github.com:${env.REPO_FULL}.git",
            credentialsId: '684bbba1-3125-48c1-99ae-2f09e0c8cff0' 
          ]]
        ])
      }
    }

    stage('Build & Test') {
      steps {
        sh '''
          echo "Building branch=${BRANCH_NAME} PR=${CHANGE_ID:-none} target=${CHANGE_TARGET:-n/a}"
          # ---- Tes commandes réelles ici ----
          echo "OK"
        '''
      }
    }

    stage('PR: announce start (optional)') {
      when { changeRequest() }
      steps {
        withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) { // <-- Secret text PAT
          sh '''
            # Commentaire d'information au démarrage du build PR
            curl -sS -X POST \
              -H "Authorization: Bearer $GITHUB_TOKEN" \
              -H "Accept: application/vnd.github+json" \
              https://api.github.com/repos/${REPO_FULL}/issues/${CHANGE_ID}/comments \
              -d @- <<JSON
            { "body": "🔧 Jenkins started build for commit `${GIT_COMMIT}` on `${BRANCH_NAME}` — ${BUILD_URL}" }
            JSON
          '''
        }
      }
    }
  }

  post {
    success {
      steps {
        withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
          sh '''
            # Commit Status: success
            curl -sS -X POST \
              -H "Authorization: Bearer $GITHUB_TOKEN" \
              -H "Accept: application/vnd.github+json" \
              https://api.github.com/repos/${REPO_FULL}/statuses/${GIT_COMMIT} \
              -d @- <<JSON
            {
              "state": "success",
              "context": "jenkins/ci",
              "description": "Build passed",
              "target_url": "${BUILD_URL}"
            }
            JSON

            # Commentaire PR si c'est une PR
            if [ -n "$CHANGE_ID" ]; then
              curl -sS -X POST \
                -H "Authorization: Bearer $GITHUB_TOKEN" \
                -H "Accept: application/vnd.github+json" \
                https://api.github.com/repos/${REPO_FULL}/issues/${CHANGE_ID}/comments \
                -d @- <<JSON
              { "body": "Build **SUCCESS** — ${BUILD_URL}" }
              JSON
            fi
          '''
        }
      }
    }

    failure {
      steps {
        withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
          sh '''
            # Commit Status: failure
            curl -sS -X POST \
              -H "Authorization: Bearer $GITHUB_TOKEN" \
              -H "Accept: application/vnd.github+json" \
              https://api.github.com/repos/${REPO_FULL}/statuses/${GIT_COMMIT} \
              -d @- <<JSON
            {
              "state": "failure",
              "context": "jenkins/ci",
              "description": "Build failed",
              "target_url": "${BUILD_URL}"
            }
            JSON

            # Commentaire PR si c'est une PR
            if [ -n "$CHANGE_ID" ]; then
              curl -sS -X POST \
                -H "Authorization: Bearer $GITHUB_TOKEN" \
                -H "Accept: application/vnd.github+json" \
                https://api.github.com/repos/${REPO_FULL}/issues/${CHANGE_ID}/comments \
                -d @- <<JSON
              { "body": "Build **FAILED** — ${BUILD_URL}" }
              JSON
            fi
          '''
        }
      }
    }
  }
}
