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
    echo "resolved IMAGE_NAME=${env.IMAGE_NAME}"
}

def buildJar() {
    echo "building the application..."
    echo "test webhook setting for this project"
    sh 'mvn clean package'
}

def buildImage() {
    def imageTag = "jeremyqindevops/java-maven-app:${env.IMAGE_NAME ?: 'latest'}"
    echo "building and pushing docker image ${imageTag}..."
    withCredentials([
        usernamePassword(
            credentialsId: 'docker-hub-credentials',
            passwordVariable: 'PASS',
            usernameVariable: 'USER'
        )
    ]) {
            sh """
                echo "$PASS" | docker login -u "$USER" --password-stdin

                docker buildx ls | grep -q "multiarch" || docker buildx create --name multiarch --use
                docker buildx inspect --bootstrap
                docker buildx build --platform linux/amd64,linux/arm64 -t ${imageTag} --push .
            """
   
    }
} 

def commitBackToGit() {

    echo "committing back to git, in the function..."
    sh "git config user.email 'jenkins-bot@local'"
    sh 'git config user.name "jenkins-bot"'
    withCredentials([string(credentialsId: 'github-secret-text', variable: 'GITHUB_TOKEN')
    ]) {
        sh '''
            git add .
            git commit -m "Increment build number [skip-ci]" || echo "No changes to commit"
            git push "https://x-access-token:${GITHUB_TOKEN}@github.com/saJeremyQin/java-maven-app.git" HEAD:${ACTIVE_BRANCH}
        '''
    }
}

def deployApp() {
    echo "deploying the application..."
    def fullImageName = "jeremyqindevops/java-maven-app:${env.IMAGE_NAME ?: 'latest'}"
    def ec2Instance="ec2-user@13.211.213.171"
    withCredentials([sshUserPrivateKey(
        credentialsId: 'ec2-ssh-key', 
        keyFileVariable: 'KEY_FILE')]) {
            sh """
                set -e
                SCP_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i \$KEY_FILE"
                SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i \$KEY_FILE"

                scp \$SCP_OPTS server-cmds.sh docker-compose.yaml ${ec2Instance}:~
                ssh \$SSH_OPTS ${ec2Instance} 'chmod +x ~/server-cmds.sh'
                ssh \$SSH_OPTS ${ec2Instance} 'bash ~/server-cmds.sh ${fullImageName}'
            """
    }       
} 

return this