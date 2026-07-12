def buildJar() {
    echo "building the application..."
    sh 'mvn clean package'
} 

def buildImage() {
    // echo "building the docker image of version ${params.VERSION}..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "docker build -t ${env.IMAGE_NAME} ."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push ${env.IMAGE_NAME}"
    }
} 

def deployApp() {
    echo "deploying the application of version."
}
return this