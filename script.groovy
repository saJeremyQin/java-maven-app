def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
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