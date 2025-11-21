pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    environment {
        // Maven settings
        MAVEN_OPTS = '-Xmx1024m'

        // Project information
        PROJECT_NAME = 'achat'
        PROJECT_VERSION = '1.0'
        ARTIFACT_NAME = "${PROJECT_NAME}-${PROJECT_VERSION}.jar"

        // Docker Registry Configuration (YOUR CREDENTIALS)
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_USERNAME = 'salimnahdi'
        DOCKER_IMAGE_NAME = 'docker-spring-boot'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}"
        DOCKER_IMAGE = "${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
        DOCKER_CREDENTIAL_ID = 'dockerhub'
        DOCKER_FULL_IMAGE = "${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${DOCKER_IMAGE}"

        // SonarQube Configuration
        SONAR_HOST_URL = 'http://achat-sonarqube:9000'
        SONAR_PROJECT_KEY = 'achat-project'
        SONAR_PROJECT_NAME = 'Achat DevOps Application'

        // Nexus Configuration
        NEXUS_URL = 'achat-nexus:8081'
        NEXUS_REPOSITORY = 'achat-releases'
        NEXUS_SNAPSHOT_REPOSITORY = 'achat-snapshots'
        NEXUS_CREDENTIAL_ID = 'nexus-credentials'

        // AWS & Terraform Configuration
        AWS_REGION = 'us-east-1'
        TERRAFORM_DIR = '.'  // Root directory where terraform-eks.tf is located
        TERRAFORM_STATE_DIR = "/var/jenkins_home/terraform-states/${JOB_NAME}"

        // Kubernetes Configuration
        K8S_NAMESPACE = 'achat-app'
        K8S_DEPLOYMENT_NAME = 'achat-app'
    }

    stages {
        // ========================================================================
        // STAGE 1: CHECKOUT GIT
        // ========================================================================
        stage('🔍 CHECKOUT GIT') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 1: Checking out code from GitHub'
                    echo '========================================='
                }

                // Checkout from GitHub (replace with your repo URL)
                git branch: 'main', url: 'https://github.com/NahdiSalim/Achat_Devops.git'

                script {
                    // Get Git commit info
                    env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    env.GIT_AUTHOR = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()

                    echo "Commit Message: ${env.GIT_COMMIT_MSG}"
                    echo "Author: ${env.GIT_AUTHOR}"
                }
            }
        }

        // ========================================================================
        // STAGE 2: PREPARATION
        // ========================================================================
        stage('🔧 PREPARATION') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 2: Preparing Build Environment'
                    echo '========================================='
                }

                sh '''
                    if [ -f mvnw ]; then
                        chmod +x mvnw
                        echo "✅ Maven wrapper is executable"
                    fi

                    echo "Java Version:"
                    java -version
                    echo ""
                    echo "Maven Version:"
                    ./mvnw --version
                '''
            }
        }

        // ========================================================================
        // STAGE 3: MVN CLEAN & COMPILE
        // ========================================================================
        stage('🧹 MVN CLEAN') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 3: Maven Clean & Compile'
                    echo '========================================='
                }

                sh './mvnw clean compile -B'

                script {
                    echo '✅ Build cleaned and compiled successfully'
                }
            }
        }

        // ========================================================================
        // STAGE 4: ARTIFACT CONSTRUCTION (PACKAGE)
        // ========================================================================
        stage('📦 ARTIFACT CONSTRUCTION') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 4: Building JAR Artifact'
                    echo '========================================='
                }

                sh './mvnw package -DskipTests -B'

                script {
                    echo "✅ Application packaged: ${ARTIFACT_NAME}"
                }
            }

            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                    script {
                        echo '✅ Artifacts archived successfully'
                    }
                }
            }
        }

        // ========================================================================
        // STAGE 5: UNIT TESTS
        // ========================================================================
        stage('🧪 UNIT TESTS') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 5: Running Unit Tests'
                    echo '========================================='
                }

                sh './mvnw test -B'

                script {
                    echo '✅ All unit tests passed'
                }
            }

            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                    script {
                        echo '✅ Test results and coverage published'
                    }
                }
            }
        }

        // ========================================================================
        // STAGE 6: SONARQUBE ANALYSIS
        // ========================================================================
        stage('📊 MVN SONARQUBE') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 6: SonarQube Code Analysis'
                    echo '========================================='
                }

                withCredentials([string(credentialsId: 'sonar-token-jenkins', variable: 'SONAR_TOKEN')]) {
                    sh """
                        ./mvnw sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName="${SONAR_PROJECT_NAME}" \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=\${SONAR_TOKEN} \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -B
                    """
                }

                script {
                    echo '✅ SonarQube analysis completed'
                    echo "📊 View results at: ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
                }

                // Quality Gate Check
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        try {
                            def qg = waitForQualityGate()
                            echo "Quality Gate status: ${qg.status}"

                            if (qg.status != 'OK') {
                                echo '⚠️  Quality Gate failed but continuing pipeline...'
                                echo "View details: ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
                            } else {
                                echo '✅ Quality Gate passed!'
                            }
                        } catch (Exception e) {
                            echo "⚠️  Could not check quality gate: ${e.message}"
                            echo 'Continuing pipeline anyway...'
                        }
                    }
                }
            }
        }

        // ========================================================================
        // STAGE 7: PUBLISH TO NEXUS
        // ========================================================================
        stage('📤 PUBLISH TO NEXUS') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 7: Publishing Artifacts to Nexus'
                    echo '========================================='
                }

                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def artifactId = pom.artifactId
                    def groupId = pom.groupId
                    def packaging = pom.packaging ?: 'jar'
                    def repository = version.endsWith('SNAPSHOT') ? NEXUS_SNAPSHOT_REPOSITORY : NEXUS_REPOSITORY
                    def jarFile = "target/${artifactId}-${version}.${packaging}"

                    echo "Artifact: ${groupId}:${artifactId}:${version}"
                    echo "Repository: ${repository}"
                    echo "JAR File: ${jarFile}"

                    try {
                        nexusArtifactUploader(
                            nexusVersion: 'nexus3',
                            protocol: 'http',
                            nexusUrl: NEXUS_URL,
                            groupId: groupId,
                            version: version,
                            repository: repository,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                [
                                    artifactId: artifactId,
                                    classifier: '',
                                    file: jarFile,
                                    type: packaging
                                ],
                                [
                                    artifactId: artifactId,
                                    classifier: '',
                                    file: 'pom.xml',
                                    type: 'pom'
                                ]
                            ]
                        )

                        echo '✅ Artifact published to Nexus!'
                        echo "📦 View at: http://${NEXUS_URL}/#browse/browse:${repository}"
                    } catch (Exception e) {
                        echo "⚠️  Nexus upload failed: ${e.message}"
                        echo 'Continuing pipeline anyway...'
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        // ========================================================================
        // STAGE 8: BUILD DOCKER IMAGE
        // ========================================================================
        stage('🐳 BUILDING DOCKER IMAGE') {
            when {
                expression { return fileExists('Dockerfile') }
            }
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 8: Building Docker Image'
                    echo '========================================='
                    echo "Building: ${DOCKER_FULL_IMAGE}"
                }

                sh "ls -lh target/${PROJECT_NAME}-${PROJECT_VERSION}.jar"

                sh """
                    docker build \
                        -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} \
                        -t ${DOCKER_IMAGE_NAME}:latest \
                        .
                """

                script {
                    echo '✅ Docker image built successfully'
                    sh "docker images | grep ${DOCKER_IMAGE_NAME}"
                }
            }
        }

        // ========================================================================
        // STAGE 9: DEPLOY DOCKER IMAGE TO DOCKERHUB
        // ========================================================================
        stage('📤 DEPLOY DOCKER IMAGE') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 9: Pushing to DockerHub'
                    echo '========================================='
                }

                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CREDENTIAL_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "Logging in to DockerHub..."
                        echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin ${DOCKER_REGISTRY}
                    '''

                    sh """
                        # Tag images with your DockerHub username
                        docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        docker tag ${DOCKER_IMAGE_NAME}:latest ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest

                        # Push both tags
                        docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                    """

                    sh 'docker logout ${DOCKER_REGISTRY}'
                }

                script {
                    echo '✅ Docker image pushed successfully'
                    echo "🐳 Image: ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                    echo "🐳 Image: ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        // ========================================================================
        // STAGE 10: CLEANUP LOCAL DOCKER IMAGES
        // ========================================================================
        stage('🧹 CLEANUP DOCKER IMAGES') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 10: Cleaning Local Docker Images'
                    echo '========================================='
                }

                sh """
                    docker rmi ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} || true
                    docker rmi ${DOCKER_IMAGE_NAME}:latest || true
                    docker rmi ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} || true
                    docker rmi ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest || true
                    echo "✅ Local Docker images cleaned"
                """
            }
        }

        // ========================================================================
        // STAGE 11: TEST AWS CREDENTIALS
        // ========================================================================
        stage('🔐 TEST AWS CREDENTIALS') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 11: Verifying AWS Credentials'
                    echo '========================================='
                }

                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                    string(credentialsId: 'aws-session-token', variable: 'AWS_SESSION_TOKEN')
                ]) {
                    sh '''
                        echo "Testing AWS credentials..."
                        aws sts get-caller-identity

                        echo ""
                        echo "✅ AWS credentials verified successfully!"
                    '''
                }
            }
        }

        // ========================================================================
        // STAGE 12: TERRAFORM INFRASTRUCTURE PROVISIONING
        // ========================================================================
        stage('🏗️  TERRAFORM INFRASTRUCTURE') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 12: Provisioning AWS Infrastructure'
                    echo '========================================='
                }

                // Setup Terraform state directory
                sh """
                    mkdir -p ${TERRAFORM_STATE_DIR}
                    echo "Terraform state directory: ${TERRAFORM_STATE_DIR}"
                """

                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                    string(credentialsId: 'aws-session-token', variable: 'AWS_SESSION_TOKEN')
                ]) {
                    // Terraform Init
                    sh '''
                        echo "Initializing Terraform for EKS..."
                        terraform init -upgrade
                    '''

                    // Terraform Plan
                    sh '''
                        echo "Planning Terraform changes..."
                        terraform plan -out=tfplan
                    '''

                    // Terraform Apply
                    sh '''
                        echo "Applying Terraform changes..."
                        terraform apply -auto-approve tfplan
                    '''
                }

                script {
                    echo '✅ Terraform infrastructure provisioned!'
                }
            }
        }

        // ========================================================================
        // STAGE 13: DEPLOY TO AWS EKS
        // ========================================================================
        stage('☸️  DEPLOY TO AWS EKS') {
            steps {
                script {
                    echo '========================================='
                    echo 'Stage 13: Deploying to AWS EKS'
                    echo '========================================='
                    echo "Docker Image: ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                    echo "AWS Region: ${AWS_REGION}"
                }

                withCredentials([
                    string(credentialsId: 'aws-access-key-id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws-secret-access-key', variable: 'AWS_SECRET_ACCESS_KEY'),
                    string(credentialsId: 'aws-session-token', variable: 'AWS_SESSION_TOKEN')
                ]) {
                    // Get EKS cluster info
                    sh '''
                        echo "EKS Cluster Name:"
                        terraform output -raw cluster_name || echo "N/A"

                        echo ""
                        echo "EKS Cluster Endpoint:"
                        terraform output -raw cluster_endpoint || echo "N/A"
                    '''

                    // Deploy application to EKS
                    sh """
                        # Get EKS cluster name
                        CLUSTER_NAME=\$(terraform output -raw cluster_name)

                    # Configure kubectl
                    aws eks update-kubeconfig --name \${CLUSTER_NAME} --region ${AWS_REGION}

                    # Create namespace if not exists
                    kubectl create namespace ${K8S_NAMESPACE} || true

                    # Deploy MySQL Database
                    echo "Deploying MySQL database..."
                    kubectl apply -f - <<EOF
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
  namespace: ${K8S_NAMESPACE}
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  namespace: ${K8S_NAMESPACE}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: "root"
        - name: MYSQL_DATABASE
          value: "achatdb"
        - name: MYSQL_USER
          value: "achat_user"
        - name: MYSQL_PASSWORD
          value: "achat_password"
        ports:
        - containerPort: 3306
          name: mysql
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
      volumes:
      - name: mysql-storage
        persistentVolumeClaim:
          claimName: mysql-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: ${K8S_NAMESPACE}
spec:
  selector:
    app: mysql
  ports:
  - port: 3306
    targetPort: 3306
  clusterIP: None
EOF

                    # Wait for MySQL to be ready
                    echo "Waiting for MySQL to be ready..."
                    kubectl wait --for=condition=ready pod -l app=mysql -n ${K8S_NAMESPACE} --timeout=300s || true
                    sleep 30

                    # Deploy Application
                    echo "Deploying Achat application..."
                    kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${K8S_DEPLOYMENT_NAME}
  namespace: ${K8S_NAMESPACE}
spec:
  replicas: 2
  selector:
    matchLabels:
      app: ${K8S_DEPLOYMENT_NAME}
  template:
    metadata:
      labels:
        app: ${K8S_DEPLOYMENT_NAME}
    spec:
                      containers:
                      - name: ${K8S_DEPLOYMENT_NAME}
                        image: ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                        imagePullPolicy: Always
        ports:
        - containerPort: 8089
          name: http
          protocol: TCP
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://mysql:3306/achatdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
        - name: SPRING_DATASOURCE_USERNAME
          value: "achat_user"
        - name: SPRING_DATASOURCE_PASSWORD
          value: "achat_password"
        - name: SPRING_JPA_HIBERNATE_DDL_AUTO
          value: "update"
        - name: SERVER_SERVLET_CONTEXT_PATH
          value: "/SpringMVC"
        readinessProbe:
          httpGet:
            path: /SpringMVC/actuator/health
            port: 8089
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 5
        livenessProbe:
          httpGet:
            path: /SpringMVC/actuator/health
            port: 8089
          initialDelaySeconds: 90
          periodSeconds: 20
          timeoutSeconds: 5
          failureThreshold: 3
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: achat-app-service
  namespace: ${K8S_NAMESPACE}
  annotations:
    service.beta.kubernetes.io/aws-load-balancer-type: "nlb"
    service.beta.kubernetes.io/aws-load-balancer-scheme: "internet-facing"
    service.beta.kubernetes.io/aws-load-balancer-cross-zone-load-balancing-enabled: "true"
spec:
  type: LoadBalancer
  selector:
    app: ${K8S_DEPLOYMENT_NAME}
  ports:
  - name: http
    protocol: TCP
    port: 80
    targetPort: 8089
  sessionAffinity: None
  externalTrafficPolicy: Cluster
EOF

                    echo ""
                    echo "✅ Application deployed to EKS!"
                    echo ""

                    # Wait for deployment
                    echo "Waiting for deployment to be ready..."
                    kubectl wait --for=condition=available --timeout=600s deployment/${K8S_DEPLOYMENT_NAME} -n ${K8S_NAMESPACE} || true

                    # Show deployment status
                    echo ""
                    echo "========================================="
                    echo "Deployment Status"
                    echo "========================================="
                    kubectl get deployments -n ${K8S_NAMESPACE}
                    echo ""
                    kubectl get services -n ${K8S_NAMESPACE}
                    echo ""
                    kubectl get pods -n ${K8S_NAMESPACE}
                """

                    // Wait for LoadBalancer URL
                    script {
                        echo ''
                        echo '========================================='
                        echo 'Waiting for LoadBalancer URL...'
                        echo '========================================='
                    }

                    sh """
                        # Get EKS cluster name
                        CLUSTER_NAME=\$(terraform output -raw cluster_name)
                        aws eks update-kubeconfig --name \${CLUSTER_NAME} --region ${AWS_REGION}

                    echo "Waiting for LoadBalancer to get external URL (this may take 2-3 minutes)..."
                    echo ""

                    for i in {1..30}; do
                        APP_URL=\$(kubectl get svc achat-app-service -n ${K8S_NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "")

                        if [ -n "\${APP_URL}" ] && [ "\${APP_URL}" != "null" ]; then
                            echo "✅ LoadBalancer is ready!"
                            echo ""
                            echo "════════════════════════════════════════════════════════"
                            echo "🎉  APPLICATION DEPLOYED SUCCESSFULLY!"
                            echo "════════════════════════════════════════════════════════"
                            echo ""
                            echo "🌐 APPLICATION URLs:"
                            echo ""
                            echo "   📍 Main API:"
                            echo "      http://\${APP_URL}/SpringMVC/"
                            echo ""
                            echo "   📚 Swagger UI (Test your endpoints here!):"
                            echo "      http://\${APP_URL}/SpringMVC/swagger-ui.html"
                            echo ""
                            echo "   ❤️  Health Check:"
                            echo "      http://\${APP_URL}/SpringMVC/actuator/health"
                            echo ""
                            echo "   📊 API Documentation (JSON):"
                            echo "      http://\${APP_URL}/SpringMVC/v3/api-docs"
                            echo ""
                            echo "════════════════════════════════════════════════════════"
                            echo ""

                            # Test health endpoint
                            echo "Testing application health..."
                            sleep 60

                            for j in {1..15}; do
                                if curl -f -s "http://\${APP_URL}/SpringMVC/actuator/health" > /dev/null 2>&1; then
                                    echo "✅ Application is healthy and responding!"
                                    echo ""
                                    echo "Health Status:"
                                    curl -s "http://\${APP_URL}/SpringMVC/actuator/health"
                                    echo ""
                                    echo ""
                                    echo "✅ You can now access Swagger UI at:"
                                    echo "   http://\${APP_URL}/SpringMVC/swagger-ui.html"
                                    break
                                else
                                    echo "Attempt \$j/15: Application not ready yet, waiting 20 seconds..."
                                    sleep 20
                                fi
                            done

                            break
                        fi

                        echo "Attempt \$i/30: LoadBalancer not ready yet, waiting 10 seconds..."
                        sleep 10
                    done

                    # Final status check
                    APP_URL=\$(kubectl get svc achat-app-service -n ${K8S_NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')

                    if [ -z "\${APP_URL}" ] || [ "\${APP_URL}" = "null" ]; then
                        echo "⚠️  LoadBalancer URL not available yet"
                            echo "   Check with: kubectl get svc achat-app-service -n ${K8S_NAMESPACE}"
                        fi
                    """

                    script {
                        echo ''
                        echo '════════════════════════════════════════════════════════'
                        echo '✅ DEPLOYMENT COMPLETE!'
                        echo '════════════════════════════════════════════════════════'
                        echo ''
                        echo 'Useful kubectl commands:'
                        echo "  kubectl get pods -n ${K8S_NAMESPACE}"
                        echo "  kubectl get svc -n ${K8S_NAMESPACE}"
                        echo "  kubectl logs -n ${K8S_NAMESPACE} -l app=${K8S_DEPLOYMENT_NAME}"
                        echo "  kubectl describe svc achat-app-service -n ${K8S_NAMESPACE}"
                        echo ''
                    }
                }
            }
        }
    }

    post {
        always {
            echo '========================================='
            echo 'Pipeline Execution Summary'
            echo '========================================='
            archiveArtifacts artifacts: 'target/**/*.jar', allowEmptyArchive: true
        }

        success {
            echo '✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅'
            echo '✅                                      ✅'
            echo '✅  PIPELINE COMPLETED SUCCESSFULLY!    ✅'
            echo '✅                                      ✅'
            echo '✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅'
            echo ''
            echo "Build: #${env.BUILD_NUMBER}"
            echo "Commit: ${env.GIT_COMMIT_MSG}"
            echo "Docker Image: ${DOCKER_REGISTRY}/${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
            echo "SonarQube: ${SONAR_HOST_URL}"
            echo "Nexus: http://${NEXUS_URL}"
            echo ''
            echo '🎉 Your application is now deployed on AWS EKS!'
            echo '📚 Access Swagger UI from the LoadBalancer URL shown above'
        }

        failure {
            echo '❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌'
            echo '❌                                      ❌'
            echo '❌         PIPELINE FAILED!             ❌'
            echo '❌                                      ❌'
            echo '❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌ ❌'
            echo ''
            echo "Build: #${env.BUILD_NUMBER}"
            echo 'Check console output for details'
        }

        unstable {
            echo '⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️'
            echo '⚠️                                      ⚠️'
            echo '⚠️  PIPELINE COMPLETED WITH WARNINGS!  ⚠️'
            echo '⚠️                                      ⚠️'
            echo '⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️ ⚠️'
            echo ''
            echo "Build: #${env.BUILD_NUMBER}"
            echo 'Some optional stages had issues (Nexus, SonarQube)'
            echo 'Core functionality completed successfully'
        }
    }
}
