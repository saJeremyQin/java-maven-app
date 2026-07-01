def incrementBuildNumber() {
    echo "incrementing the build number..."
    sh '''
        mvn build-helper:parse-version versions:set \
        -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} \
        versions:commit
    '''
    def version = sh(
        script: 'mvn -q -DforceStdout help:evaluate -Dexpression=project.version',
        returnStdout: true
    ).trim()
    env.IMAGE_NAME = "${version}-${env.BUILD_NUMBER}"
}

def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
}

def buildImage() {
    def imageName = env.IMAGE_NAME ?: "latest"
    echo "building the docker image of version ${imageName}..."
    withCredentials([
        usernamePassword(
            credentialsId: 'docker-hub-credentials',
            passwordVariable: 'PASS',
            usernameVariable: 'USER'
        )
    ]) {
        sh "docker build -t jeremyqindevops/demo-app:$imageName ."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push jeremyqindevops/demo-app:$imageName"
    }
} 

def deployApp() {
    echo 'deploying the application...'
} 

return this