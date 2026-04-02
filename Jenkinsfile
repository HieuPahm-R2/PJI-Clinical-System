// PJI Advisor - Jenkinsfile
// Pipeline: Push Code → Test → SonarQube → Build Images → Push DockerHub → Deploy


pipeline {
    agent any

    // -------------------------------------------------------------------------
    // Environment variables
    // -------------------------------------------------------------------------
    environment {
        // Docker Hub
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')  // Jenkins credentials ID
        DOCKERHUB_REPO        = 'your-dockerhub-username'             // TODO: Thay bằng username thật

        // SonarQube
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONAR_TOKEN    = credentials('sonarqube-token')

        // Image tags - dùng BUILD_NUMBER để version rõ ràng
        IMAGE_TAG = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"

        // Deploy server
        DEPLOY_HOST       = credentials('deploy-server-host')     // IP hoặc domain của server
        DEPLOY_USER       = credentials('deploy-server-user')     // SSH user
        DEPLOY_SSH_KEY    = credentials('deploy-server-ssh-key')  // SSH private key
        DEPLOY_PATH       = '/opt/pji-advisor'                    // Thư mục chứa docker-compose trên server

        // Notification
        SLACK_WEBHOOK = credentials('slack-webhook-url')
    }

    // -------------------------------------------------------------------------
    // Pipeline options
    // -------------------------------------------------------------------------
    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()               // Không cho chạy 2 build cùng lúc
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    // -------------------------------------------------------------------------
    // Stages
    // -------------------------------------------------------------------------
    stages {

        // =====================================================================
        // STAGE 1: Checkout code
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    echo "Building commit: ${env.GIT_COMMIT_SHORT}"
                }
            }
        }

        // =====================================================================
        // STAGE 2: Run tests song song cho Backend + Frontend + RAG
        // =====================================================================
        stage('Test') {
            parallel {

                // ----- Backend tests (Spring Boot) -----
                stage('Backend Tests') {
                    steps {
                        dir('backend') {
                            sh '''
                                echo "=== Running Spring Boot tests ==="
                                ./mvnw clean test \
                                    -Dspring.profiles.active=test \
                                    -Dmaven.test.failure.ignore=false
                            '''
                        }
                    }
                    post {
                        always {
                            // Publish test results
                            junit(
                                testResults: 'backend/target/surefire-reports/*.xml',
                                allowEmptyResults: true
                            )
                        }
                    }
                }

                // ----- Frontend tests (React/TypeScript) -----
                stage('Frontend Tests') {
                    steps {
                        dir('frontend') {
                            sh '''
                                echo "=== Running Frontend tests ==="
                                npm ci
                                npm run lint
                                npm run test -- --ci --coverage
                            '''
                        }
                    }
                    post {
                        always {
                            publishHTML(target: [
                                reportDir: 'frontend/coverage/lcov-report',
                                reportFiles: 'index.html',
                                reportName: 'Frontend Coverage'
                            ])
                        }
                    }
                }

                // ----- RAG Service tests (FastAPI + Python) -----
                stage('RAG Service Tests') {
                    steps {
                        dir('rag-service') {
                            sh '''
                                echo "=== Running RAG Service tests ==="
                                python -m venv .venv
                                . .venv/bin/activate
                                pip install -r requirements.txt
                                pip install -r requirements-test.txt
                                pytest tests/ \
                                    --tb=short \
                                    --junitxml=test-results.xml \
                                    -v
                            '''
                        }
                    }
                    post {
                        always {
                            junit(
                                testResults: 'rag-service/test-results.xml',
                                allowEmptyResults: true
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // STAGE 3: SonarQube Analysis
        // =====================================================================
        stage('SonarQube Analysis') {
            steps {
                script {
                    // Backend scan (Java)
                    dir('backend') {
                        sh """
                            ./mvnw sonar:sonar \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.token=${SONAR_TOKEN} \
                                -Dsonar.projectKey=pji-backend \
                                -Dsonar.projectName='PJI Backend' \
                                -Dsonar.java.coveragePlugin=jacoco \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }

                    // Frontend scan (TypeScript)
                    dir('frontend') {
                        sh """
                            npx sonar-scanner \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.token=${SONAR_TOKEN} \
                                -Dsonar.projectKey=pji-frontend \
                                -Dsonar.projectName='PJI Frontend' \
                                -Dsonar.sources=src \
                                -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
                        """
                    }

                    // RAG Service scan (Python)
                    dir('rag-service') {
                        sh """
                            . .venv/bin/activate
                            npx sonar-scanner \
                                -Dsonar.host.url=${SONAR_HOST_URL} \
                                -Dsonar.token=${SONAR_TOKEN} \
                                -Dsonar.projectKey=pji-rag-service \
                                -Dsonar.projectName='PJI RAG Service' \
                                -Dsonar.sources=app \
                                -Dsonar.python.version=3.11
                        """
                    }
                }
            }
        }

        // =====================================================================
        // STAGE 4: SonarQube Quality Gate - PHẢI pass mới được build
        // =====================================================================
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo "Quality Gate PASSED - Code đạt chuẩn"
            }
        }

        // =====================================================================
        // STAGE 5: Build Docker Images
        // =====================================================================
        stage('Build Docker Images') {
            parallel {

                stage('Build Backend Image') {
                    steps {
                        dir('backend') {
                            sh """
                                docker build \
                                    -t ${DOCKERHUB_REPO}/pji-backend:${IMAGE_TAG} \
                                    -t ${DOCKERHUB_REPO}/pji-backend:latest \
                                    --build-arg BUILD_NUMBER=${BUILD_NUMBER} \
                                    --build-arg GIT_COMMIT=${GIT_COMMIT_SHORT} \
                                    -f Dockerfile .
                            """
                        }
                    }
                }

                stage('Build Frontend Image') {
                    steps {
                        dir('frontend') {
                            sh """
                                docker build \
                                    -t ${DOCKERHUB_REPO}/pji-frontend:${IMAGE_TAG} \
                                    -t ${DOCKERHUB_REPO}/pji-frontend:latest \
                                    --build-arg BUILD_NUMBER=${BUILD_NUMBER} \
                                    -f Dockerfile .
                            """
                        }
                    }
                }

                stage('Build RAG Service Image') {
                    steps {
                        dir('rag-service') {
                            sh """
                                docker build \
                                    -t ${DOCKERHUB_REPO}/pji-rag-service:${IMAGE_TAG} \
                                    -t ${DOCKERHUB_REPO}/pji-rag-service:latest \
                                    -f Dockerfile .
                            """
                        }
                    }
                }
            }
        }

        // =====================================================================
        // STAGE 6: Push Images to Docker Hub
        // =====================================================================
        stage('Push to Docker Hub') {
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'

                sh """
                    # Push tất cả images với cả 2 tags (version + latest)
                    docker push ${DOCKERHUB_REPO}/pji-backend:${IMAGE_TAG}
                    docker push ${DOCKERHUB_REPO}/pji-backend:latest

                    docker push ${DOCKERHUB_REPO}/pji-frontend:${IMAGE_TAG}
                    docker push ${DOCKERHUB_REPO}/pji-frontend:latest

                    docker push ${DOCKERHUB_REPO}/pji-rag-service:${IMAGE_TAG}
                    docker push ${DOCKERHUB_REPO}/pji-rag-service:latest
                """
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }

        // =====================================================================
        // STAGE 7: Deploy to Staging
        // =====================================================================
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    deployToServer('staging')
                }
            }
        }

        // =====================================================================
        // STAGE 8: Deploy to Production (chỉ branch main, cần approve thủ công)
        // =====================================================================
        stage('Approve Production Deploy') {
            when {
                branch 'main'
            }
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    input(
                        message: "Deploy to PRODUCTION?",
                        ok: "Yes, deploy to production",
                        submitter: "admin,lead-dev"  // Chỉ người có quyền mới approve
                    )
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                script {
                    deployToServer('production')
                }
            }
        }

        // =====================================================================
        // STAGE 9: Smoke Test sau deploy
        // =====================================================================
        stage('Smoke Test') {
            steps {
                script {
                    sh '''
                        echo "=== Running smoke tests ==="
                        sleep 30  # Đợi services khởi động

                        # Test backend health
                        curl -sf http://${DEPLOY_HOST}:8085/actuator/health \
                            || (echo "Backend health check FAILED" && exit 1)

                        # Test frontend accessible
                        curl -sf http://${DEPLOY_HOST} \
                            || (echo "Frontend health check FAILED" && exit 1)

                        # Test RAG service
                        curl -sf http://${DEPLOY_HOST}:8000/health \
                            || (echo "RAG service health check FAILED" && exit 1)

                        echo "=== All smoke tests PASSED ==="
                    '''
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Post actions
    // -------------------------------------------------------------------------
    post {
        success {
            script {
                notifySlack('SUCCESS', 'good')
            }
        }
        failure {
            script {
                notifySlack('FAILED', 'danger')
            }
        }
        always {
            // Dọn dẹp Docker images cũ trên Jenkins agent
            sh """
                docker rmi ${DOCKERHUB_REPO}/pji-backend:${IMAGE_TAG} || true
                docker rmi ${DOCKERHUB_REPO}/pji-frontend:${IMAGE_TAG} || true
                docker rmi ${DOCKERHUB_REPO}/pji-rag-service:${IMAGE_TAG} || true
            """
            cleanWs()  // Xóa workspace
        }
    }
}

// =============================================================================
// Helper functions
// =============================================================================

def deployToServer(String environment) {
    echo "=== Deploying to ${environment} ==="

    sshagent(credentials: ['deploy-server-ssh-key']) {
        sh """
            ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} << 'ENDSSH'
                cd ${DEPLOY_PATH}

                # Pull images mới
                docker compose pull

                # Deploy với zero-downtime (restart từng service)
                # Infrastructure services (DB, Redis, RabbitMQ) không restart
                docker compose up -d --no-deps pji-backend
                docker compose up -d --no-deps pji-frontend
                docker compose up -d --no-deps pji-rag-service

                # Verify containers running
                docker compose ps

                # Xóa images cũ không dùng
                docker image prune -f
ENDSSH
        """
    }
}

def notifySlack(String status, String color) {
    def message = """
        *PJI Advisor - Build ${status}*
        Branch: `${env.BRANCH_NAME}`
        Commit: `${env.GIT_COMMIT_SHORT}`
        Build: #${env.BUILD_NUMBER}
        Duration: ${currentBuild.durationString}
        <${env.BUILD_URL}|View Build>
    """.stripIndent()

    sh """
        curl -X POST ${SLACK_WEBHOOK} \
            -H 'Content-type: application/json' \
            -d '{"attachments":[{"color":"${color}","text":"${message}"}]}'
    """
}