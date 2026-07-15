def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
} 

def bumpPomVersionAndPush() {
    echo "bumping pom version using Jenkins build number..."

    def currentVersion = sh(
        script: 'mvn -q -DforceStdout help:evaluate -Dexpression=project.version',
        returnStdout: true
    ).trim()

    def lastDot = currentVersion.lastIndexOf('.')
    if (lastDot <= 0) {
        error("Unsupported project version format: ${currentVersion}. Expected x.y.z")
    }

    def versionPrefix = currentVersion.substring(0, lastDot)
    def newVersion = "${versionPrefix}.${env.BUILD_NUMBER}"

    if (currentVersion == newVersion) {
        echo "version already set to ${newVersion}"
        return newVersion
    }

    sh "mvn -B versions:set -DnewVersion=${newVersion} -DgenerateBackupPoms=false"

    sh '''
        git config user.name "jenkins-bot"
        git config user.email "jenkins-bot@local"
    '''

    sh 'git add pom.xml'
    sh "git commit -m 'ci: bump version to ${newVersion} [skip ci]' || true"
    sh "git push origin HEAD:${env.BRANCH_NAME}"

    return newVersion
}

def buildImage() {
    // echo "building the docker image of version ${params.VERSION}..."
    withCredentials([
        usernamePassword(
            credentialsId: 'docker-hub-credentials',
            passwordVariable: 'PASS',
            usernameVariable: 'USER'
        )
    ]) {
        sh """
            echo "$PASS" | docker login -u "$USER" --password-stdin

            docker buildx ls | grep -q '^multiarch ' || docker buildx create --name multiarch
            docker buildx use multiarch
            docker buildx inspect --bootstrap
            
            docker buildx build \
            --platform linux/amd64,linux/arm64 \
            -t ${env.IMAGE_NAME} \
            --push .
        """
    }
} 

def deployApp() {
    echo "deploying the application of version."
}
return this