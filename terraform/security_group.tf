

resource "aws_security_group" "maven_app_sg" {
  name        = "${var.environment}-app-sg"
  description = "Access for the application instance"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH from the Jenkins public IP"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.jenkins_ip, var.my_ip]
  }

  ingress {
    description = "Application traffic"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}