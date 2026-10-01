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
        withEnv(["IMAGE_TAG=${imageTag}"]) {
            sh '''
                set -eu
                set +x
                printf '%s' "$PASS" | docker login -u "$USER" --password-stdin

                docker buildx ls | grep -q "multiarch" || docker buildx create --name multiarch --use
                docker buildx inspect --bootstrap
                docker buildx build --platform linux/amd64,linux/arm64 -t "$IMAGE_TAG" --push .
            '''
        }
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
    def ec2Instance="ec2-user@${env.PUBLIC_EC2_IP}"
    withCredentials([
        sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'KEY_FILE'),
        usernamePassword(
            credentialsId: 'docker-hub-credentials',
            passwordVariable: 'PASS',
            usernameVariable: 'USER'
        )
    ]) {
        withEnv(["EC2_INSTANCE=${ec2Instance}", "IMAGE_TAG=${fullImageName}"]) {
            sh '''
                set -e
                set +x

                ssh_ec2() {
                    ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
                        -o ConnectTimeout=10 -i "$KEY_FILE" "$EC2_INSTANCE" "$@"
                }
                scp_ec2() {
                    scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null \
                        -i "$KEY_FILE" "$@"
                }

                ready=0
                attempt=1
                while [ "$attempt" -le 30 ]; do
                    if ssh_ec2 'sudo cloud-init status --wait && docker compose version'; then
                        ready=1
                        break
                    fi
                    echo "Waiting for EC2 initialization (attempt $attempt/30)..."
                    sleep 10
                    attempt=$((attempt + 1))
                done

                if [ "$ready" -ne 1 ]; then
                    echo "EC2 SSH did not become ready within five minutes." >&2
                    exit 1
                fi

                scp_ec2 server-cmds.sh docker-compose.yaml "$EC2_INSTANCE:~"
                ssh_ec2 'chmod +x ~/server-cmds.sh'

                {
                    printf '%s\n' "$USER"
                    printf '%s' "$PASS"
                } | ssh_ec2 'IFS= read -r USER; docker login --username "$USER" --password-stdin'

                printf '%s\n' "$IMAGE_TAG" | ssh_ec2 'IFS= read -r IMAGE; export IMAGE; trap "docker logout >/dev/null 2>&1 || true" EXIT; bash ~/server-cmds.sh'
            '''
        }
    }
} 

return this