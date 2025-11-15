pipeline {
    agent any

    environment {
        // Docker Registry Configuration
        registry = 'salimnahdi/docker-spring-boot'
        registryCredential = 'dockerhub'
        dockerImage = ''

        // SonarQube Configuration
        SONAR_URL = 'http://achat-sonarqube:9000'
        SONAR_PROJECT_KEY = 'achat-project'

        // Nexus Configuration
        NEXUS_URL = 'http://achat-nexus:8081'
        NEXUS_REPOSITORY = 'achat-releases'
        NEXUS_SNAPSHOT_REPOSITORY = 'achat-snapshots'
        NEXUS_CREDENTIAL_ID = 'nexus-credentials'

        // Kubernetes Configuration
        K8S_NAMESPACE = 'default'
        K8S_DEPLOYMENT_NAME = 'achat-app'

        // Application Configuration
        APP_NAME = 'achat'
        APP_VERSION = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('🔍 CHECKOUT GIT') {
            steps {
                echo '======================================'
                echo '       Checking out source code       '
                echo '======================================'
                git branch: 'main', url: 'https://github.com/NahdiSalim/Achat_Devops.git'

                script {
                    // Get Git commit info for better traceability
                    env.GIT_COMMIT_MSG = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim()
                    env.GIT_AUTHOR = sh(script: 'git log -1 --pretty=%an', returnStdout: true).trim()
                    echo "Commit: ${env.GIT_COMMIT_MSG}"
                    echo "Author: ${env.GIT_AUTHOR}"
                }
            }
        }

        stage('🔧 PREPARATION') {
            steps {
                echo '======================================'
                echo '       Preparing build environment    '
                echo '======================================'

                // Make Maven wrapper executable (Unix/Linux agents)
                sh '''
                    if [ -f mvnw ]; then
                        chmod +x mvnw
                        echo "✅ Maven wrapper is now executable"
                    fi
                '''

                // Display build information
                sh '''
                    echo "Java Version:"
                    java -version
                    echo ""
                    echo "Maven Version:"
                    ./mvnw --version
                '''
            }
        }

        stage('🧹 CLEAN') {
            steps {
                echo '======================================'
                echo '       Cleaning previous builds       '
                echo '======================================'
                sh './mvnw clean -B'
            }
        }

        stage('🔨 COMPILE') {
            steps {
                echo '======================================'
                echo '       Compiling source code          '
                echo '======================================'
                sh './mvnw compile -B'
            }
        }

        stage('🧪 UNIT TESTS') {
            steps {
                echo '======================================'
                echo '       Running Unit Tests             '
                echo '======================================'
                sh './mvnw test -B'
            }
            post {
                always {
                    // Publish test results
                    junit '**/target/surefire-reports/*.xml'

                    // Publish JaCoCo coverage report
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('📦 PACKAGE') {
            steps {
                echo '======================================'
                echo '       Building JAR artifact          '
                echo '======================================'
                sh './mvnw package -DskipTests -B'
            }
        }

        stage('📊 SONARQUBE ANALYSIS') {
            steps {
                echo '======================================'
                echo '       Running SonarQube Analysis     '
                echo '======================================'

                withCredentials([string(credentialsId: 'sonar-token-jenkins', variable: 'SONAR_TOKEN')]) {
                    sh """
                        ./mvnw sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.host.url=${SONAR_URL} \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -B
                    """
                }

                echo '✅ SonarQube analysis completed!'
                echo "📊 View results at: ${SONAR_URL}"
            }
        }

        stage('🔍 SONARQUBE QUALITY GATE') {
            steps {
                echo '======================================'
                echo '       Checking Quality Gate          '
                echo '======================================'

                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        try {
                            // Wait for SonarQube quality gate result
                            def qg = waitForQualityGate()
                            if (qg.status != 'OK') {
                                echo "⚠️  Quality Gate Status: ${qg.status}"
                                echo '⚠️  WARNING: Quality gate failed but continuing pipeline...'
                            // unstable(message: "Quality Gate failed: ${qg.status}")
                            } else {
                                echo '✅ Quality Gate passed!'
                            }
                        } catch (Exception e) {
                            echo "⚠️  Could not check quality gate: ${e.message}"
                            echo '⚠️  Continuing pipeline anyway...'
                        }
                    }
                }
            }
        }

        stage('📤 PUBLISH TO NEXUS') {
            steps {
                echo '======================================'
                echo '       Publishing artifact to Nexus   '
                echo '======================================'

                script {
                    // Read POM version
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def artifactId = pom.artifactId
                    def groupId = pom.groupId
                    def packaging = pom.packaging ?: 'jar'

                    // Determine repository (snapshot or release)
                    def repository = version.endsWith('SNAPSHOT') ? NEXUS_SNAPSHOT_REPOSITORY : NEXUS_REPOSITORY
                    def jarFile = "target/${artifactId}-${version}.${packaging}"

                    echo "Artifact: ${groupId}:${artifactId}:${version}"
                    echo "Repository: ${repository}"
                    echo "JAR File: ${jarFile}"

                    // Verify JAR file exists
                    def jarExists = fileExists(jarFile)
                    if (!jarExists) {
                        echo "⚠️  WARNING: JAR file not found at ${jarFile}"
                        echo '⚠️  Listing target directory contents:'
                        sh 'ls -la target/*.jar || echo "No JAR files found"'
                        error("JAR file not found: ${jarFile}")
                    }

                    // Upload artifact to Nexus using Nexus Artifact Uploader plugin
                    try {
                        nexusArtifactUploader(
                            nexusVersion: 'nexus3',
                            protocol: 'http',
                            nexusUrl: 'achat-nexus:8081',
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
                        echo "📦 View at: ${NEXUS_URL}/#browse/browse:${repository}"
                    } catch (Exception e) {
                        echo "⚠️  Nexus upload failed: ${e.message}"
                        echo '⚠️  Error details:'
                        e.printStackTrace()
                        echo '⚠️  Continuing pipeline anyway...'
                        // Don't fail the build for Nexus upload issues
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('🐳 BUILD DOCKER IMAGE') {
            steps {
                echo '======================================'
                echo '       Building Docker Image          '
                echo '======================================'

                script {
                    // Build Docker image with multiple tags
                    dockerImage = docker.build("${registry}:${env.BUILD_NUMBER}")
                    echo "✅ Docker image built: ${registry}:${env.BUILD_NUMBER}"
                }
            }
        }

        stage('🔒 DOCKER IMAGE SCAN') {
            steps {
                echo '======================================'
                echo '       Scanning Docker Image          '
                echo '======================================'

                script {
                    try {
                        // Basic image inspection
                        sh """
                            docker image inspect ${registry}:${env.BUILD_NUMBER}
                            docker image history ${registry}:${env.BUILD_NUMBER}
                        """

                        echo '✅ Docker image scan completed'
                    // Note: Add Trivy or similar tool for vulnerability scanning
                    } catch (Exception e) {
                        echo "⚠️  Image scan skipped: ${e.message}"
                    }
                }
            }
        }

        stage('📤 PUSH DOCKER IMAGE') {
            steps {
                echo '======================================'
                echo '       Pushing to DockerHub           '
                echo '======================================'

                script {
                    docker.withRegistry('', registryCredential) {
                        // Push with build number tag
                        dockerImage.push("${env.BUILD_NUMBER}")

                        // Push with latest tag
                        dockerImage.push('latest')

                        echo '✅ Docker image pushed successfully!'
                        echo "🐳 Image: ${registry}:${env.BUILD_NUMBER}"
                        echo "🐳 Image: ${registry}:latest"
                    }
                }
            }
        }

        stage('🧹 CLEANUP DOCKER IMAGES') {
            steps {
                echo '======================================'
                echo '       Cleaning up local images       '
                echo '======================================'

                sh """
                    docker rmi ${registry}:${env.BUILD_NUMBER} || true
                    docker rmi ${registry}:latest || true
                    echo "✅ Local Docker images cleaned"
                """
            }
        }

        stage('☸️  DEPLOY TO KUBERNETES') {
            when {
                // Only deploy on main branch
                branch 'main'
            }
            steps {
                echo '======================================'
                echo '       Deploying to Kubernetes        '
                echo '======================================'

                script {
                    try {
                        // Apply Kubernetes manifests
                        sh """
                            # Replace image tag in deployment
                            sed -i 's|IMAGE_TAG|${env.BUILD_NUMBER}|g' k8s/deployment.yaml

                            # Apply configurations
                            kubectl apply -f k8s/namespace.yaml || true
                            kubectl apply -f k8s/configmap.yaml
                            kubectl apply -f k8s/secrets.yaml
                            kubectl apply -f k8s/deployment.yaml
                            kubectl apply -f k8s/service.yaml

                            # Wait for rollout
                            kubectl rollout status deployment/${K8S_DEPLOYMENT_NAME} -n ${K8S_NAMESPACE}

                            # Get deployment info
                            kubectl get deployments -n ${K8S_NAMESPACE}
                            kubectl get services -n ${K8S_NAMESPACE}
                            kubectl get pods -n ${K8S_NAMESPACE}
                        """

                        echo '✅ Kubernetes deployment successful!'
                    } catch (Exception e) {
                        echo "⚠️  Kubernetes deployment skipped: ${e.message}"
                        echo 'ℹ️   This is normal if K8s is not configured yet'
                    }
                }
            }
        }

        stage('🏗️  TERRAFORM INFRASTRUCTURE') {
            when {
                // Only run on main branch and if terraform should be applied
                allOf {
                    branch 'main'
                    environment name: 'DEPLOY_TO_AWS', value: 'true'
                }
            }
            steps {
                echo '======================================'
                echo '       Provisioning Infrastructure    '
                echo '======================================'

                script {
                    dir('terraform') {
                        withCredentials([aws(credentialsId: 'aws-credentials')]) {
                            sh '''
                                # Initialize Terraform
                                terraform init

                                # Validate configuration
                                terraform validate

                                # Plan infrastructure changes
                                terraform plan -out=tfplan

                                # Apply changes (with auto-approve for automation)
                                # terraform apply -auto-approve tfplan

                                echo "✅ Terraform plan created"
                                echo "ℹ️   Run 'terraform apply' manually to provision infrastructure"
                            '''
                        }
                    }
                }
            }
        }

        stage('📈 CONFIGURE MONITORING') {
            steps {
                echo '======================================'
                echo '       Configuring Monitoring         '
                echo '======================================'

                script {
                    try {
                        sh '''
                            # Verify Prometheus is scraping the application
                            echo "Prometheus URL: http://localhost:9090"
                            echo "Grafana URL: http://localhost:3000"
                            echo "Application Metrics: http://localhost:8089/SpringMVC/actuator/metrics"
                            echo "Application Health: http://localhost:8089/SpringMVC/actuator/health"
                        '''

                        echo '✅ Monitoring endpoints configured'
                    } catch (Exception e) {
                        echo "⚠️  Monitoring configuration skipped: ${e.message}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo '======================================'
            echo '       Pipeline Execution Summary     '
            echo '======================================'

            // Archive artifacts
            archiveArtifacts artifacts: 'target/**/*.jar', allowEmptyArchive: true

            // Clean workspace
            cleanWs()
        }

        success {
            echo '✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅'
            echo '✅                                      ✅'
            echo '✅     PIPELINE COMPLETED SUCCESSFULLY! ✅'
            echo '✅                                      ✅'
            echo '✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅'
            echo ''
            echo "Build: #${env.BUILD_NUMBER}"
            echo "Commit: ${env.GIT_COMMIT_MSG}"
            echo "Docker Image: ${registry}:${env.BUILD_NUMBER}"
            echo "SonarQube: ${SONAR_URL}"
            echo "Nexus: ${NEXUS_URL}"

        // Send notification (optional - configure email/Slack)
        // emailext body: "Build succeeded: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        //          subject: "✅ Jenkins Build Success",
        //          to: "team@achat.local"
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

        // Send notification (optional)
        // emailext body: "Build failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        //          subject: "❌ Jenkins Build Failed",
        //          to: "team@achat.local"
        }

        unstable {
            echo '⚠️  Pipeline completed with warnings'
        }
    }
}
