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
    echo "test webhook setting for this project"
    sh 'mvn clean package'
}

def buildImage() {
    def imageName = env.IMAGE_NAME ?: "latest"
    def imageTag = "jeremyqindevops/demo-app:${imageName}"
    echo "building the docker image of version ${imageName}..."
    withCredentials([
        usernamePassword(
            credentialsId: 'docker-hub-credentials',
            passwordVariable: 'PASS',
            usernameVariable: 'USER'
        )
    ]) {
        sh "docker build -t ${imageTag} ."
        sh 'echo "$PASS" | docker login -u "$USER" --password-stdin'
        sh "docker push ${imageTag}"
    }
} 

def commitBackToGit() {

    echo "committing back to git, in the function..."
    sh "git config user.email 'jenkins-bot@local'"
    sh 'git config user.name "jenkins-bot"'
    withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')
    ]) {
        sh '''
            git add .
            git commit -m "Increment build number" || echo "No changes to commit"
            git push "https://x-access-token:${GITHUB_TOKEN}@github.com/saJeremyQin/java-maven-app.git" HEAD:jenkins
        '''
    }
}

def deployApp() {
    echo 'deploying the application...'
} 

return this