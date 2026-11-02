pipeline {
  //Part 1
  agent any 
   options {
    timeout(time: 30, unit: 'MINUTES')
            timestamps()
   }

  parameters {
    string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'the branch name to checkout')
    choice(name: 'BUILD_ENV', choices: ['dev', 'staging', 'prod'], description: 'The target environment')
  }

            //part 2
            stages {
              stage('checkout') {
                steps {
                  checkout scm(
                    branches: [[name: "${params.BRANCH.NAME}"]],
                    userRemoteConfigs: [[url: 'https://github.com']]
                    )
                }
              }

              stage('precheck') {
                steps {
                  echo "The pipeline is currently targetting the environment: ${params.BUILD_ENV}"
                }
              }

            stage('BUild') {
              steps {
                sh 'mvn clean package'
              }
            }
            }

            // Part 3
            post {
              success {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
              }
              failure {
                deleteDir()
              }
            }
            }
}
            
  
