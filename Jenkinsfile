def gv

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }

    stages {
        // stage("init") {
        //     steps {
        //         script {
        //             gv = load "script.groovy"
        //         }
        //     }
        // }
        stage("test") {

            steps {
                script {
                    echo "Testing the application"
                    }
            }
        }
        stage("build") {
         
            steps {
                script {
                    echo "building the application"
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    echo "deploying the application"
                    def dockerCmd = "docker run -d -p 3080:3080 jeremyqindevops/demo-app:1.0"
                    withCredentials([sshUserPrivateKey(
                        credentialsId: 'ec2-ssh-key', 
                        keyFileVariable: 'KEY_FILE', 
                        usernameVariable: 'SSH_USER')]) {

                            sh """
                                chmod 400 $KEY_FILE
                                ssh -o StrictHostKeyChecking=no -i $KEY_FILE $SSH_USER@3.26.224.83 ${dockerCmd}
                            """       
                    }
                }
            }
        }
    }   
}