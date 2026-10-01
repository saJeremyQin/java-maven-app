def gv

pipeline {
    agent any
    tools {
        maven "maven-3.9"
    }

    stages {
        stage("init") {
            steps {
                script {
                    scmSkip(deleteBuild: true, skipPattern: '.*\\[skip-ci\\].*')
                    gv = load "script.groovy"
                    env.ACTIVE_BRANCH = (env.BRANCH_NAME ?: env.GIT_BRANCH ?: env.CHANGE_BRANCH ?: "")
                        .replaceFirst(/^origin\//, "")
                }
            }
        }
        stage("increment build number") {
            steps {
                script {
                    echo "incrementing build number"
                    gv.incrementBuildNumber()
                }
            }
        }
        stage("build jar") {

            steps {
                script {
                    // echo "building jar"
                    gv.buildJar()
                    echo "Executing pipeline for branch ${env.ACTIVE_BRANCH}"
                }
            }
        }
        stage("buildImage") {
            when {
                expression {
                    return env.ACTIVE_BRANCH == "feature/terraform" || env.ACTIVE_BRANCH == "master"
                }
            }
            steps {
                script {
                    // echo "building image"
                    gv.buildImage()
                }
            }
        }

        stage("provision") {
            when {
                expression {
                    return env.ACTIVE_BRANCH == "feature/terraform" || env.ACTIVE_BRANCH == "master"
                }
            }
            steps {
                withCredentials([
                    string(credentialsId: 'aws_access_key_id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'aws_secret_access_key', variable: 'AWS_SECRET_ACCESS_KEY'),
                ]) {
                        dir("terraform") {
                            sh '''
                                set -e
                                terraform init
                                terraform plan -var-file="terraform.tfvars"
                                terraform apply -var-file="terraform.tfvars" -auto-approve
                            '''
                            script {
                                env.PUBLIC_EC2_IP = sh(script: 'terraform output -raw public_ec2_ip', returnStdout: true).trim()
                                echo "EC2 Instance IP: ${env.PUBLIC_EC2_IP}"
                            }
                    }
                }
            }
        }

        stage("deploy") {
            when {
                expression {
                    return env.ACTIVE_BRANCH == "feature/terraform" || env.ACTIVE_BRANCH == "master"
                }
            }
            steps {
                script {
                    echo "deploying docker image to EC2 instance..."
                    gv.deployApp()
                }
            }
        }
        stage("commit back to git") {
            when {
                expression {
                    return env.ACTIVE_BRANCH == "feature/terraform" || env.ACTIVE_BRANCH == "master"
                }
            }
            steps {
                script {
                    // echo "committing back to git"
                    gv.commitBackToGit()
                }
            }
        }
    }   
}

