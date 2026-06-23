def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
    echo "building the docker image of version ${params.VERSION}..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "docker build -t jeremyqindevops/demo-app:${params.VERSION} ."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push jeremyqindevops/demo-app:${params.VERSION}"
    }
} 

def deployApp() {
    echo "deploying the application of version ${params.VERSION}..."
} 

return this